package com.wolfgames.app.games

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.i18n.SEP
import com.wolfgames.app.i18n.Strings
import com.wolfgames.app.i18n.cat
import com.wolfgames.app.ui.Ctx
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.TAU
import com.wolfgames.app.ui.Vibe
import com.wolfgames.app.ui.coin
import com.wolfgames.app.ui.text
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

// v1.6 새 게임 (웹 games-v16.js 와 같은 규칙): 플링코(🍀 운빨) / 삼목 · 오목(⚔️ 대결)

private const val GOLD = "#FFD740"
private const val RED = "#FF3D5A"
private const val PIf = PI.toFloat()
private fun ease(u: Float) = 1f - (1f - u.coerceIn(0f, 1f)).pow(3)

/** 설정 화면 → 게임 화면 */
@Composable
private fun SetupThen16(title: String, onBack: () -> Unit, desc: String, game: @Composable () -> Unit) {
    var started by rememberSaveable { mutableStateOf(com.wolfgames.app.DebugOpen.autostart) }
    if (!started) GameScaffold(title, onBack) { SetupPanel(desc, { started = true }) {} }
    else { BackHandler { started = false }; GameScaffold(title, onBack = { started = false }) { game() } }
}

/** 한 화면 게임: 매 프레임 그리기 + 누른 곳 */
private interface PadGame {
    fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings)
    fun down(x: Float, y: Float, t: Float, w: Float, h: Float)
}

@Composable
private fun PadCanvas(game: PadGame) {
    val s = LocalStrings.current
    val density = LocalDensity.current.density
    val t0 = remember(game) { System.nanoTime() }
    val now = { (System.nanoTime() - t0) / 1e9f }
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(game) { while (true) withFrameNanos { tick = it } }
    Canvas(
        Modifier.fillMaxSize().pointerInput(game) {
            awaitPointerEventScope {
                while (true) {
                    val ev = awaitPointerEvent()
                    for (ch in ev.changes) if (ch.changedToDown()) {
                        game.down(ch.position.x / density, ch.position.y / density, now(), size.width / density, size.height / density); ch.consume()
                    }
                }
            }
        },
    ) {
        tick.let { }
        val w = size.width / density; val h = size.height / density
        drawIntoCanvas { cc -> val c = Ctx(cc.nativeCanvas, density); c.fillStyle = "#12121A"; c.fillRect(0f, 0f, w, h); game.draw(c, w, h, now(), s); c.finish() }
    }
}

/** 좁은 칸 안에 글자 맞추기 (단어 단위 줄바꿈 + 크기 줄이기). 두 언어면 아래에 흐리게 */
private fun Ctx.fitLabel(str: String, cx: Float, cy: Float, maxW: Float, size: Float, color: String) {
    val parts = str.split(SEP)
    fun wrap(p: String, sz: Float): List<String> {
        font(sz, true)
        val out = ArrayList<String>(); var cur = ""
        for (wd in p.split(Regex("\\s+")).filter { it.isNotEmpty() }) { val tr = if (cur.isEmpty()) wd else "$cur $wd"; if (measureText(tr) <= maxW || cur.isEmpty()) cur = tr else { out.add(cur); cur = wd } }
        if (cur.isNotEmpty()) out.add(cur)
        return out
    }
    var sz = size; var rows: List<List<String>>
    while (true) {
        rows = parts.map { wrap(it, sz) }
        font(sz, true)
        val wide = rows.flatten().any { measureText(it) > maxW }
        if ((!wide && rows.flatten().size <= 4) || sz <= 8f) break
        sz -= 0.5f
    }
    val all = rows.flatMapIndexed { k, r -> r.map { it to k } }; val lh = sz * 1.15f
    val al = globalAlpha
    all.forEachIndexed { j, (ln, k) -> globalAlpha = if (k > 0) al * 0.75f else al; text(ln, cx, cy + (j - (all.size - 1) / 2f) * lh, sz, color) }
    globalAlpha = al
}

