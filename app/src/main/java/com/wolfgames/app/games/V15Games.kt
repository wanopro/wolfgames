package com.wolfgames.app.games

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.i18n.Strings
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.Ctx
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.PCard
import com.wolfgames.app.ui.PLAYER_HEX
import com.wolfgames.app.ui.Sector
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.SplitCanvas
import com.wolfgames.app.ui.SplitGame
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.TAU
import com.wolfgames.app.ui.Vibe
import com.wolfgames.app.ui.WG
import com.wolfgames.app.ui.badge
import com.wolfgames.app.ui.chompWolf
import com.wolfgames.app.ui.coin
import com.wolfgames.app.ui.coinEdge
import com.wolfgames.app.ui.hub
import com.wolfgames.app.ui.region
import com.wolfgames.app.ui.splitLines
import com.wolfgames.app.ui.text
import com.wolfgames.app.ui.wolfPaw
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sin
import kotlin.random.Random

// v1.5 새 게임 (웹 games-v15.js 와 같은 규칙)
// 탭 줄다리기 · 슬랩 대결(⚔️ 대결) / 맥주 거품 채우기(⚡ 순발력) / 동전 던지기(🍀 운빨) / 글로벌 킹스컵(🃏 카드)

private const val GOLD = "#FFC107"
private const val RED = "#FF3D5A"
private const val PIf = PI.toFloat()
private fun ease(u: Float) = 1f - (1f - u.coerceIn(0f, 1f)).pow(3)

/** 설정 화면 → 게임 화면 */
@Composable
private fun SetupThen(title: String, onBack: () -> Unit, setup: @Composable (start: () -> Unit) -> Unit, game: @Composable () -> Unit) {
    var started by rememberSaveable { mutableStateOf(com.wolfgames.app.DebugOpen.autostart) }
    if (!started) GameScaffold(title, onBack) { setup { started = true } }
    else { BackHandler { started = false }; GameScaffold(title, onBack = { started = false }) { game() } }
}

/** 매 프레임 다시 그리는 캔버스 (t = 시작 후 초) */
@Composable
private fun AnimCanvas(modifier: Modifier, input: Modifier, draw: (Ctx, Float, Float, Float) -> Unit) {
    val density = LocalDensity.current.density
    val t0 = remember { System.nanoTime() }
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) { while (true) withFrameNanos { tick = it } }
    Canvas(modifier.then(input)) {
        tick.let { }
        val w = size.width / density; val h = size.height / density
        drawIntoCanvas { cc -> val ctx = Ctx(cc.nativeCanvas, density); ctx.fillStyle = "#12121A"; ctx.fillRect(0f, 0f, w, h); draw(ctx, w, h, (System.nanoTime() - t0) / 1e9f); ctx.finish() }
    }
}

// ---------- 위·아래 2명 화면 (누르기·떼기 둘 다 받음) ----------
private interface TwoSideGame {
    fun draw(ctx: Ctx, w: Float, h: Float, t: Float, s: Strings)
    fun down(p: Int, t: Float, x: Float, y: Float)
    fun up(p: Int, t: Float, left: Int) {}
    fun hub(t: Float) {}
}

@Composable
private fun TwoSideCanvas(game: TwoSideGame) {
    val s = LocalStrings.current
    val density = LocalDensity.current.density
    val t0 = remember(game) { System.nanoTime() }
    val now = { (System.nanoTime() - t0) / 1e9f }
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(game) { while (true) withFrameNanos { tick = it } }
    Canvas(
        Modifier.fillMaxSize().pointerInput(game) {
            awaitPointerEventScope {
                val owner = HashMap<PointerId, Int>()
                while (true) {
                    val ev = awaitPointerEvent()
                    val w = size.width / density; val h = size.height / density
                    for (ch in ev.changes) {
                        if (ch.changedToDown()) {
                            val x = ch.position.x / density; val y = ch.position.y / density
                            if (hypot(x - w / 2, y - h / 2) < 40f) game.hub(now())
                            else { val p = if (y > h / 2) 0 else 1; owner[ch.id] = p; game.down(p, now(), x, y) }
                            ch.consume()
                        } else if (ch.changedToUp()) {
                            val p = owner.remove(ch.id)
                            if (p != null) game.up(p, now(), owner.values.count { it == p })
                            ch.consume()
                        }
                    }
                }
            }
        },
    ) {
        tick.let { }
        val w = size.width / density; val h = size.height / density
        drawIntoCanvas { cc -> val ctx = Ctx(cc.nativeCanvas, density); ctx.fillStyle = "#12121A"; ctx.fillRect(0f, 0f, w, h); game.draw(ctx, w, h, now(), s); ctx.finish() }
    }
}

