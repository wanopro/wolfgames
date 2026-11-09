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
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

// v1.9 게임 — 🐺 늑대 야바위 (🍀 운빨) — 웹 games-v19.js 와 같은 방식

private fun easeIO(u0: Float): Float { val u = u0.coerceIn(0f, 1f); return if (u < 0.5f) 2 * u * u else 1 - (-2 * u + 2).pow(2) / 2 }
val YB_COLS = listOf("#FF5A6E", "#4FC3F7", "#FFD54F", "#69F0AE", "#BA68C8", "#FF9800", "#F48FB1", "#A1887F")
private const val TWO_PI = 7f

/** 🐺 야바위 늑대 (앞모습). open: 입 0~1, mood: 0 보통 · 1 통과(웃음) · -1 꽝(땀) */
fun Ctx.yWolf(x: Float, y: Float, r: Float, open: Float, mood: Int = 0, t: Float = 0f) {
    save(); translate(x, y)
    beginPath(); ellipse(0f, r * 1.02f, r * 0.85f, r * 0.16f, 0f, 0f, TWO_PI); fillStyle = "rgba(0,0,0,.35)"; fill()
    for (s in listOf(-1f, 1f)) {
        beginPath(); moveTo(s * r * 0.28f, -r * 0.62f); lineTo(s * r * 0.78f, -r * 1.25f); lineTo(s * r * 0.92f, -r * 0.3f); closePath(); fillStyle = "#7C8593"; fill()
        beginPath(); moveTo(s * r * 0.44f, -r * 0.6f); lineTo(s * r * 0.74f, -r * 1.02f); lineTo(s * r * 0.82f, -r * 0.42f); closePath(); fillStyle = "#F6AFC0"; fill()
    }
    beginPath(); ellipse(0f, 0f, r, r * 0.92f, 0f, 0f, TWO_PI); fillStyle = "#9AA3AF"; fill()
    for (s in listOf(-1f, 1f)) { beginPath(); moveTo(s * r * 0.92f, r * 0.05f); lineTo(s * r * 1.15f, r * 0.25f); lineTo(s * r * 0.9f, r * 0.42f); closePath(); fillStyle = "#9AA3AF"; fill() }
    for (s in listOf(-1f, 1f)) {
        if (mood == 1) { beginPath(); arc(s * r * 0.36f, -r * 0.2f, r * 0.13f, PI.toFloat() * 1.1f, PI.toFloat() * 1.9f); strokeStyle = "#2B2B33"; lineWidth = r * 0.07f; lineCap = "round"; stroke() }
        else { beginPath(); ellipse(s * r * 0.36f, -r * 0.2f, r * 0.13f, r * 0.15f, 0f, 0f, TWO_PI); fillStyle = "#2B2B33"; fill(); beginPath(); arc(s * r * 0.32f, -r * 0.26f, r * 0.05f, 0f, TWO_PI); fillStyle = "#fff"; fill() }
        beginPath(); ellipse(s * r * 0.62f, r * 0.16f, r * 0.13f, r * 0.08f, 0f, 0f, TWO_PI); fillStyle = "rgba(255,130,150,.55)"; fill()
    }
    beginPath(); ellipse(0f, r * 0.38f, r * 0.5f, r * 0.36f + open * r * 0.22f, 0f, 0f, TWO_PI); fillStyle = "#F1EEE8"; fill()
    beginPath(); ellipse(0f, r * 0.16f, r * 0.13f, r * 0.09f, 0f, 0f, TWO_PI); fillStyle = "#2B2B33"; fill()
    if (open > 0.04f) {
        beginPath(); ellipse(0f, r * 0.5f + open * r * 0.12f, r * 0.32f, r * 0.05f + open * r * 0.3f, 0f, 0f, TWO_PI); fillStyle = "#5A1E2B"; fill()
        fillStyle = "#fff"
        for (s in listOf(-1f, 1f)) { beginPath(); moveTo(s * r * 0.24f, r * 0.5f - open * r * 0.16f); lineTo(s * r * 0.14f, r * 0.5f - open * r * 0.16f); lineTo(s * r * 0.19f, r * 0.5f - open * r * 0.04f); closePath(); fill() }
        beginPath(); ellipse(0f, r * 0.62f + open * r * 0.2f, r * 0.14f, r * 0.07f, 0f, 0f, TWO_PI); fillStyle = "#F48FB1"; fill()
    } else {
        beginPath(); moveTo(-r * 0.16f, r * 0.36f); quadraticCurveTo(-r * 0.08f, r * 0.46f, 0f, r * 0.36f); quadraticCurveTo(r * 0.08f, r * 0.46f, r * 0.16f, r * 0.36f)
        strokeStyle = "#2B2B33"; lineWidth = r * 0.05f; lineCap = "round"; stroke()
    }
    if (mood == -1) { beginPath(); ellipse(r * 0.78f, -r * 0.55f + sin(t * 6) * 2, r * 0.09f, r * 0.14f, 0.3f, 0f, TWO_PI); fillStyle = "#4FC3F7"; fill() }
    restore()
}