/** 위·아래 양쪽에서 읽히는 안내 글자 (위쪽은 뒤집어서) */
private fun Ctx.bothSides(w: Float, h: Float, str: String, size: Float, color: String, pad: Float = 26f) {
    val off = if (SEP in str) size * 0.5f else 0f
    text(str, w / 2, h - pad - off, size, color)
    save(); translate(w / 2, pad + off); rotate(PIf); text(str, 0f, 0f, size, color); restore()
}

/** 위·아래 사람 각자 자기 쪽 글자 (msgs[0] 아래 사람, msgs[1] 위 사람 · 위쪽은 뒤집어서) */
private fun Ctx.eachSide(w: Float, h: Float, msgs: List<String>, size: Float, cols: List<String>, pad: Float = 26f) {
    msgs.forEachIndexed { i, m ->
        // 자리가 넉넉해서 두 언어도 원래 크기 그대로 두 줄
        val parts = m.split(SEP)
        var sz = size; while (sz > 10 && parts.maxOf { font(sz); measureText(it) } > w - 72) sz -= 0.5f
        val off = if (parts.size > 1) sz * 0.6f else 0f
        save(); if (i == 1) { translate(w, h); rotate(PIf) }
        val yc = h - pad - off
        if (parts.size > 1) { text(parts[0], w / 2, yc - sz * 0.58f, sz, cols[i]); val al = globalAlpha; globalAlpha = al * 0.85f; text(parts[1], w / 2, yc + sz * 0.58f, sz, cols[i]); globalAlpha = al }
        else text(m, w / 2, yc, sz, cols[i])
        restore()
    }
}

/** 가운데 "다시 하기" 알약 */
private fun Ctx.againPill(w: Float, h: Float, s: Strings) {
    fillStyle = "rgba(0,0,0,.6)"; beginPath(); roundRect(w / 2 - 110, h / 2 - 22, 220f, 44f, 22f); fill()
    fitLabel(s.playAgain, w / 2, h / 2, 200f, 17f, "#fff")
}

// ===================== 🔻 플링코 =====================
val PLINKO_ICONS = listOf("🍺", "🥃", "🔄", "👉", "👈", "🍻", "✅")
private val PLINKO_COLS = listOf("#FF7043", "#29B6F6", "#78909C", "#AB47BC", "#EC407A", "#FFB300", "#43A047")
private const val ROWS = 11
private const val CR = 12f
private const val PR = 5f

