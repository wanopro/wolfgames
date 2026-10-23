package com.wolfgames.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.i18n.Strings
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * 원형 화면 분할: 화면 가운데를 중심으로 n명이 같은 넓이로 둘러앉은 모양.
 * P1 = 아래 가운데, 시계방향(아래 → 왼쪽 → 위 → 오른쪽)으로 P2, P3… (웹 split.js 와 같음)
 */
class Sector(val poly: List<Pair<Float, Float>>, val rot: Float, val ax: Float, val ay: Float, val rin: Float)

fun sectors(w: Float, h: Float, n: Int): List<Sector> {
    val cx = w / 2; val cy = h / 2
    val path = listOf(w / 2 to h, 0f to h, 0f to 0f, w to 0f, w to h, w / 2 to h)
    data class Seg(val x0: Float, val y0: Float, val x1: Float, val y1: Float, val a: Float, val from: Float)
    val seg = ArrayList<Seg>(); var tot = 0f
    for (i in 0 until path.size - 1) {
        val (x0, y0) = path[i]; val (x1, y1) = path[i + 1]
        val a = abs((x0 - cx) * (y1 - cy) - (x1 - cx) * (y0 - cy)) / 2
        seg.add(Seg(x0, y0, x1, y1, a, tot)); tot += a
    }
    fun at(a0: Float): Pair<Float, Float> {
        val a = ((a0 % tot) + tot) % tot
        for (s in seg) if (a <= s.from + s.a + 1e-4f) { val u = if (s.a > 0) (a - s.from) / s.a else 0f; return (s.x0 + (s.x1 - s.x0) * u) to (s.y0 + (s.y1 - s.y0) * u) }
        return path[0]
    }
    val verts = ArrayList<Pair<Float, Pair<Float, Float>>>()
    for (lap in -1..1) for (s in seg) verts.add((s.from + s.a + lap * tot) to (s.x1 to s.y1))
    return (0 until n).map { k ->
        val a0 = k * tot / n - tot / (2 * n); val a1 = k * tot / n + tot / (2 * n)
        val corners = verts.filter { it.first > a0 + 1e-3f && it.first < a1 - 1e-3f }.sortedBy { it.first }.map { it.second }
        val pts = listOf(cx to cy, at(a0)) + corners + listOf(at(a1))
        var area = 0f; var gx = 0f; var gy = 0f
        for (q in pts.indices) { val (x0, y0) = pts[q]; val (x1, y1) = pts[(q + 1) % pts.size]; val f = x0 * y1 - x1 * y0; area += f; gx += (x0 + x1) * f; gy += (y0 + y1) * f }
        gx /= 3 * area; gy /= 3 * area
        var rin = 1e9f
        for (q in pts.indices) {
            val (x0, y0) = pts[q]; val (x1, y1) = pts[(q + 1) % pts.size]; val dx = x1 - x0; val dy = y1 - y0
            val l2 = (dx * dx + dy * dy).takeIf { it > 0 } ?: 1f
            val u = (((gx - x0) * dx + (gy - y0) * dy) / l2).coerceIn(0f, 1f)
            rin = min(rin, hypot(gx - x0 - u * dx, gy - y0 - u * dy))
        }
        Sector(pts, atan2(gy - cy, gx - cx) - (PI / 2).toFloat(), gx, gy, rin)
    }
}

fun inPoly(x: Float, y: Float, poly: List<Pair<Float, Float>>): Boolean {
    var c = false; var j = poly.size - 1
    for (i in poly.indices) {
        val (xi, yi) = poly[i]; val (xj, yj) = poly[j]
        if ((yi > y) != (yj > y) && x < (xj - xi) * (y - yi) / (yj - yi) + xi) c = !c
        j = i
    }
    return c
}

fun Ctx.polyPath(poly: List<Pair<Float, Float>>) { beginPath(); poly.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(x, y) else lineTo(x, y) }; closePath() }

/** 영역 하나: 배경 칠하고, 그 사람 방향으로 돌린 좌표계에서 content(scale) */
fun Ctx.region(sec: Sector, fill: String, glow: String? = null, content: Ctx.(Float) -> Unit) {
    save(); polyPath(sec.poly); clip()
    fillStyle = fill; fill()
    if (glow != null) { lineWidth = 14f; strokeStyle = glow; polyPath(sec.poly); stroke() }
    translate(sec.ax, sec.ay); rotate(sec.rot)
    content(max(0.5f, min(1.05f, sec.rin / 118f)))
    restore()
}

fun Ctx.splitLines(secs: List<Sector>, w: Float, h: Float) {
    save(); strokeStyle = "rgba(0,0,0,.55)"; lineWidth = 4f
    for (s in secs) { beginPath(); moveTo(w / 2, h / 2); lineTo(s.poly[1].first, s.poly[1].second); stroke() }
    restore()
}