/** 위 사람(P2)은 180도 돌려서 그림. (0,0) = 가운데 선 왼쪽, y+ = 화면 끝 쪽 */
private fun Ctx.half(w: Float, h: Float, topSide: Boolean, fn: Ctx.() -> Unit) {
    save(); if (topSide) { translate(w, h / 2); rotate(PIf) } else translate(0f, h / 2); fn(); restore()
}

// ===================== 🎀 탭 줄다리기 =====================
private class TugGame(val vibe: Vibe) : TwoSideGame {
    var startAt = 0f; var pos = 0f; var disp = 0f; val cnt = IntArray(2); var win = -1; var winAt = 0f; var last = 0f; var buzz = false
    val ripples = ArrayDeque<FloatArray>()
    init { round(0f) }
    fun round(t: Float) { startAt = t + 1.6f; pos = 0f; disp = 0f; cnt[0] = 0; cnt[1] = 0; win = -1; winAt = 0f; last = t; buzz = false; ripples.clear() }
    override fun down(p: Int, t: Float, x: Float, y: Float) {
        if (win >= 0 || t < startAt) return
        cnt[p]++; pos += if (p == 0) 0.045f else -0.045f; ripples.addLast(floatArrayOf(t, x, y)); if (ripples.size > 40) ripples.removeFirst()
        if (abs(pos) >= 1f) { win = if (pos > 0) 0 else 1; winAt = t; pos = sign(pos); vibe.strong() }
    }
    override fun hub(t: Float) { if (win >= 0 && t - winAt > 0.8f) { round(t); vibe.tap() } }
    override fun draw(ctx: Ctx, w: Float, h: Float, t: Float, s: Strings) = with(ctx) {
        val dt = t - last; last = t
        if (t >= startAt && !buzz) { buzz = true; vibe.tick() }
        disp = if (win >= 0) pos else disp + (pos - disp) * min(1f, dt * 12)
        val edge = min(110f, h * 0.15f); val midY = h / 2 - disp * (h / 2 - edge)
        val shake = if (win < 0 && t > startAt) sin(t * 60) * 2 else 0f
        fillStyle = PLAYER_HEX[1] + "44"; fillRect(0f, 0f, w, midY); fillStyle = PLAYER_HEX[0] + "44"; fillRect(0f, midY, w, h - midY)
        lineDash = floatArrayOf(10f, 8f); lineWidth = 3f
        for ((y, c) in listOf(edge to PLAYER_HEX[0], h - edge to PLAYER_HEX[1])) { strokeStyle = c; beginPath(); moveTo(14f, y); lineTo(w - 14, y); stroke() }
        lineDash = null
        save(); translate(w / 2 + shake, 0f)
        lineWidth = 16f; strokeStyle = "#C8A06A"; beginPath(); moveTo(0f, 30f); lineTo(0f, h - 30); stroke()
        strokeStyle = "#8D6A3F"; lineWidth = 3f
        var yy = 30f + (((disp * 60) % 22) + 22) % 22
        while (yy < h - 30) { beginPath(); moveTo(-8f, yy); lineTo(8f, yy + 9); stroke(); yy += 22f }
        beginPath(); arc(0f, midY, 24f, 0f, TAU); fillStyle = "#FF7043"; fill(); lineWidth = 4f; strokeStyle = "#fff"; stroke()
        text("🎀", 0f, midY + 1, 26f, "#fff")
        restore()
        for (r in ripples) { val u = (t - r[0]) / 0.35f; if (u < 0 || u > 1) continue; beginPath(); arc(r[1], r[2], 14 + u * 40, 0f, TAU); strokeStyle = "rgba(255,255,255,${0.7f * (1 - u)})"; lineWidth = 4f; stroke() }
        for (p in 0..1) half(w, h, p == 1) {
            val won = win == p; val lost = win >= 0 && !won; val hy = h / 2
            text("P${p + 1}", 44f, hy - 34, 22f, PLAYER_HEX[p])
            text("${cnt[p]}", w - 56, hy - 44, 30f, "#fff"); text(s.tugTaps, w - 56, hy - 18, 13f, "#bbb")
            val lx = w / 2 - min(95f, w * 0.24f); val ly = min(hy - 120, hy * 0.6f)
            if (win < 0) { if (t >= startAt) text(s.tugMash, lx, ly, 24 * (1 + 0.08f * sin(t * 25)), "#fff") }
            else badge(if (won) s.winWord else s.shotWord, lx, ly, 1.2f, if (won) GOLD else RED, if (won) "#000" else "#fff")
            if (lost) chompWolf(w / 2 + min(85f, w * 0.22f), ly - 6, 32f, abs(sin((t - winAt) * 10)), min(1f, (t - winAt) * 4))
        }
        if (t < startAt + 0.4f) {
            val a = if (t < startAt) 0.55f else 0.55f * (1 - (t - startAt) / 0.4f)
            fillStyle = "rgba(0,0,0,$a)"; fillRect(0f, 0f, w, h)
            for (p in 0..1) half(w, h, p == 1) { text(if (t < startAt - 0.5f) s.readyWord else "GO!", w / 2, 70f, 44f, "#FFD740") }
        }
        if (win >= 0 && t - winAt > 0.8f) hub(w, h, 30f, "#121826") { text("🔄", w / 2, h / 2 + 1, 22f, "#fff") }
    }
}