private class Board(val pegs: List<FloatArray>, val sw: Float, val slotTop: Float, val slotBot: Float, val n: Int, val top: Float, val dropY: Float)
private fun board(w: Float, h: Float): Board {
    val n = PLINKO_ICONS.size; val sw = (w - 24) / n; val slotBot = h - 14; val slotH = min(118f, max(92f, h * 0.17f)); val slotTop = slotBot - slotH
    val top = min(150f, max(96f, h * 0.17f)); val gapY = (slotTop - 26 - top) / (ROWS - 1); val pegs = ArrayList<FloatArray>()
    for (r in 0 until ROWS) { if (r % 2 == 1) for (i in 0..n) pegs.add(floatArrayOf(12 + sw * i, top + r * gapY)) else for (i in 0 until n) pegs.add(floatArrayOf(12 + sw * (i + 0.5f), top + r * gapY)) }
    return Board(pegs, sw, slotTop, slotBot, n, top, top - min(48f, gapY * 1.4f))
}
private class Sim(val path: List<FloatArray>, val slot: Int)
/** 떨어뜨리기 물리 (웹과 같은 계산) */
private fun simulate(w: Float, h: Float, x0: Float, seed: Long): Sim {
    val b = board(w, h); var sd = seed; val rnd = { sd = (sd * 16807) % 2147483647; sd / 2147483647f }
    var x = x0.coerceIn(12 + CR, w - 12 - CR); var y = b.dropY; var vx = (rnd() - 0.5f) * 30; var vy = 0f
    val path = ArrayList<FloatArray>(); val dt = 1f / 240; var lastY = y
    for (k in 0 until 240 * 14) {
        vy += 1500 * dt; x += vx * dt; y += vy * dt
        for (pg in b.pegs) {
            val px = pg[0]; val py = pg[1]; val dx = x - px; val dy = y - py; val d = hypot(dx, dy)
            if (d < CR + PR && d > 0) {
                var nx = dx / d; var ny = dy / d
                val out = if (px < w / 2) -1f else 1f; val wallDist = abs(px - (if (out < 0) 12f else w - 12))
                // 벽 옆 좁은 틈: 안쪽으로 굴러가게
                if (wallDist < b.sw * 0.6f && dx * out > 0 && wallDist > 1) { nx = -out * 0.6f; ny = -0.8f; vx = -out * max(80f, abs(vx)) }
                x = px + nx * (CR + PR); y = py + ny * (CR + PR)
                val vn = vx * nx + vy * ny
                if (vn < 0) { vx -= 1.55f * vn * nx; vy -= 1.55f * vn * ny; vx += (rnd() - 0.5f) * 40 }
            }
        }
        if (x < 12 + CR) { x = 12 + CR; vx = abs(vx) * 0.5f }
        if (x > w - 12 - CR) { x = w - 12 - CR; vx = -abs(vx) * 0.5f }
        if (y > b.slotTop) {
            val i = floor((x - 12) / b.sw).toInt().coerceIn(0, b.n - 1); val l = 12 + i * b.sw; val r = l + b.sw
            if (x - CR < l + 2) { x = l + 2 + CR; vx = abs(vx) * 0.4f }
            if (x + CR > r - 2) { x = r - 2 - CR; vx = -abs(vx) * 0.4f }
        }
        if (y > b.slotBot - CR) { y = b.slotBot - CR; vy = -abs(vy) * 0.25f; vx *= 0.6f; if (abs(vy) < 80) { vy = 0f; vx = 0f } }
        if (k % 60 == 0) { if (k > 0 && abs(y - lastY) < 3 && y < b.slotBot - CR - 1) { vx += (if (x < w / 2) 1 else -1) * 160f; vy = -60f }; lastY = y }
        if (k % 4 == 0) path.add(floatArrayOf(x, y))
        if (y >= b.slotBot - CR - 0.5f && vy == 0f) { path.add(floatArrayOf(x, y)); break }
    }
    return Sim(path, floor((x - 12) / b.sw).toInt().coerceIn(0, b.n - 1))
}

