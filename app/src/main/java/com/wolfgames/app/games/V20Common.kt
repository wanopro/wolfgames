package com.wolfgames.app.games

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
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.wolfgames.app.ui.Ctx
import com.wolfgames.app.ui.text
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

// v2.0 게임 공통 (웹 games-v20.js 맨 위 함수들과 같음)
// Beep.tone 종류: 0 sine · 1 square · 2 sawtooth · 3 triangle

internal const val V20_TAU = 7f
internal fun v20Ease(u0: Float): Float { val u = u0.coerceIn(0f, 1f); return if (u < 0.5f) 2 * u * u else 1 - (-2 * u + 2).pow(2) / 2 }
internal fun v20EaseOut(u0: Float): Float { val u = u0.coerceIn(0f, 1f); return 1 - (1 - u).pow(3) }

/** 글자가 maxW 보다 넓으면 줄여서 */
internal fun Ctx.fitText(s: String, x: Float, y: Float, size: Float, color: String, maxW: Float) {
    font(size); val w = measureText(s); text(s, x, y, if (w > maxW) size * maxW / w else size, color)
}

/** 사람 점 줄 (지금 차례 밝게). extra(i, x, y) 로 점 아래에 더 그리기 */
internal fun Ctx.turnDots(w: Float, n: Int, cur: Int, y: Float, extra: ((Int, Float, Float) -> Unit)? = null) {
    val r = min(14f, w / (n * 3.2f))
    for (i in 0 until n) {
        val x = w / 2 + (i - (n - 1) / 2f) * r * 2.8f
        globalAlpha = if (i == cur) 1f else 0.35f
        beginPath(); arc(x, y, r * (if (i == cur) 1.15f else 1f), 0f, V20_TAU); fillStyle = YB_COLS[i]; fill()
        text("${i + 1}", x, y + 1, r * 0.95f, "#000"); globalAlpha = 1f
        extra?.invoke(i, x, y + r + 12)
    }
}

/** 결과 카드 (가운데 크게, 흔들리며 등장). v = 나온 뒤 지난 초 */
internal fun Ctx.resultCard(w: Float, h: Float, v: Float, title: String, line: String, col: String = "#FF5A6E", cy: Float = h / 2) {
    val k = (0.6f + 0.4f * min(1f, v / 0.25f)) * min(1f, w / 380f)
    val sh = if (v < 0.4f) sin(v * 60) * 7 * (1 - v / 0.4f) else 0f
    save(); translate(w / 2 + sh, cy); scale(k, k)
    beginPath(); roundRect(-165f, -82f, 330f, 164f, 26f); fillStyle = "rgba(12,12,18,.94)"; fill(); strokeStyle = col; lineWidth = 4f; stroke()
    fitText(title, 0f, -34f, 40f, col, 300f); fitText(line, 0f, 30f, 34f, "#FFD740", 300f)
    restore()
}

internal const val V20_PI = PI.toFloat()

/** Anim18 과 같지만 누른 채로 움직이기(onMove x,y,w,h)와 손 떼기·취소(onUp t)도 받음 */
@Composable
internal fun AnimPtr(
    key: Any,
    onDown: (Float, Float, Float, Float, Float) -> Unit,
    onMove: (Float, Float, Float, Float) -> Unit,
    onUp: (Float) -> Unit = {},
    draw: (Ctx, Float, Float, Float) -> Unit,
) {
    val density = LocalDensity.current.density
    val t0 = remember(key) { System.nanoTime() }
    val now = { (System.nanoTime() - t0) / 1e9f }
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(key) { while (true) withFrameNanos { tick = it } }
    Canvas(Modifier.fillMaxSize().pointerInput(key) {
        awaitPointerEventScope {
            while (true) {
                val ev = awaitPointerEvent()
                for (ch in ev.changes) {
                    val x = ch.position.x / density; val y = ch.position.y / density
                    val w = size.width / density; val h = size.height / density
                    if (ch.changedToDown()) { onDown(x, y, now(), w, h); ch.consume() }
                    else if (ch.changedToUp() || (ch.previousPressed && !ch.pressed)) { onUp(now()); ch.consume() }
                    else if (ch.pressed) { onMove(x, y, w, h); ch.consume() }
                }
            }
        }
    }) {
        tick.let { }
        val w = size.width / density; val h = size.height / density
        drawIntoCanvas { cc -> val c = Ctx(cc.nativeCanvas, density); draw(c, w, h, now()); c.finish() }
    }
}