@Composable
fun TugWarScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current
    SetupThen(s.tugTitle, onBack, { start -> SetupPanel(s.tugDesc, start) {} }) { TwoSideCanvas(remember { TugGame(vibe) }) }
}

// ===================== 🐾 슬랩 대결 =====================
private const val IMPACT = 0.22f
private class SlapGame(val vibe: Vibe) : TwoSideGame {
    var att = Random.nextInt(2); val score = IntArray(2); val hold = IntArray(2); var phase = "wait"; var at = 0f; var kind = ""
    fun result(k: String, t: Float) { phase = "result"; kind = k; at = t; if (k == "hit" || k == "flinch") { score[att]++; vibe.strong() } else vibe.tick() }
    override fun down(p: Int, t: Float, x: Float, y: Float) {
        hold[p]++
        val def = 1 - att
        if (p == def && phase == "wait") phase = "ready"
        if (p == att && phase == "ready") { phase = "swing"; at = t; vibe.tap() }
    }
    override fun up(p: Int, t: Float, left: Int) {
        hold[p] = left
        if (p != 1 - att || left > 0) return
        if (phase == "swing" && t - at < IMPACT) result("dodge", t) else if (phase == "ready") result("flinch", t)
    }
    override fun draw(ctx: Ctx, w: Float, h: Float, t: Float, s: Strings) = with(ctx) {
        if (phase == "swing" && t - at >= IMPACT) result("hit", at + IMPACT)
        if (phase == "result" && t - at > 1.7f) { if (kind == "dodge") att = 1 - att; phase = if (hold[1 - att] > 0 && kind == "hit") "ready" else "wait" }
        val def = 1 - att; val dtE = t - at
        fillStyle = PLAYER_HEX[1] + "33"; fillRect(0f, 0f, w, h / 2); fillStyle = PLAYER_HEX[0] + "33"; fillRect(0f, h / 2, w, h / 2)
        strokeStyle = "rgba(0,0,0,.6)"; lineWidth = 4f; beginPath(); moveTo(0f, h / 2); lineTo(w, h / 2); stroke()
        val cx = w / 2; val cy = h / 2; val sg = if (def == 1) -1f else 1f; val k = min(1f, h / 760f) * 0.95f
        var defOff = 0f; var attOff = 0f; var red = 0f; var burst = -1f
        if (phase == "swing") attOff = ease(dtE / IMPACT) * 120
        if (phase == "result" && kind == "hit") { attOff = if (dtE < 0.45f) 120f else max(0f, 1 - (dtE - 0.45f) / 0.4f) * 120; red = max(0f, 1 - dtE / 1.6f); burst = dtE }
        if (phase == "result" && kind == "dodge") { attOff = if (dtE < 0.2f) 120 * ease(0.6f + dtE * 2) else max(0f, 1 - (dtE - 0.2f) / 0.4f) * 120; defOff = min(1f, dtE / 0.1f) * 80 * (if (dtE < 1f) 1f else max(0f, 1 - (dtE - 1f) / 0.4f)) }
        if (phase == "result" && kind == "flinch") defOff = min(1f, dtE / 0.08f) * 50 * max(0f, 1 - max(0f, dtE - 0.6f) / 0.4f)
        if (phase == "wait") defOff = 30f
        val swing = min(1f, attOff / 120); val shake = if (phase == "ready") sin(t * 9) * 3 else 0f
        val attY = cy - sg * (58 - attOff * 0.95f) * k; val defY = cy + sg * (58 + defOff) * k
        if (swing < 0.5f) { wolfPaw(cx + shake, attY, k * (0.95f + swing * 0.3f), PLAYER_HEX[att], att == 1, true); wolfPaw(cx, defY, k * 0.95f, PLAYER_HEX[def], def == 1, false, red) }
        else { wolfPaw(cx, defY, k * 0.95f, PLAYER_HEX[def], def == 1, false, red); wolfPaw(cx + shake + swing * 14, attY, k * (0.95f + swing * 0.22f), PLAYER_HEX[att], att == 1, false) }
        if (burst in 0f..0.8f) {
            val q = min(1f, burst / 0.12f); save(); translate(cx + 70 * k, cy + sg * 40 * k); scale(q, q); rotate(-0.2f)
            beginPath(); for (a in 0 until 16) { val r = if (a % 2 == 1) 26f else 52f; val x = cos(a * PIf / 8) * r; val y = sin(a * PIf / 8) * r; if (a == 0) moveTo(x, y) else lineTo(x, y) }; closePath()
            fillStyle = "#FFEB3B"; fill(); strokeStyle = "#E65100"; lineWidth = 4f; stroke(); text(s.slapSmack, 0f, 2f, 22f, "#D84315"); restore()
        }
        if (burst in 0f..0.16f) { fillStyle = "rgba(255,255,255,${0.7f * (1 - burst / 0.16f)})"; fillRect(0f, 0f, w, h) }
        for (p in 0..1) half(w, h, p == 1) {
            val isAtt = p == att; val y = h / 2 - 40
            text("P${p + 1}", 40f, y + 16, 20f, PLAYER_HEX[p]); text("💥 ${score[p]}", w - 50, y + 16, 20f, "#fff")
            text(if (isAtt) s.slapAtt else s.slapDef, w / 2, y - 70, 24f, if (isAtt) "#FFAB91" else "#80D8FF")
            val sub = if (phase == "result") when (kind) { "hit" -> if (isAtt) s.slapHit else s.slapOuch; "dodge" -> if (isAtt) s.slapMiss else s.slapDodge; else -> if (isAtt) s.slapFlinchAtt else s.slapFlinch }
            else if (isAtt) (if (phase == "wait") s.slapAttWait else s.slapAttHint) else (if (phase == "wait") s.slapDefHold else s.slapDefHint)
            text(sub, w / 2, y - 38, 17f, "#fff")
        }
    }
}