private const val YB_EAT_END = 3.0f
private class P2(val x: Float, val y: Float)

private class YbPick(val t: Float, val p: Int, val bad: Boolean)
private class YbHit(val k: Int, val p: Int)

/** 늑대 수 = 인원 (1~8명, 1명이면 2마리). 꽝 늑대 1 ~ 인원-1마리, 꽝을 다 찾으면 끝 */
private class YabawiGame(val players: Int, losers: Int, val vibe: Vibe) {
    val n = max(2, players); val kk = losers.coerceIn(1, n - 1)
    var base = 0f; var empty = setOf<Int>(); var sw = listOf<Triple<Int, Int, Float>>(); var shEnd = 0f; var cur = 0
    val picked = HashMap<Int, YbPick>(); val hits = ArrayList<YbHit>(); var hitT = 0f; var cardOn = false; var overT = 0f; var eaten = 0; var swapIdx = -1
    var eat = listOf<Int>(); var step = 0f
    init { round(0f) }
    fun round(t: Float) {
        val l = ArrayList<Triple<Int, Int, Float>>(); var d = 0.62f
        fun pair(): Pair<Int, Int> { val a = Random.nextInt(n); var b = Random.nextInt(n - 1); if (b >= a) b++; return a to b }
        while (d > 0.1f) { val (a, b) = pair(); l.add(Triple(a, b, d)); d *= 0.92f }
        repeat(12) { val (a, b) = pair(); l.add(Triple(a, b, 0.1f)) }
        sw = l; shEnd = YB_EAT_END + l.sumOf { it.third.toDouble() }.toFloat()
        base = t; empty = (0 until n).shuffled().take(kk).toSet(); cur = 0; picked.clear(); hits.clear(); hitT = 0f; cardOn = false; overT = 0f; eaten = 0; swapIdx = -1
        eat = (0 until n).filter { it !in empty }; step = 1.6f / eat.size
    }
    fun over() = hits.size >= kk || cur >= players
    /** 자리: 1~3마리 한 줄, 4마리 2×2, 5·6마리 3+2·3×2, 7·8마리 4+3·4+4 */
    fun slots(w: Float, h: Float): Triple<List<P2>, Float, Float> {
        val rows = if (n <= 3) listOf(n) else if (n == 4) listOf(2, 2) else if (n <= 6) listOf(3, n - 3) else listOf(4, n - 4)
        val k = min(min(w / 390f, h / 700f), 1.5f); val r = (if (n <= 2) 64f else if (n <= 4) 58f else if (n <= 6) 52f else 38f) * k
        fun gap(cnt: Int) = if (cnt == 4) w * 0.235f else if (cnt == 3) w * 0.31f else w * 0.38f
        val out = ArrayList<P2>()
        rows.forEachIndexed { ri, cnt ->
            val y = if (rows.size == 1) h * 0.5f else h * (0.36f + ri * 0.33f)
            for (i in 0 until cnt) out.add(P2(w / 2 + (i - (cnt - 1) / 2f) * gap(cnt), y + if (cnt == 3 && i == 1) r * 0.5f else if (cnt == 4 && (i == 1 || i == 2)) r * 0.4f else 0f))
        }
        return Triple(out, r, k)
    }
    class Where(val out: List<P2>, val fast: Boolean, val j: Int)
    fun where(tt: Float, s0: List<P2>): Where {
        val pos = IntArray(n) { it }; var at = YB_EAT_END
        for ((j, sx) in sw.withIndex()) {
            val (a, b, d) = sx
            if (tt < at) break
            val wa = pos.indexOf(a); val wb = pos.indexOf(b)
            if (tt < at + d) {
                val u = easeIO((tt - at) / d); val out = pos.map { s0[it] }.toMutableList()
                val A = s0[a]; val B = s0[b]; val dx = B.x - A.x; val dy = B.y - A.y; val L = max(1e-3f, hypot(dx, dy)); val nx = -dy / L * 60; val ny = dx / L * 60; val sn = sin(u * PI.toFloat())
                out[wa] = P2(A.x + dx * u + nx * sn, A.y + dy * u + ny * sn); out[wb] = P2(B.x - dx * u - nx * sn, B.y - dy * u - ny * sn)
                return Where(out, d < 0.2f, j)
            }
            pos[wa] = b; pos[wb] = a; at += d
        }
        return Where(pos.map { s0[it] }, false, sw.size)
    }
    fun down(x: Float, y: Float, t: Float, w: Float, h: Float) {
        val tt = t - base
        if (over()) { if (t - (if (hits.isNotEmpty()) hitT else overT) > 1.2f) { round(t); vibe.tap() }; return }
        if (cardOn) { if (t - hitT > 1.2f) { cardOn = false; vibe.tap() }; return }
        if (tt < shEnd) return
        val (sl, r, _) = slots(w, h); val out = where(tt, sl).out
        var best = -1; var bd = r * 1.3f
        for (k in 0 until n) { if (picked.containsKey(k)) continue; val d = hypot(x - out[k].x, y - out[k].y); if (d < bd) { bd = d; best = k } }
        if (best < 0) return
        val bad = best in empty
        picked[best] = YbPick(t, cur, bad); cur++
        if (bad) { hits.add(YbHit(best, cur - 1)); hitT = t; cardOn = !over(); vibe.strong(); Beep.tone(90f, 0.7f, 2, 0.22f) }
        else { vibe.tap(); Beep.tone(1320f, 0.12f, 3, 0.18f) }
        if (over() && hits.isEmpty()) overT = t
    }
    private fun Ctx.card(x: Float, y: Float, sc: Float, title: String, line: String) {
        save(); translate(x, y); scale(sc, sc)
        beginPath(); roundRect(-165f, -82f, 330f, 164f, 26f); fillStyle = "rgba(12,12,18,.94)"; fill(); strokeStyle = "#FF5A6E"; lineWidth = 4f; stroke()
        text(title, 0f, -34f, 40f, "#FF5A6E")
        font(34f); val lw = measureText(line); text(line, 0f, 30f, max(16f, min(34f, 34f * 300f / max(1f, lw))), "#FFD740")
        restore()
    }
    fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings) {
        val tt = t - base; val (slot, r, sc) = slots(w, h); val wh = where(tt, slot); val out = wh.out
        fun eatAt(k: Int) = 0.6f + eat.indexOf(k) * step
        val eatenNow = if (tt < YB_EAT_END) eat.count { tt > eatAt(it) + 0.45f } else eat.size
        if (eatenNow > eaten) { eaten = eatenNow; Beep.tone(880f + eatenNow * 40, 0.08f, 3, 0.16f) }
        if (tt >= YB_EAT_END && tt < shEnd && wh.j != swapIdx) { swapIdx = wh.j; if (wh.j < sw.size && sw[wh.j].third > 0.2f) Beep.tone(320f, 0.05f, 0, 0.08f) }
        val isOver = over()
        // 사람 점 (지금 고를 사람 밝게, 벌칙 받은 사람 💀)
        val dotR = min(14f, w / (players * 3.2f)); val losers = hits.map { it.p }
        for (i in 0 until players) {
            val x = w / 2 + (i - (players - 1) / 2f) * dotR * 2.8f
            c.globalAlpha = if (tt > shEnd && !cardOn && !isOver && i == cur) 1f else if (i in losers) 0.9f else 0.35f
            c.beginPath(); c.arc(x, 34f, dotR, 0f, TWO_PI); c.fillStyle = YB_COLS[i]; c.fill(); c.text(if (i in losers) "💀" else "${i + 1}", x, 35f, dotR * 0.95f, "#000"); c.globalAlpha = 1f
        }
        // 늑대
        for (k in 0 until n) {
            val p = out[k]; val pk = picked[k]; val bad = k in empty
            var open = 0f; var mood = 0
            if (tt < YB_EAT_END) {
                open = min(1f, tt / 0.5f)
                if (!bad && tt > eatAt(k) + 0.45f) open = max(0f, 1 - (tt - eatAt(k) - 0.45f) / 0.2f)
                if (tt > 2.5f) open = max(0f, open - (tt - 2.5f) / 0.3f)
            }
            if (pk != null) { open = min(1f, (t - pk.t) / 0.25f); mood = if (bad) -1 else 1 }
            if (wh.fast && tt >= YB_EAT_END && tt < shEnd) { c.globalAlpha = 0.25f; c.yWolf(p.x + (Random.nextFloat() - 0.5f) * 16 * sc, p.y + (Random.nextFloat() - 0.5f) * 16 * sc, r, 0f); c.globalAlpha = 1f }
            val bob = if (tt > shEnd) sin(t * 5 + k) * 3 else 0f
            val big = if (pk != null && bad) 1 + 0.2f * min(1f, (t - pk.t) / 0.3f) else 1f
            c.yWolf(p.x, p.y + bob, r * big, open, mood, t)
            if (pk != null && t - pk.t > 0.3f) {
                if (!bad) c.coin(p.x, p.y + r * 0.55f - min(1f, (t - pk.t) / 0.4f) * 30 * sc, 16 * sc, 1)
                c.beginPath(); c.arc(p.x, p.y - r * 1.55f, 15 * sc, 0f, TWO_PI); c.fillStyle = YB_COLS[pk.p]; c.fill()
                c.text("${pk.p + 1}", p.x, p.y - r * 1.55f + 1, 15 * sc, "#000"); c.text(if (bad) "💀" else "✅", p.x + 26 * sc, p.y - r * 1.55f, 20 * sc, "#fff")
            }
        }
        // 날아가는 코인
        if (tt < YB_EAT_END) {
            for (k in eat) {
                val u = (tt - eatAt(k)) / 0.45f; if (u < 0 || u > 1) continue
                val x0 = w / 2; val y0 = 84f; val x1 = slot[k].x; val y1 = slot[k].y + r * 0.5f
                c.coin(x0 + (x1 - x0) * u, y0 + (y1 - y0) * u - sin(u * PI.toFloat()) * 80 * sc, (18 - 6 * u) * sc, 1, 1 - abs(sin(u * 9)) * 0.7f)
            }
            val left = eat.size - eat.count { tt > eatAt(it) }
            if (left > 0) { c.coin(w / 2, 84f, 18 * sc, 1); c.text("× $left", w / 2 + 44 * sc, 84f, 18 * sc, "#FFD740") }
        }
        // 안내 · 결과
        val fs = min(20f, w / 19f)
        if (hitT > 0 && (cardOn || (isOver && hits.isNotEmpty()))) {
            val u = t - hitT
            if (u > 0.4f) {
                val v = u - 0.4f; val k2 = (0.6f + 0.4f * min(1f, v / 0.25f)) * min(1f, w / 380f); val sh = if (v < 0.4f) sin(v * 60) * 7 * (1 - v / 0.4f) else 0f
                val shown = if (isOver) hits.toList() else listOf(hits.last()); val e = out[shown.last().k]; val cy = if (e.y > h / 2) max(110f, h * 0.2f) else h - 130
                c.fillStyle = "rgba(0,0,0,${0.55f * min(1f, v / 0.25f)})"; c.fillRect(0f, 0f, w, h)
                for (x in shown) c.yWolf(out[x.k].x, out[x.k].y, r * (1.25f + sin(t * 10) * 0.03f), 1f, -1, t)
                c.card(w / 2 + sh, cy, k2, s.ybHit, s.ybShotN(shown.joinToString(" · ") { "${it.p + 1}" }))
            }
            if (u > 1.2f) c.text(if (isOver) s.playAgain else s.ybPick(cur + 1), w / 2, h - 22, 15f, if (isOver) "#9A9AB0" else YB_COLS[cur], false)
        } else if (isOver) {
            c.text(s.ybPass(cur), w / 2, h - 70, fs, "#69F0AE")
            if (t - overT > 1f) c.text(s.playAgain, w / 2, h - 22, 15f, "#9A9AB0", false)
        } else if (tt < YB_EAT_END) c.text(s.ybEatingN(kk), w / 2, h - 60, fs, "#fff")
        else if (tt < shEnd) c.text(s.ybShuffle, w / 2, h - 60, fs, "#fff")
        else {
            val last = picked.values.firstOrNull { it.p == cur - 1 }
            if (last != null && !last.bad && t - last.t < 1.5f) c.text(s.ybPass(cur), w / 2, h - 60, fs, "#69F0AE")
            else c.text(s.ybPick(cur + 1), w / 2, h - 60, fs, YB_COLS[cur])
        }
    }
}

@Composable
fun YabawiScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var n by remember { mutableIntStateOf(prefs.getInt("yb_n", 4).coerceIn(1, 8)) }
    var e by remember { mutableIntStateOf(prefs.getInt("yb_e", 1).coerceIn(1, max(1, n - 1))) }
    SetupThen18(s.ybTitle, onBack, { start ->
        SetupPanel(s.ybDesc, start) {
            Stepper(s.players, n, 1..8) { n = it; prefs.edit().putInt("yb_n", it).apply(); if (e > max(1, it - 1)) { e = max(1, it - 1); prefs.edit().putInt("yb_e", e).apply() } }
            Stepper(s.ybLosers, e, 1..max(1, n - 1)) { e = it; prefs.edit().putInt("yb_e", it).apply() }
        }
    }) {
        val g = remember { YabawiGame(n, e, vibe) }
        Anim18(g, onDown = { x, y, t, w, h -> g.down(x, y, t, w, h) }) { c, w, h, t -> g.draw(c, w, h, t, s) }
    }
}