private class PlinkoGame(val vibe: Vibe) : PadGame {
    var phase = "ready"; var order = (0 until 7).shuffled(); var prev: List<Int>? = null; var at = 0f
    var dropX = 0f; var dropAt = 0f; var sim: Sim? = null; var buzz = false
    fun startShuffle(t: Float) { prev = order; var nw = order.shuffled(); if (nw == order) nw = nw.reversed(); order = nw; phase = "shuffle"; at = t; sim = null; vibe.tick() }
    override fun down(x: Float, y: Float, t: Float, w: Float, h: Float) {
        val b = board(w, h)
        if (phase == "result" && t - at > 0.5f) { startShuffle(t); return }
        if (phase != "ready" || y > b.top - 6) return
        dropX = x; dropAt = t; sim = simulate(w, h, x, 1L + Random.nextInt(2147483000)); phase = "drop"; buzz = false; vibe.tap()
    }
    override fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings) = with(c) {
        val b = board(w, h); val labels = s.plinkoSlots.split('|')
        linearFill(0f, 0f, 0f, h, "#1E2433", "#141824"); beginPath(); roundRect(6f, 6f, w - 12, h - 12, 18f); fill(); lineWidth = 3f; strokeStyle = "#2E3650"; stroke()
        val sm = sim
        val u = if (sm != null) ((t - dropAt) * 60).toInt() else 0
        val landed = sm != null && u >= sm.path.size
        if (phase == "drop" && landed) { phase = "result"; at = t; if (!buzz) { buzz = true; vibe.strong() } }
        if (phase == "result" && t - at > 2.6f) startShuffle(t)
        if (phase == "shuffle" && t - at > 0.7f) { phase = "ready"; prev = null }
        val msg = when (phase) { "shuffle" -> s.plinkoShuffling; "ready" -> s.plinkoTap; else -> "" }
        if (msg.isNotEmpty()) fitLabel(msg, w / 2, max(30f, b.dropY - 34), w - 40, 15f, GOLD)
        if (phase == "ready") {
            lineDash = floatArrayOf(6f, 6f); strokeStyle = "rgba(255,255,255,.28)"; lineWidth = 2f; beginPath(); moveTo(18f, b.dropY); lineTo(w - 18, b.dropY); stroke(); lineDash = null
            coin(w / 2, b.dropY + sin(t * 4) * 4, CR, 1)
        }
        for (pg in b.pegs) { beginPath(); arc(pg[0], pg[1], PR, 0f, TAU); fillStyle = "#CFD8DC"; fill(); beginPath(); arc(pg[0] - 1.5f, pg[1] - 1.5f, 1.8f, 0f, TAU); fillStyle = "#fff"; fill() }
        val hit = if (sm != null && (phase == "result" || (phase == "drop" && landed))) sm.slot else -1
        val sh = if (phase == "shuffle") min(1f, (t - at) / 0.6f) else 1f
        for (i in 0 until b.n) {
            val pv = prev
            val si = if (phase == "shuffle" && sh < 0.5f && pv != null) pv[i] else order[i]; val col = PLINKO_COLS[si]
            val x = 12 + i * b.sw; val glow = i == hit && floor(t * 8).toInt() % 2 == 0
            val sq = if (phase == "shuffle") abs(cos(sh * PIf)) else 1f
            save(); translate(x + b.sw / 2, 0f); scale(max(0.05f, sq), 1f); translate(-(x + b.sw / 2), 0f)
            beginPath(); roundRect(x + 2, b.slotTop + 10, b.sw - 4, b.slotBot - b.slotTop - 10, 8f); fillStyle = if (glow) col else col + "44"; fill()
            if (i == hit) { lineWidth = 3f; strokeStyle = col; stroke() }
            text(PLINKO_ICONS[si], x + b.sw / 2, b.slotTop + 34, min(24f, b.sw * 0.5f), "#fff")
            fitLabel(labels.getOrElse(si) { "" }, x + b.sw / 2, (b.slotTop + 52 + b.slotBot) / 2, b.sw - 8, 12.5f, "#fff")
            restore()
            if (i > 0) { beginPath(); moveTo(x, b.slotTop); lineTo(x, b.slotBot); strokeStyle = "#CFD8DC"; lineWidth = 4f; lineCap = "round"; stroke() }
        }
        if (sm != null && phase != "shuffle") {
            val p = sm.path[min(u, sm.path.size - 1)]
            coin(p[0], p[1], CR, 1)
            if (u < 10) { beginPath(); arc(dropX, b.dropY, 14f + u * 3, 0f, TAU); strokeStyle = "rgba(255,255,255,${max(0f, 0.7f - u * 0.07f)})"; lineWidth = 3f; stroke() }
        }
        if (phase == "result" && hit >= 0) {
            val si = order[hit]; val col = PLINKO_COLS[si]; val pop = ease((t - at) / 0.25f); val lab = labels.getOrElse(si) { "" }
            val bw = min(w - 40, 300f); val bh = if (SEP in lab) 128f else 96f
            save(); translate(w / 2, h * 0.4f); scale(0.6f + 0.4f * pop, 0.6f + 0.4f * pop)
            beginPath(); roundRect(-bw / 2, -bh / 2, bw, bh, 22f); fillStyle = "rgba(0,0,0,.8)"; fill(); lineWidth = 4f; strokeStyle = col; stroke()
            text(PLINKO_ICONS[si], -bw / 2 + 38, 0f, 34f, "#fff")
            fitLabel(lab, 22f, 0f, bw - 96, 32f, col)
            restore()
        }
    }
}

@Composable
fun PlinkoScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current
    SetupThen16(s.plinkoTitle, onBack, s.plinkoDesc) { PadCanvas(remember { PlinkoGame(vibe) }) }
}

// ===================== 🟢🔴 삼목 =====================
private val TTT_LINES = listOf(intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8), intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8), intArrayOf(0, 4, 8), intArrayOf(2, 4, 6))
private val TTT_COL = listOf("#4CD964", "#FF5A5A")
const val TTT_SEC = 2f