@Composable
fun SlapDuelScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current
    SetupThen(s.slapTitle, onBack, { start -> SetupPanel(s.slapDesc, start) {} }) { TwoSideCanvas(remember { SlapGame(vibe) }) }
}

// ===================== 🍺 맥주 거품 채우기 =====================
private class BeerGame(override val n: Int, val vibe: Vibe) : SplitGame {
    var startAt = 0f; var k = 1f; var stop = arrayOfNulls<Float>(n); var doneAt = 0f
    // 차오르는 속도 = 점점 빨라지는 기본 속도 × (1 + 물결 두 개) → 빨라졌다 느려졌다 (멈추거나 줄지는 않음) — 웹과 같음
    var waves = listOf<FloatArray>()
    private fun rw(a: Float, b: Float) = a + Random.nextFloat() * (b - a)
    fun round(t: Float) {
        startAt = t + 1.4f; k = 0.85f + Random.nextFloat() * 0.35f; stop = arrayOfNulls(n); doneAt = 0f
        waves = listOf(floatArrayOf(rw(0.35f, 0.5f), rw(2.2f, 3.6f), rw(0f, 6.28f)), floatArrayOf(rw(0.2f, 0.35f), rw(4.5f, 7f), rw(0f, 6.28f)))
    }
    private fun wave(x: Float, amp: Float, w: Float, p: Float): Float {
        fun f(u: Float) = -(0.06f + 0.11f * u) * cos(w * u + p) / w + 0.11f * sin(w * u + p) / (w * w)
        return amp * (f(x) - f(0f))
    }
    fun level(t: Float): Float { val x = max(0f, t - startAt) * k; return min(1.12f, 0.06f * x + 0.055f * x * x + waves.sumOf { wave(x, it[0], it[1], it[2]).toDouble() }.toFloat()) }
    init { round(0f) }
    fun overT(): Float { var lo = startAt; var hi = startAt + 30; repeat(40) { val m = (lo + hi) / 2; if (level(m) >= 1.06f) hi = m else lo = m }; return hi }
    override fun tap(i: Int, t: Float) { if (doneAt > 0 || t < startAt || stop[i] != null) return; stop[i] = level(t); vibe.tap() }
    override fun hub(t: Float) { if (doneAt > 0 && t - doneAt > 0.6f) { round(t); vibe.tap() } }
    override fun draw(ctx: Ctx, w: Float, h: Float, t: Float, secs: List<Sector>, s: Strings) = with(ctx) {
        if (doneAt == 0f && (stop.all { it != null } || t > overT() + 0.3f)) { doneAt = t; for (i in 0 until n) if (stop[i] == null) stop[i] = 1.06f; vibe.strong() }
        val done = doneAt > 0
        val order = (0 until n).sortedBy { val v = stop[it]; if (v == null || v > 1f) 9f + (v ?: 0f) else 1f - v }
        secs.forEachIndexed { i, sec ->
            val v = stop[i] ?: min(level(t), 1.06f); val over = v > 1f
            val rk = if (done) order.indexOf(i) else -1; val first = done && rk == 0 && !over; val lose = done && (over || rk == n - 1)
            region(sec, PLAYER_HEX[i] + "30", if (first) GOLD else if (lose) RED else null) { sc ->
                val gh = 140 * sc; val gw = 84 * sc; val topY = -gh / 2 - 26 * sc; val bot = gh / 2 - 26 * sc; val lineY = topY + 18 * sc
                text("P${i + 1}", -gw - 22 * sc, topY + 8 * sc, 18 * sc, PLAYER_HEX[i])
                val glass: Ctx.() -> Unit = { beginPath(); moveTo(-gw / 2, topY); lineTo(gw / 2, topY); lineTo(gw / 2 - 8 * sc, bot); lineTo(-gw / 2 + 8 * sc, bot); closePath() }
                glass(); fillStyle = "rgba(255,255,255,.10)"; fill()
                save(); glass(); clip()
                val fillTop = bot - (bot - lineY) * min(v, 1f)
                fillStyle = "#F9A825"; fillRect(-gw, fillTop, gw * 2, bot - fillTop + 2)
                fillStyle = "rgba(255,255,255,.55)"
                for (b in 0 until 7) { val by = bot - ((t * 60 * sc + b * 23 * sc) % (bot - fillTop + 1)); beginPath(); arc(-gw / 3 + (b * 13 % 50) * sc, by, 2.2f * sc, 0f, TAU); fill() }
                val fh = 12 * sc; fillStyle = "#FFF8E1"
                for (q in -3..3) { beginPath(); arc(q * 14 * sc, fillTop - fh * 0.3f + sin(t * 6 + q) * 1.5f * sc, 10 * sc, 0f, TAU); fill() }
                fillRect(-gw, fillTop - fh * 0.3f, gw * 2, fh)
                restore()
                glass(); lineWidth = 4 * sc; strokeStyle = "#E0E0E0"; stroke()
                beginPath(); roundRect(gw / 2 - 4 * sc, topY + 26 * sc, 26 * sc, 70 * sc, 12 * sc); lineWidth = 7 * sc; stroke()
                if (v >= 1.04f) { fillStyle = "#FFF8E1"; for (sd in listOf(-1f, 1f)) { beginPath(); ellipse(sd * (gw / 2 + 2 * sc), topY + 30 * sc, 8 * sc, 28 * sc, 0f, 0f, TAU); fill() }; beginPath(); ellipse(0f, topY - 8 * sc, gw * 0.6f, 14 * sc, 0f, 0f, TAU); fill() }
                lineDash = floatArrayOf(6 * sc, 5 * sc); strokeStyle = "#FF5252"; lineWidth = 3 * sc; beginPath(); moveTo(-gw / 2 - 14 * sc, lineY); lineTo(gw / 2 + 14 * sc, lineY); stroke(); lineDash = null
                text(s.beerLine, -gw / 2 - 32 * sc, lineY, 11 * sc, "#FF8A80")
                val msg = if (stop[i] != null) (if (over) s.beerOver else "%.1f%%".format(v * 100)) else if (t < startAt) s.readyWord else s.beerTap
                text(msg, 0f, bot + 24 * sc, 20 * sc, if (over) "#80D8FF" else "#fff")
                if (first) badge(s.rankFirst, 0f, bot + 56 * sc, sc * 0.9f, GOLD, "#000") else if (lose) badge(s.rankLast, 0f, bot + 56 * sc, sc * 0.9f, RED, "#fff")
            }
        }
        splitLines(secs, w, h)
        hub(w, h, 34f, "#1F2433") { text(if (done) "🔄" else "🍺", w / 2, h / 2 + 1, 24f, "#fff") }
        if (done) text(s.tapHubAgain, w / 2, h / 2 + 52, 13f, "#fff")
    }
}

