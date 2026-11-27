package com.wolfgames.app.games

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.i18n.Strings
import com.wolfgames.app.ui.Ctx
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.Vibe
import com.wolfgames.app.ui.coin
import com.wolfgames.app.ui.text
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

// v2.0 게임 — 🎰 늑대 슬롯머신 (🍀 운빨) — 웹 games-v20.js 와 같은 방식
// 그림 5종: 0 늑대 · 1 코인 · 2 별 · 3 하트 · 4 해골(💀). 해골이 하나라도 → 벌칙 / 늑대 3개 → 다 같이 / 같은 그림 3개 → 지목 / 나머지 → 통과

private val SL_W = floatArrayOf(3f, 3f, 3f, 3f, 1.5f)
private const val SL_FULL = 2f * V20_PI

private fun slPick(): Int {
    var r = Random.nextFloat() * SL_W.sum()
    for (k in SL_W.indices) { r -= SL_W[k]; if (r < 0) return k }
    return 0
}
private fun slOutcome(): IntArray {
    val a = slPick(); val b = slPick(); var c3 = slPick()
    if (a == b && a != 4 && Random.nextFloat() < 0.4f) c3 = a
    return intArrayOf(a, b, c3)
}
/** "bad" 벌칙 · "all" 다 같이 · "pick" 지목 · "pass" 통과 */
private fun slJudge(r: IntArray): String {
    if (4 in r) return "bad"
    if (r[0] == r[1] && r[1] == r[2]) return if (r[0] == 0) "all" else "pick"
    return "pass"
}

private fun Ctx.slStar(x: Float, y: Float, r: Float, col: String) {
    beginPath()
    for (i in 0 until 10) {
        val a = i * V20_PI / 5 - V20_PI / 2; val rr = if (i % 2 == 1) r * 0.45f else r
        val px = x + cos(a) * rr; val py = y + sin(a) * rr
        if (i == 0) moveTo(px, py) else lineTo(px, py)
    }
    closePath(); fillStyle = col; fill(); lineWidth = r * 0.12f; strokeStyle = "#7A5A00"; stroke()
}
private fun Ctx.slHeart(x: Float, y: Float, s: Float, col: String) {
    beginPath(); moveTo(x, y + s * 0.35f)
    bezierCurveTo(x - s * 1.1f, y - s * 0.35f, x - s * 0.45f, y - s * 1.05f, x, y - s * 0.45f)
    bezierCurveTo(x + s * 0.45f, y - s * 1.05f, x + s * 1.1f, y - s * 0.35f, x, y + s * 0.35f)
    fillStyle = col; fill()
}
private fun Ctx.slSkull(x: Float, y: Float, r: Float) {
    beginPath(); arc(x, y - r * 0.1f, r * 0.78f, V20_PI * 0.85f, V20_PI * 2.15f); lineTo(x + r * 0.5f, y + r * 0.55f); lineTo(x - r * 0.5f, y + r * 0.55f); closePath()
    fillStyle = "#F3F3F3"; fill(); lineWidth = r * 0.08f; strokeStyle = "#555"; stroke()
    for (s in listOf(-1f, 1f)) { beginPath(); ellipse(x + s * r * 0.3f, y - r * 0.1f, r * 0.2f, r * 0.24f, 0f, 0f, SL_FULL); fillStyle = "#2B2B33"; fill() }
    beginPath(); moveTo(x, y + r * 0.1f); lineTo(x - r * 0.1f, y + r * 0.28f); lineTo(x + r * 0.1f, y + r * 0.28f); closePath(); fillStyle = "#2B2B33"; fill()
    for (k in listOf(-0.25f, 0f, 0.25f)) { beginPath(); moveTo(x + k * r, y + r * 0.42f); lineTo(x + k * r, y + r * 0.6f); lineWidth = r * 0.07f; strokeStyle = "#555"; stroke() }
}
private fun Ctx.slSymbol(k: Int, x: Float, y: Float, s: Float, t: Float = 0f) {
    when (k) {
        0 -> yWolf(x, y + s * 0.1f, s * 0.62f, 0.3f, 1, t)
        1 -> coin(x, y, s * 0.7f, 1)
        2 -> slStar(x, y, s * 0.8f, "#FFD54F")
        3 -> slHeart(x, y + s * 0.25f, s * 0.9f, "#FF4D6D")
        else -> slSkull(x, y, s * 0.85f)
    }
}

/** 릴 돌아갈 때 보이는 그림 (같은 칸은 늘 같은 그림) */
private fun slRnd(i: Int, r: Int): Float { val x = sin(i * 12.9898 + r * 78.233) * 43758.5453; return (x - floor(x)).toFloat() }