private class TttGame(val vibe: Vibe) : PadGame {
    var starter = 0
    val cells = IntArray(9) { -1 }; val placed = FloatArray(9); val q = listOf(ArrayDeque<Int>(), ArrayDeque<Int>())
    var turn = 0; var turnAt = 0f; var win = -2; var line: IntArray? = null; var endAt = 0f; var timeUpAt = -9f; var tick = 9
    init { round(0f) }
    fun round(t: Float) { q[0].clear(); q[1].clear(); cells.fill(-1); placed.fill(0f); turn = starter; turnAt = t + 1.2f; win = -2; line = null; endAt = 0f; timeUpAt = -9f; tick = 9; starter = 1 - starter }
    fun geo(w: Float, h: Float): FloatArray { val sz = minOf(w - 40, h - 190, 420f); return floatArrayOf(sz, (w - sz) / 2, (h - sz) / 2, sz / 3) }
    fun finish(t: Float) {
        for (l in TTT_LINES) { val v = cells[l[0]]; if (v >= 0 && l.all { cells[it] == v }) { win = v; line = l; endAt = t; vibe.strong(); return } }
        if (cells.all { it >= 0 }) { win = -1; endAt = t; vibe.strong() }
    }
    override fun down(x: Float, y: Float, t: Float, w: Float, h: Float) {
        if (win > -2) { if (t - endAt > 0.8f) { round(t); vibe.tap() }; return }
        if (t < turnAt) return
        val (_, x0, y0, cell) = geo(w, h)
        val cx = floor((x - x0) / cell).toInt(); val cy = floor((y - y0) / cell).toInt()
        if (cx !in 0..2 || cy !in 0..2) return
        val k = cy * 3 + cx; if (cells[k] >= 0) return
        // 한 사람당 3개까지: 4번째를 놓으면 내 가장 오래된 코인이 사라짐
        if (q[turn].size >= 3) cells[q[turn].removeFirst()] = -1
        q[turn].addLast(k); cells[k] = turn; placed[k] = t; vibe.tap(); finish(t)
        if (win == -2) { turn = 1 - turn; turnAt = t; tick = 9 }
    }
    override fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings) = with(c) {
        val (sz, x0, y0, cell) = geo(w, h); val names = listOf(s.green, s.red)
        if (win == -2 && t >= turnAt && t - turnAt > TTT_SEC) { timeUpAt = t; turn = 1 - turn; turnAt = t; tick = 9; vibe.strong() }
        val left = if (win == -2) max(0f, TTT_SEC - max(0f, t - turnAt)) else 0f
        if (win == -2 && t >= turnAt) { val k = ceil(left).toInt(); if (k != tick) { tick = k; if (k < TTT_SEC) vibe.tick() } }
        val col = TTT_COL[turn]
        if (win == -2) { fillStyle = col + "1A"; fillRect(0f, 0f, w, h) }
        beginPath(); roundRect(x0 - 10, y0 - 10, sz + 20, sz + 20, 22f); fillStyle = "#1E2433"; fill(); lineWidth = 3f; strokeStyle = if (win == -2) col else "#2E3650"; stroke()
        strokeStyle = "#3A4462"; lineWidth = 6f; lineCap = "round"
        for (i in 1..2) { beginPath(); moveTo(x0 + i * cell, y0 + 12); lineTo(x0 + i * cell, y0 + sz - 12); stroke(); beginPath(); moveTo(x0 + 12, y0 + i * cell); lineTo(x0 + sz - 12, y0 + i * cell); stroke() }
        for (k in 0 until 9) {
            val v = cells[k]; if (v < 0) continue
            val cx = x0 + (k % 3 + 0.5f) * cell; val cy = y0 + (k / 3 + 0.5f) * cell; val pop = ease((t - placed[k]) / 0.18f)
            val l = line
            // 다음에 사라질 코인 (지금 차례인 사람이 3개 다 놓았을 때) 은 깜빡임
            val goes = win == -2 && v == turn && q[v].size >= 3 && q[v].first() == k
            globalAlpha = if (l != null && k !in l) 0.35f else if (goes) 0.45f + 0.35f * sin(t * 10) else 1f
            coin(cx, cy, cell * 0.36f * (0.5f + 0.5f * pop), 1, 1f, if (v == 1) "red" else "green"); globalAlpha = 1f
        }
        line?.let { l ->
            val ax = x0 + (l[0] % 3 + 0.5f) * cell; val ay = y0 + (l[0] / 3 + 0.5f) * cell; val bx = x0 + (l[2] % 3 + 0.5f) * cell; val by = y0 + (l[2] / 3 + 0.5f) * cell; val u = ease((t - endAt) / 0.3f)
            beginPath(); moveTo(ax, ay); lineTo(ax + (bx - ax) * u, ay + (by - ay) * u); strokeStyle = "#fff"; lineWidth = 8f; lineCap = "round"; stroke()
        }
        if (win == -2) {
            val bw = min(w - 60, 360f); val uu = if (t < turnAt) 1f else left / TTT_SEC
            for (yy in listOf(y0 + sz + 28, y0 - 28)) {
                beginPath(); roundRect((w - bw) / 2, yy - 6, bw, 12f, 6f); fillStyle = "rgba(255,255,255,.12)"; fill()
                beginPath(); roundRect((w - bw) / 2 + (if (yy < h / 2) bw * (1 - uu) else 0f), yy - 6, max(6f, bw * uu), 12f, 6f); fillStyle = if (uu < 0.35f) RED else col; fill()
            }
            val tu = t - timeUpAt < 1
            val msg = if (tu) s.timeUp else if (t < turnAt) s.colorTurn(names[turn]) else cat(s.colorTurn(names[turn]), "  " + String.format(java.util.Locale.US, "%.1f", left))
            // 아래 사람 = 초록, 위 사람 = 빨강. 각자 쪽에 "나: 초록" + 지금 상황
            eachSide(w, h, listOf(0, 1).map { cat(s.youAre(names[it]), "  ·  ", msg) }, 19f, listOf(0, 1).map { if (tu) RED else if (it == turn) TTT_COL[it] else "#8A8FA0" })
            for (p in 0..1) { save(); if (p == 1) { translate(w, h); rotate(PIf) }; coin(30f, h - 26 - (if (SEP in msg) 11f else 0f), 13f, 1, 1f, if (p == 1) "red" else "green"); restore() }
        } else {
            val msg = if (win == -1) s.drawWord else cat(s.winsOf(names[win]), "  ·  ", names[1 - win], " ", s.shotWord)
            eachSide(w, h, listOf(0, 1).map { cat(s.youAre(names[it]), "  ·  ", msg) }, 19f, listOf(0, 1).map { if (win == -1) GOLD else TTT_COL[win] })
            if (t - endAt > 0.8f) againPill(w, h, s)
        }
    }
}