@Composable
fun BeerPourScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current
    var n by rememberSaveable { mutableIntStateOf(2) }
    SetupThen(s.beerTitle, onBack, { start -> SetupPanel(s.beerDesc, start) { Stepper(s.players, n, 2..6) { n = it } } }) { SplitCanvas(remember { BeerGame(n, vibe) }) }
}

// ===================== 🪙 동전 던지기 =====================
const val EDGE_CHANCE = 0.15f
private class CoinGame(override val n: Int, val vibe: Vibe) : SplitGame {
    var pick = IntArray(n); var allAt = 0f; var spinAt = 0f; var landAt = 0f; var result = 1; var last = 9; var buzzed = false
    fun round() { pick = IntArray(n); allAt = 0f; last = 9; buzzed = false }
    override fun tap(i: Int, t: Float) {}
    override fun tapAt(i: Int, t: Float, x: Float, y: Float, sec: Sector) {
        if (allAt > 0) return
        val dx = x - sec.ax; val dy = y - sec.ay
        val lx = dx * cos(-sec.rot) - dy * sin(-sec.rot)
        pick[i] = if (lx < 0) 1 else -1; vibe.tap()
        if (pick.all { it != 0 }) {
            allAt = t; spinAt = t + 3; landAt = spinAt + 2.4f
            result = if (Random.nextFloat() < EDGE_CHANCE) 0 else if (Random.nextBoolean()) 1 else -1
        }
    }
    override fun hub(t: Float) { if (allAt > 0 && t > landAt + 1.2f) { round(); vibe.tap() } }
    override fun draw(ctx: Ctx, w: Float, h: Float, t: Float, secs: List<Sector>, s: Strings) = with(ctx) {
        val done = allAt > 0 && t >= landAt + (if (result == 0) 1.4f else 0.35f)
        if (allAt > 0 && t < spinAt) { val k = ceil(spinAt - t).toInt(); if (k != last) { last = k; vibe.tick() } }
        if (done && !buzzed) { buzzed = true; vibe.strong() }
        secs.forEachIndexed { i, sec ->
            val p = pick[i]; val ok = done && p == result; val bad = done && !ok
            region(sec, if (bad) "#4A1820" else if (ok) "#1F5E3A" else PLAYER_HEX[i] + "30", if (done) (if (ok) "#69F0AE" else RED) else null) { sc ->
                text("P${i + 1}", 0f, -64 * sc, 18 * sc, PLAYER_HEX[i])
                if (done) {
                    text(if (ok) s.coinRight else s.coinWrong, 0f, -18 * sc, 34 * sc, if (ok) "#69F0AE" else "#FF8A80")
                    coin(0f, 34 * sc, 22 * sc, p); if (bad) badge(s.shotWord, 0f, 76 * sc, sc * 0.85f, RED, "#fff")
                } else {
                    for ((f, dx, lab) in listOf(Triple(1, -48f, s.coinHead), Triple(-1, 48f, s.coinTail))) {
                        val on = p == f; val off = p != 0 && !on
                        save(); globalAlpha = if (off) 0.35f else 1f
                        beginPath(); roundRect((dx - 40) * sc, -36 * sc, 80 * sc, 96 * sc, 16 * sc); fillStyle = if (on) "#FFC94A" else "rgba(255,255,255,.08)"; fill()
                        if (on) { lineWidth = 4 * sc; strokeStyle = "#fff"; stroke() }
                        coin(dx * sc, 4 * sc, 26 * sc, f); text(lab, dx * sc, 46 * sc, 16 * sc, if (on) "#000" else "#fff"); restore()
                    }
                    if (p == 0) text(s.coinPick, 0f, 78 * sc, 14 * sc, "#ccc")
                }
            }
        }
        splitLines(secs, w, h)
        val cx = w / 2; val cy = h / 2; val R = 46f
        if (allAt == 0f || t < spinAt) {
            beginPath(); arc(cx, cy, R + 12, 0f, TAU); fillStyle = "#1F2433"; fill(); lineWidth = 5f; strokeStyle = "rgba(0,0,0,.6)"; stroke()
            coin(cx, cy, R * 0.8f, 1)
            if (allAt > 0) { val k = ceil(spinAt - t).toInt(); val u = (spinAt - t) % 1f; fillStyle = "rgba(0,0,0,.55)"; beginPath(); arc(cx, cy, R, 0f, TAU); fill(); text("$k", cx, cy + 2, 46 * (1 + 0.3f * u), "#FFD740") }
            else text("${pick.count { it != 0 }}/$n", cx, cy + R + 24, 15f, "#fff")
            return@with
        }
        val u = min(1f, (t - spinAt) / 2.4f); val flips = 9
        val endPhi = if (result == 0) (flips + 0.5f) * PIf else (if (result > 0) flips + 1 else flips) * PIf
        val phi = endPhi * ease(min(1f, u * 1.05f)); val hgt = sin(min(1f, u * 1.15f) * PIf); val r = R * (1 + 0.9f * hgt)
        beginPath(); ellipse(cx, cy + R * 0.9f, R * (1 - 0.4f * hgt), R * 0.18f * (1 - 0.4f * hgt), 0f, 0f, TAU); fillStyle = "rgba(0,0,0,.35)"; fill()
        if (result == 0 && u >= 1f) {
            val dt = t - landAt; coinEdge(cx, cy + R * 0.9f, R, sin(dt * 14) * 0.25f * exp(-dt * 2.2f))
            if (dt > 0.9f) badge(s.coinEdge, cx, cy - R * 1.6f, min(1f, w / 380f), RED, "#fff")
        } else {
            val sy = cos(phi); coin(cx, cy - hgt * 20, r, if (sy >= 0) 1 else -1, abs(sy))
            if (u >= 1f && t > landAt + 0.35f) badge(if (result > 0) s.coinResHead else s.coinResTail, cx, cy - R * 1.55f, 1f, "#FFC94A", "#000")
        }
        if (done && t > landAt + 1.2f) text(s.tapHubAgain, cx, cy + R + 34, 13f, "#fff")
    }
}