private class SlotGame(val players: Int, val vibe: Vibe) {
    // ts: 아직 한 번도 안 돌렸으면 아주 먼 미래 → st < 0 (처음 화면: 기본 그림 · 차례 안내)
    var cur = 0; var ts = 1e9f; var res = intArrayOf(0, 1, 2); var stops = floatArrayOf(0f, 0f, 0f)
    var judged: String? = null; var jt = 0f; var ticks = 0; var landed = 0

    fun spin(t: Float) {
        ts = t; res = slOutcome(); val slow = if (res[0] == res[1]) 1.1f else 0f
        stops = floatArrayOf(1.0f, 1.5f, 2.0f + slow); judged = null; landed = 0; ticks = 0
        Beep.tone(220f, 0.2f, 1, 0.12f); vibe.tap()
    }

    fun down(t: Float) {
        val st = t - ts
        if (st >= 0 && judged == null) return // 돌아가는 중
        if (judged != null) { if (t - jt < 0.8f) return; cur = (cur + 1) % players }
        spin(t)
    }

    fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings) {
        val st = t - ts; val spinning = st >= 0 && st < stops[2] + 0.3f
        c.turnDots(w, players, cur, 30f)
        val mw = min(min(w * 0.9f, 400f), h * 0.62f); val mh = mw * 0.98f; val mx = w / 2 - mw / 2 - mw * 0.04f; val my = max(64f, h * 0.46f - mh / 2)
        // 위 간판: 늑대 얼굴 + 깜빡이 불
        c.beginPath(); c.roundRect(mx + mw * 0.12f, my - mw * 0.08f, mw * 0.76f, mw * 0.2f, mw * 0.06f); c.fillStyle = "#B71C1C"; c.fill()
        for (i in 0 until 9) {
            val on = if (spinning) ((floor(t * 10).toInt() + i) % 2 == 0) else ((floor(t * 2).toInt() + i) % 3 == 0)
            c.beginPath(); c.arc(mx + mw * 0.17f + i * mw * 0.083f, my - mw * 0.08f + mw * 0.035f, mw * 0.018f, 0f, SL_FULL); c.fillStyle = if (on) "#FFE082" else "#6D1B1B"; c.fill()
        }
        c.yWolf(w / 2 - mw * 0.04f, my - mw * 0.02f, mw * 0.11f, if (spinning) 0.6f else 0.2f, 1, t)
        // 몸통
        c.beginPath(); c.roundRect(mx, my + mw * 0.1f, mw, mh * 0.8f, mw * 0.08f); c.fillStyle = "#E53935"; c.fill(); c.lineWidth = 6f; c.strokeStyle = "#7F1D1D"; c.stroke()
        c.beginPath(); c.roundRect(mx + mw * 0.04f, my + mw * 0.14f, mw * 0.92f, mh * 0.72f, mw * 0.06f); c.fillStyle = "#C62828"; c.fill()
        // 창 3칸
        val ww = mw * 0.84f; val wx = mx + mw * 0.08f; val wy = my + mw * 0.22f; val wh = mh * 0.44f; val cw = ww / 3
        c.beginPath(); c.roundRect(wx - 6, wy - 6, ww + 12, wh + 12, 14f); c.fillStyle = "#FFD54F"; c.fill()
        for (r in 0 until 3) {
            val x0 = wx + r * cw; val cx = x0 + cw / 2; val cy = wy + wh / 2; val sz = min(cw, wh) * 0.36f
            c.save(); c.beginPath(); c.roundRect(x0 + 3, wy, cw - 6, wh, 10f); c.fillStyle = "#FFFDF5"; c.fill(); c.clip()
            if (st >= 0 && st < stops[r]) { // 돌아가는 중
                val v = if (st < 0.25f) st / 0.25f else 1f; val pos = st * 14 * v; val i0 = floor(pos).toInt(); val f = pos - i0
                for (d in -1..1) { val k = floor(slRnd(i0 - d, r) * 5).toInt().coerceIn(0, 4); c.globalAlpha = 0.9f; c.slSymbol(k, cx, cy + (d + f) * wh * 0.55f, sz, t) }
                c.globalAlpha = 1f; c.fillStyle = "rgba(255,255,255,.25)"; c.fillRect(x0, wy, cw, wh)
                if (r == 2 && res[0] == res[1] && st > stops[1]) { c.fillStyle = "rgba(255,213,79,${0.25f + 0.2f * sin(t * 30)})"; c.fillRect(x0, wy, cw, wh) }
            } else { // 멈춤 (살짝 튕김)
                val k = if (st >= 0) res[r] else intArrayOf(0, 2, 1)[r]; val u = st - stops[r]
                val bo = if (st >= 0 && u < 0.35f) sin(u / 0.35f * V20_PI) * (1 - u / 0.35f) * wh * 0.12f else 0f
                val jd = judged
                val pulse = jd != null && jd != "pass" && (k == 4 || jd != "bad")
                c.slSymbol(k, cx, cy + bo, sz * (if (pulse) 1 + 0.08f * sin(t * 10) else 1f), t)
            }
            c.restore()
            // 멈출 때 소리
            if (st >= stops[r] && landed <= r && st < stops[2] + 1) { landed = r + 1; Beep.tone(140f + r * 40, 0.12f, 1, 0.14f); vibe.tap() }
        }
        // 가운데 선
        c.strokeStyle = "rgba(229,57,53,.5)"; c.lineWidth = 3f; c.beginPath(); c.moveTo(wx, wy + wh / 2); c.lineTo(wx + ww, wy + wh / 2); c.stroke()
        // 아래 동전 받이
        c.beginPath(); c.roundRect(mx + mw * 0.2f, my + mw * 0.1f + mh * 0.62f, mw * 0.6f, mh * 0.12f, 12f); c.fillStyle = "#7F1D1D"; c.fill()
        // 레버
        val lx = mx + mw + mw * 0.06f; val ly0 = my + mw * 0.3f
        val pull = if (st >= 0 && st < 0.45f) sin(st / 0.45f * V20_PI) else 0f; val kh = mh * 0.42f * (1 - pull * 1.6f)
        c.beginPath(); c.roundRect(lx - 9, ly0 + mh * 0.12f, 18f, mh * 0.34f, 8f); c.fillStyle = "#9E9E9E"; c.fill()
        c.lineWidth = 9f; c.strokeStyle = "#BDBDBD"; c.lineCap = "round"; c.beginPath(); c.moveTo(lx, ly0 + mh * 0.29f); c.lineTo(lx + 6, ly0 + mh * 0.29f - kh); c.stroke()
        c.beginPath(); c.arc(lx + 6, ly0 + mh * 0.29f - kh, mw * 0.06f, 0f, SL_FULL); c.fillStyle = "#FF1744"; c.fill()
        c.beginPath(); c.arc(lx + 3, ly0 + mh * 0.29f - kh - 4, mw * 0.02f, 0f, SL_FULL); c.fillStyle = "rgba(255,255,255,.7)"; c.fill()
        // 돌아가는 소리
        if (spinning && st < stops[2]) { val n = floor(st * 14).toInt(); if (n > ticks) { ticks = n; Beep.tone(900f, 0.02f, 1, 0.04f) } }
        // 판정
        if (st >= stops[2] + 0.3f && judged == null && st >= 0) {
            val j = slJudge(res); judged = j; jt = t
            if (j == "pass") Beep.tone(1320f, 0.15f, 3, 0.18f) else { vibe.strong(); Beep.tone(90f, 0.6f, 2, 0.2f) }
        }
        val fs = min(20f, w / 19f)
        val jd = judged
        if (jd != null) {
            val v = t - jt
            if (jd == "pass") c.text(s.slPass, w / 2, h - 60, fs * 1.3f, "#69F0AE")
            else c.resultCard(
                w, h, v,
                if (jd == "bad") s.shotWord else if (jd == "all") s.slAll else s.slPick,
                if (jd == "pick") "👉" else s.ybShotN(if (jd == "all") "🙌" else "${cur + 1}"),
                if (jd == "pick") "#4FC3F7" else "#FF5A6E", h - 120
            )
            if (v > 0.8f) c.text(s.turnN(((cur + 1) % players) + 1) + " 👉", w / 2, h - 22, 15f, YB_COLS[(cur + 1) % players], false)
        } else if (!spinning) c.text(s.turnN(cur + 1) + " — 🎰", w / 2, h - 60, fs, YB_COLS[cur])
    }
}

@Composable
fun SlotScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var n by remember { mutableIntStateOf(prefs.getInt("sl_n", 4).coerceIn(1, 8)) }
    SetupThen18(s.slTitle, onBack, { start ->
        SetupPanel(s.slDesc, start) {
            Stepper(s.players, n, 1..8) { n = it; prefs.edit().putInt("sl_n", it).apply() }
        }
    }) {
        val g = remember { SlotGame(n, vibe) }
        Anim18(g, onDown = { _, _, t, _, _ -> g.down(t) }) { c, w, h, t -> g.draw(c, w, h, t, s) }
    }
}