fun Ctx.text(str: String, x: Float, y: Float, size: Float, color: String, bold: Boolean = true) { font(size, bold); fillStyle = color; fillText(str, x, y) }

fun Ctx.badge(str: String, x: Float, y: Float, s: Float, bg: String, fg: String) {
    font(18 * s); val w = measureText(str) + 24 * s; val h = 34 * s
    beginPath(); roundRect(x - w / 2, y - h / 2, w, h, h / 2); fillStyle = bg; fill()
    text(str, x, y + 1, 18 * s, fg)
}

fun Ctx.hub(w: Float, h: Float, r: Float, fill: String, draw: (() -> Unit)? = null) {
    save(); beginPath(); arc(w / 2, h / 2, r, 0f, TAU); fillStyle = fill; fill()
    lineWidth = 5f; strokeStyle = "rgba(0,0,0,.6)"; stroke(); draw?.invoke(); restore()
}

/** 주사위 (웹 drawDie 와 같음) */
private val PIPS = mapOf(
    1 to listOf(0 to 0), 2 to listOf(-1 to -1, 1 to 1), 3 to listOf(-1 to -1, 0 to 0, 1 to 1),
    4 to listOf(-1 to -1, 1 to -1, -1 to 1, 1 to 1), 5 to listOf(-1 to -1, 1 to -1, 0 to 0, -1 to 1, 1 to 1),
    6 to listOf(-1 to -1, 1 to -1, -1 to 0, 1 to 0, -1 to 1, 1 to 1),
)
fun Ctx.die(x: Float, y: Float, size: Float, value: Int, border: String? = null) {
    val hs = size / 2; val r = size * 0.18f
    beginPath(); roundRect(x - hs, y - hs, size, size, r); fillStyle = "#fff"; fill()
    if (border != null) { lineWidth = size * 0.08f; strokeStyle = border; beginPath(); roundRect(x - hs + size * 0.04f, y - hs + size * 0.04f, size * 0.92f, size * 0.92f, r * 0.9f); stroke() }
    val o = size * 0.26f
    for ((dx, dy) in PIPS[value] ?: emptyList()) {
        beginPath(); arc(x + dx * o, y + dy * o, if (value == 1) size * 0.135f else size * 0.09f, 0f, TAU)
        fillStyle = if (value == 1) "#E53935" else "#1A1A1A"; fill()
    }
}

/** 분할 화면 게임: 매 프레임 draw, 영역 터치 tap, 가운데 원 터치 hub. t = 시작 후 초 */
interface SplitGame {
    val n: Int
    fun draw(ctx: Ctx, w: Float, h: Float, t: Float, secs: List<Sector>, s: Strings)
    fun tap(i: Int, t: Float)
    fun hub(t: Float) {}
}

val PLAYER_HEX = listOf("#FF5252", "#448AFF", "#69F0AE", "#FFD740", "#E040FB", "#18FFFF", "#FF6E40", "#B2FF59", "#FF80AB", "#8C9EFF")

/** 분할 화면 그리기 + 터치. 제목 줄은 GameScaffold 가 그림 */
@Composable
fun SplitCanvas(game: SplitGame, modifier: Modifier = Modifier.fillMaxSize()) {
    val s = LocalStrings.current
    val density = LocalDensity.current.density
    val t0 = remember(game) { System.nanoTime() }
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(game) { while (true) withFrameNanos { tick = it } }
    val holder = remember(game) { arrayOfNulls<List<Sector>>(1) }
    val sizeHolder = remember(game) { floatArrayOf(0f, 0f) }
    Canvas(
        modifier.pointerInput(game) {
            awaitPointerEventScope {
                while (true) {
                    val ev = awaitPointerEvent()
                    for (ch in ev.changes) if (ch.changedToDown()) {
                        val x = ch.position.x / density; val y = ch.position.y / density
                        val t = (System.nanoTime() - t0) / 1e9f
                        val w = sizeHolder[0]; val h = sizeHolder[1]
                        if (hypot(x - w / 2, y - h / 2) < 48f) game.hub(t)
                        else holder[0]?.indexOfFirst { inPoly(x, y, it.poly) }?.takeIf { it >= 0 }?.let { game.tap(it, t) }
                        ch.consume()
                    }
                }
            }
        },
    ) {
        tick.let { }
        val w = size.width / density; val h = size.height / density
        if (w != sizeHolder[0] || h != sizeHolder[1] || holder[0] == null) { sizeHolder[0] = w; sizeHolder[1] = h; holder[0] = sectors(w, h, game.n) }
        drawIntoCanvas { cc ->
            val ctx = Ctx(cc.nativeCanvas, density)
            ctx.fillStyle = "#12121A"; ctx.fillRect(0f, 0f, w, h)
            game.draw(ctx, w, h, (System.nanoTime() - t0) / 1e9f, holder[0]!!, s)
            ctx.finish()
        }
    }
}