@Composable
fun CoinTossScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current
    var n by rememberSaveable { mutableIntStateOf(4) }
    SetupThen(s.coinTitle, onBack, { start -> SetupPanel(s.coinDesc, start) { Stepper(s.players, n, 2..8) { n = it } } }) { SplitCanvas(remember { CoinGame(n, vibe) }) }
}

// ===================== 👑 글로벌 킹스컵 =====================
val KC_ICONS = listOf("🌊", "👉", "🙋", "🚽", "💃", "🍻", "🙌", "🤝", "🔤", "📚", "📜", "❓", "👑")

private class KingsState {
    var deck = PCard.deck(); var cur: PCard? = null; var at = 0f; var kings = 0
    fun reset() { deck = PCard.deck(); cur = null; kings = 0 }
}

private fun Ctx.wrapText(str: String, x: Float, y: Float, maxW: Float, size: Float, color: String) {
    font(size, true)
    val spaced = str.contains(' ')
    val words = if (spaced) str.split(' ') else str.map { it.toString() }
    val out = ArrayList<String>(); var line = ""
    for (wd in words) { val tryL = if (line.isEmpty()) wd else line + (if (spaced) " " else "") + wd; if (measureText(tryL) > maxW && line.isNotEmpty()) { out.add(line); line = wd } else line = tryL }
    if (line.isNotEmpty()) out.add(line)
    out.forEachIndexed { i, l -> text(l, x, y + i * (size + 6), size, color) }
}