@Composable
fun TicTacToeScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current
    SetupThen16(s.tttTitle, onBack, s.tttDesc) { PadCanvas(remember { TttGame(vibe) }) }
}

// ===================== ⚫⚪ 오목 =====================
const val GN = 15
private val DIRS = listOf(1 to 0, 0 to 1, 1 to 1, 1 to -1)

/** 놓은 자리(x,y)로 같은 색이 몇 개 이어지는지 (가장 긴 줄) */
fun maxRun(b: IntArray, x: Int, y: Int, v: Int): Int {
    var best = 0
    for ((dx, dy) in DIRS) {
        var n = 1
        for (sg in intArrayOf(1, -1)) for (k in 1 until GN) { val X = x + dx * k * sg; val Y = y + dy * k * sg; if (X !in 0 until GN || Y !in 0 until GN || b[Y * GN + X] != v) break; n++ }
        best = max(best, n)
    }
    return best
}

/** 흑 33 금지: (x,y)에 흑을 놓으면 열린 3이 두 줄 이상 생기는지 (b: 0 빈칸, 1 흑, 2 백, 아직 안 놓은 판) — 웹 isDoubleThree 와 같음 */
fun isDoubleThree(b: IntArray, x: Int, y: Int): Boolean {
    val bb = b.copyOf(); bb[y * GN + x] = 1
    if (maxRun(bb, x, y, 1) >= 5) return false
    fun at(X: Int, Y: Int) = if (X !in 0 until GN || Y !in 0 until GN) 3 else bb[Y * GN + X]
    var threes = 0
    for ((dx, dy) in DIRS) {
        var open3 = false
        for (q in -4..4) {
            if (open3) break
            if (q == 0 || at(x + dx * q, y + dy * q) != 0) continue
            for (a in -5..0) {
                if (open3) break
                val lo = a + 1; val hi = a + 4
                if (0 < lo || 0 > hi || q < lo || q > hi) continue
                var ok = at(x + dx * a, y + dy * a) == 0 && at(x + dx * (a + 5), y + dy * (a + 5)) == 0
                var k = lo
                while (k <= hi && ok) { val v = if (k == q) 1 else at(x + dx * k, y + dy * k); if (v != 1) ok = false; k++ }
                if (ok && (at(x + dx * (a - 1), y + dy * (a - 1)) == 1 || at(x + dx * (a + 6), y + dy * (a + 6)) == 1)) ok = false
                if (ok) open3 = true
            }
        }
        if (open3) threes++
    }
    return threes >= 2
}

private class GomokuGame(val vibe: Vibe) : PadGame {
    val b = IntArray(GN * GN); var turn = 1; var last = -1; var win = 0; var line: IntArray? = null; var endAt = 0f; var ban: IntArray? = null; var banAt = -9f; var moves = 0
    fun round() { b.fill(0); turn = 1; last = -1; win = 0; line = null; endAt = 0f; ban = null; banAt = -9f; moves = 0 }
    fun geo(w: Float, h: Float): FloatArray { val sz = minOf(w - 16, h - 130, 640f); val pad = sz / (GN + 0.2f) * 0.6f; return floatArrayOf(sz, (w - sz) / 2, (h - sz) / 2, pad, (sz - pad * 2) / (GN - 1)) }
    fun winLine(x: Int, y: Int, v: Int): IntArray? {
        for ((dx, dy) in DIRS) {
            val cells = arrayListOf(x to y)
            for (sg in intArrayOf(1, -1)) for (k in 1 until GN) { val X = x + dx * k * sg; val Y = y + dy * k * sg; if (X !in 0 until GN || Y !in 0 until GN || b[Y * GN + X] != v) break; cells.add(X to Y) }
            if (cells.size >= 5) { val sorted = cells.sortedWith(compareBy({ it.first }, { it.second })); return intArrayOf(sorted.first().first, sorted.first().second, sorted.last().first, sorted.last().second) }
        }
        return null
    }
    override fun down(x: Float, y: Float, t: Float, w: Float, h: Float) {
        if (win != 0) { if (t - endAt > 0.8f) { round(); vibe.tap() }; return }
        val (_, x0, y0, pad, step) = geo(w, h)
        val gx = ((x - x0 - pad) / step).roundToInt(); val gy = ((y - y0 - pad) / step).roundToInt()
        if (gx !in 0 until GN || gy !in 0 until GN || b[gy * GN + gx] != 0) return
        if (hypot(x - (x0 + pad + gx * step), y - (y0 + pad + gy * step)) > step * 0.7f) return
        if (turn == 1 && isDoubleThree(b, gx, gy)) { ban = intArrayOf(gx, gy); banAt = t; vibe.strong(); return }
        b[gy * GN + gx] = turn; last = gy * GN + gx; moves++; vibe.tap()
        val l = winLine(gx, gy, turn)
        if (l != null) { win = turn; line = l; endAt = t; vibe.strong(); return }
        if (moves >= GN * GN) { win = -1; endAt = t; return }
        turn = 3 - turn
    }
    override fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings) = with(c) {
        val (sz, x0, y0, pad, step) = geo(w, h); val names = listOf(s.black, s.white)
        linearFill(x0, y0, x0 + sz, y0 + sz, "#E8C27A", "#C99A4E"); beginPath(); roundRect(x0, y0, sz, sz, 10f); fill()
        strokeStyle = "rgba(60,35,10,.8)"; lineWidth = 1.2f; lineCap = "butt"
        for (i in 0 until GN) {
            val a = x0 + pad + i * step; val bb = y0 + pad + i * step
            beginPath(); moveTo(a, y0 + pad); lineTo(a, y0 + pad + (GN - 1) * step); stroke()
            beginPath(); moveTo(x0 + pad, bb); lineTo(x0 + pad + (GN - 1) * step, bb); stroke()
        }
        lineCap = "round"
        for ((i, j) in listOf(3 to 3, 11 to 3, 7 to 7, 3 to 11, 11 to 11)) { beginPath(); arc(x0 + pad + i * step, y0 + pad + j * step, max(2f, step * 0.12f), 0f, TAU); fillStyle = "rgba(60,35,10,.9)"; fill() }
        val r = step * 0.46f
        for (k in 0 until GN * GN) {
            val v = b[k]; if (v == 0) continue
            val cx = x0 + pad + (k % GN) * step; val cy = y0 + pad + (k / GN) * step
            beginPath(); arc(cx + r * 0.12f, cy + r * 0.16f, r, 0f, TAU); fillStyle = "rgba(0,0,0,.28)"; fill()
            beginPath(); arc(cx, cy, r, 0f, TAU)
            radialFill(cx - r * 0.35f, cy - r * 0.35f, r * 0.1f, r * 1.3f, if (v == 1) listOf(0f to "#666666", 1f to "#0B0B0B") else listOf(0f to "#FFFFFF", 1f to "#CFCFCF")); fill()
            if (k == last) { beginPath(); arc(cx, cy, r * 0.28f, 0f, TAU); fillStyle = RED; fill() }
        }
        val bn = ban
        if (bn != null && t - banAt < 1.2f) {
            val cx = x0 + pad + bn[0] * step; val cy = y0 + pad + bn[1] * step
            globalAlpha = 1 - (t - banAt) / 1.2f; strokeStyle = RED; lineWidth = 3f
            beginPath(); arc(cx, cy, r, 0f, TAU); stroke(); beginPath(); moveTo(cx - r * 0.7f, cy - r * 0.7f); lineTo(cx + r * 0.7f, cy + r * 0.7f); stroke(); globalAlpha = 1f
        }
        line?.let { l ->
            val ax = x0 + pad + l[0] * step; val ay = y0 + pad + l[1] * step; val bx = x0 + pad + l[2] * step; val by = y0 + pad + l[3] * step; val u = ease((t - endAt) / 0.3f)
            beginPath(); moveTo(ax, ay); lineTo(ax + (bx - ax) * u, ay + (by - ay) * u); strokeStyle = RED; lineWidth = 5f; lineCap = "round"; stroke()
        }
        val (msg, col) = when {
            win == -1 -> s.drawWord to GOLD
            win != 0 -> cat(s.winsOf(names[win - 1]), "  ·  ", names[2 - win], " ", s.shotWord) to GOLD
            t - banAt < 1.2f -> s.banned33 to RED
            else -> s.colorTurn(names[turn - 1]) to (if (turn == 1) "#ffffff" else "#dddddd")
        }
        val pd = max(22f, y0 / 2)
        // 아래 사람 = 흑, 위 사람 = 백. 각자 쪽에 "나: 흑" + 지금 상황 (내 차례면 밝게)
        eachSide(w, h, listOf(0, 1).map { cat(s.youAre(names[it]), "  ·  ", msg) }, 19f, listOf(0, 1).map { if (win != 0 || t - banAt < 1.2f) col else if (it == turn - 1) "#ffffff" else "#8A8FA0" }, pd)
        for (p in 0..1) {
            save(); if (p == 1) { translate(w, h); rotate(PIf) }
            val sy = h - pd - (if (SEP in msg) 10f else 0f)
            beginPath(); arc(28f, sy, 11f, 0f, TAU); fillStyle = if (p == 1) "#eeeeee" else "#111111"; fill(); lineWidth = 2f; strokeStyle = if (win == 0 && p == turn - 1) GOLD else "#888888"; stroke(); restore()
        }
        if (win != 0 && t - endAt > 0.8f) againPill(w, h, s)
    }
}

@Composable
fun GomokuScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current
    SetupThen16(s.gomokuTitle, onBack, s.gomokuDesc) { PadCanvas(remember { GomokuGame(vibe) }) }
}