@Composable
fun KingsCupScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current
    val names = s.kcNames.split('|'); val descs = s.kcDescs.split('|')
    val st = remember { KingsState() }
    var rules by remember { mutableStateOf(false) }
    val t0 = remember { System.nanoTime() }
    val now = { (System.nanoTime() - t0) / 1e9f }
    GameScaffold("🃏 ${s.kcTitle}", onBack) {
        Column(Modifier.fillMaxSize()) {
            AnimCanvas(Modifier.fillMaxWidth().weight(1f), Modifier.pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val ev = awaitPointerEvent()
                        if (ev.changes.any { it.changedToDown() }) {
                            ev.changes.forEach { it.consume() }
                            if (st.deck.isEmpty()) continue
                            if (st.cur != null && now() - st.at < 0.4f) continue
                            if (st.cur?.r == 13 && st.kings == 4) st.kings = 0
                            val c = st.deck.removeAt(st.deck.size - 1); st.cur = c; st.at = now(); if (c.r == 13) st.kings++
                            vibe.tap(); if (c.r == 13 && st.kings == 4) vibe.strong()
                        }
                    }
                }
            }) { ctx, w, h, _ -> with(ctx) {
                val t = now()
                val deckY = max(70f, h * 0.12f)
                val gx = w - 56; val gy = deckY
                save(); translate(gx, gy)
                val cup: Ctx.() -> Unit = { beginPath(); moveTo(-24f, -28f); lineTo(24f, -28f); lineTo(17f, 28f); lineTo(-17f, 28f); closePath() }
                cup(); fillStyle = "rgba(255,255,255,.12)"; fill(); strokeStyle = "#ddd"; lineWidth = 3f; stroke()
                save(); cup(); clip(); fillStyle = "#F9A825"; fillRect(-30f, 28 - 56f * st.kings / 4, 60f, 56f * st.kings / 4); restore(); restore()
                text("👑 ${st.kings}/4", gx, gy + 46, 15f, "#FFD740")
                text("🂠 ${s.kcLeft(st.deck.size)}", 64f, deckY, 15f, "#aaa")
                if (st.deck.isNotEmpty()) {
                    for (i in 0 until min(4, st.deck.size)) { beginPath(); roundRect(w / 2 - 38 + i * 2, deckY - 52 - i * 2, 76f, 104f, 10f); fillStyle = "#B71C1C"; fill(); strokeStyle = "#fff"; lineWidth = 2f; stroke() }
                    text("🐺", w / 2 + 6, deckY - 2, 34f, "#fff")
                }
                val cur = st.cur
                if (cur == null) { text(if (st.deck.isNotEmpty()) s.kcTap else s.kcEmpty, w / 2, h * 0.55f, 22f, "#fff"); return@with }
                val u = (t - st.at) / 0.45f; val ri = cur.r - 1; val last = cur.r == 13 && st.kings == 4
                val fly = ease(min(1f, u * 1.6f)); val flip = min(1f, u); val sx = abs(cos(flip * PIf))
                val cw = min(250f, w * 0.64f); val ch = min(cw * 1.36f, h * 0.5f); val cx = w / 2; val cy = deckY + fly * (h * 0.5f - deckY)
                save(); translate(cx, cy); scale(max(0.02f, sx), 1f)
                beginPath(); roundRect(-cw / 2, -ch / 2, cw, ch, 22f)
                if (flip < 0.5f) { fillStyle = "#B71C1C"; fill(); text("🐺", 0f, 0f, 64f, "#fff") }
                else {
                    fillStyle = "#FFFDF7"; fill(); lineWidth = 4f; strokeStyle = if (last) "#FFC107" else "#ddd"; stroke()
                    val col = if (cur.red) "#D32F2F" else "#212121"
                    for (rot in listOf(0f, PIf)) { save(); rotate(rot); text(cur.rank, -cw / 2 + 26, -ch / 2 + 28, 26f, col); text(cur.suit, -cw / 2 + 26, -ch / 2 + 56, 22f, col); restore() }
                    val pop = 1 + 0.15f * max(0f, sin(min(1f, (u - 0.5f) * 2) * PIf))
                    text(if (last) "🍺" else KC_ICONS[ri], 0f, -ch * 0.06f, cw * 0.42f * pop, "#000")
                }
                restore()
                if (flip >= 0.5f) {
                    val ty = cy + ch / 2 + 32
                    text(if (last) s.kcLastKing else "${KC_ICONS[ri]} ${names[ri]}", w / 2, ty, if (last) 22f else 28f, if (last) "#FFD740" else "#fff")
                    wrapText(if (last) s.kcLastKingSub else descs[ri], w / 2, ty + 34, w - 40, 16f, "#bbb")
                    if (last) chompWolf(w / 2, ty + 90, 26f, abs(sin(t * 8)), 1f)
                }
            } }
            Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BigButton(s.kcRules, Modifier.weight(1f), color = WG.Surface2, contentColor = WG.Text) { rules = true }
                BigButton(s.kcShuffle, Modifier.weight(1f), color = WG.Surface2, contentColor = WG.Text) { vibe.tap(); st.reset() }
            }
        }
    }
    if (rules) Dialog(onDismissRequest = { rules = false }) {
        Column(Modifier.fillMaxWidth().heightIn(max = 560.dp).clip(RoundedCornerShape(24.dp)).background(WG.Surface).padding(18.dp)) {
            Text(s.kcRules, color = WG.Accent, fontSize = 20.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 8.dp))
            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) {
                for (i in 0 until 13) Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Text(PCard.RANKS[i + 1], color = WG.Accent, fontWeight = FontWeight.Black, modifier = Modifier.width(30.dp))
                    Text(KC_ICONS[i], fontSize = 20.sp, modifier = Modifier.width(34.dp))
                    Column(Modifier.weight(1f)) {
                        Text(names[i], color = WG.Text, fontWeight = FontWeight.Bold)
                        Text(descs[i], color = WG.SubText, fontSize = 13.sp)
                    }
                }
            }
            Box(Modifier.padding(top = 10.dp)) { BigButton(s.ok, Modifier.fillMaxWidth()) { rules = false } }
        }
    }
}
