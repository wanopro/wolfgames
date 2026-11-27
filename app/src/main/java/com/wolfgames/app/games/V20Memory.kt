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
import com.wolfgames.app.ui.text
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

// v2.0 게임 — 🃏 늑대 짝 맞추기 (⚔️ 대결) — 웹 games-v20.js 와 같은 방식
// 카드 앞면: 같은 늑대 + 머리 위 그림 (같은 그림끼리 짝), 배경색도 짝마다 다름

private val MM_ICONS = listOf("👑", "🎩", "🎀", "🕶️", "⭐", "🌸", "🍀", "❤️", "🎧", "🧢")
private val MM_BG = listOf("#FFE082", "#B39DDB", "#F8BBD0", "#90CAF9", "#FFF59D", "#F48FB1", "#A5D6A7", "#EF9A9A", "#80DEEA", "#FFCC80")

private fun mmDeal(pairs: Int, rnd: Random = Random): List<Int> {
    val a = ArrayList<Int>(); for (i in 0 until pairs) { a.add(i); a.add(i) }
    for (i in a.size - 1 downTo 1) { val j = rnd.nextInt(i + 1); val tmp = a[i]; a[i] = a[j]; a[j] = tmp }
    return a
}

private class MmGone(val t: Float, val p: Int)
private class MmPos(val x: Float, val y: Float)
private class MmLayout(val cols: Int, val rows: Int, val cw: Float, val chh: Float, val n: Int, val w: Float, val top0: Float, val ah: Float) {
    fun pos(i: Int): MmPos {
        val r = i / cols; val inRow = if (r == rows - 1) n - r * cols else cols; val cc = i - r * cols
        return MmPos(w / 2 + (cc - (inRow - 1) / 2f) * cw - cw / 2, top0 + (ah - rows * chh) / 2 + r * chh)
    }
}

private class MemoryGame(val players: Int, val pairs: Int, val vibe: Vibe) {
    var deck = listOf<Int>(); val up = ArrayList<Int>(); val flipT = HashMap<Int, Float>(); val gone = HashMap<Int, MmGone>()
    var cur = 0; var score = IntArray(players); var busyT = 0f; var endT = 0f; var lastMatch = -9f; var endSnd = false
    init { reset() }
    fun reset() {
        deck = mmDeal(pairs); up.clear(); flipT.clear(); gone.clear()
        cur = 0; score = IntArray(players); busyT = 0f; endT = 0f; lastMatch = -9f; endSnd = false
    }

    fun layout(w: Float, h: Float): MmLayout {
        val n = deck.size; val cols = if (n <= 16) 4 else 5; val rows = (n + cols - 1) / cols
        val top0 = 86f; val aw = w - 24; val ah = h - top0 - 70
        val cw = min(aw / cols, (ah / rows) * 0.78f); val chh = cw / 0.78f
        return MmLayout(cols, rows, cw, chh, n, w, top0, ah)
    }

    private fun Ctx.face(k: Int, x: Float, y: Float, w: Float, hh: Float, t: Float) {
        beginPath(); roundRect(x, y, w, hh, 10f); fillStyle = "#fff"; fill(); lineWidth = 2.5f; strokeStyle = "#2B2B33"; stroke()
        beginPath(); arc(x + w / 2, y + hh * 0.56f, w * 0.36f, 0f, V20_TAU); fillStyle = MM_BG[k]; fill()
        yWolf(x + w / 2, y + hh * 0.6f, w * 0.24f, 0.2f, 1, t)
        text(MM_ICONS[k], x + w / 2, y + hh * 0.22f, w * 0.3f, "#000", false)
    }

    private fun Ctx.backSide(x: Float, y: Float, w: Float, hh: Float) {
        beginPath(); roundRect(x, y, w, hh, 10f); linearStops(x, y, x + w, y + hh, listOf(0f to "#3949AB", 1f to "#1A237E")); fill(); lineWidth = 2.5f; strokeStyle = "#0D1240"; stroke()
        beginPath(); roundRect(x + 5, y + 5, w - 10, hh - 10, 7f); strokeStyle = "rgba(255,255,255,.35)"; lineWidth = 1.5f; stroke()
        val px = x + w / 2; val py = y + hh / 2; val r = w * 0.14f; fillStyle = "rgba(255,255,255,.85)"
        beginPath(); ellipse(px, py + r * 0.5f, r * 1.05f, r * 0.85f, 0f, 0f, V20_TAU); fill()
        for ((dx, dy) in listOf(-1.05f to -0.55f, -0.38f to -1.15f, 0.38f to -1.15f, 1.05f to -0.55f)) { beginPath(); ellipse(px + dx * r, py + dy * r, r * 0.38f, r * 0.46f, 0f, 0f, V20_TAU); fill() }
    }

    fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings) {
        c.turnDots(w, players, if (endT > 0f) -1 else cur, 30f) { i, x, y -> c.text("⭐" + score[i], x, y + 4, 13f, "#FFD740") }
        val L = layout(w, h)
        for (i in deck.indices) {
            val p = L.pos(i); val gn = gone[i]
            if (gn != null && t - gn.t > 0.9f) continue
            val ft = flipT[i]; val isUp = up.contains(i) || gn != null
            var sx = 1f; var showFace = isUp
            if (ft != null && t - ft < 0.25f && gn == null) { val u = (t - ft) / 0.25f; sx = abs(cos(u * V20_PI)); showFace = if (u < 0.5f) !isUp else isUp }
            c.save(); c.translate(p.x + L.cw / 2, p.y + L.chh / 2)
            if (gn != null) {
                val u = max(0f, (t - gn.t - 0.4f) / 0.5f); c.globalAlpha = 1 - u
                val k = 1 + 0.12f * sin(min(1f, (t - gn.t) / 0.4f) * V20_PI); c.scale(k * (1 - u * 0.4f), k * (1 - u * 0.4f))
            }
            c.scale(sx, 1f)
            val cw = L.cw - 8; val hh = L.chh - 8
            if (showFace) c.face(deck[i], -cw / 2, -hh / 2, cw, hh, t) else c.backSide(-cw / 2, -hh / 2, cw, hh)
            if (gn != null && t - gn.t < 0.4f) { c.beginPath(); c.roundRect(-cw / 2, -hh / 2, cw, hh, 10f); c.lineWidth = 5f; c.strokeStyle = YB_COLS[gn.p]; c.stroke() }
            c.restore(); c.globalAlpha = 1f
        }
        // 판정 (2장 뒤집고 0.9초 뒤)
        if (up.size == 2 && busyT > 0f && t - busyT > 0.9f) {
            val a = up[0]; val b = up[1]
            if (deck[a] == deck[b]) {
                gone[a] = MmGone(t, cur); gone[b] = MmGone(t, cur); score[cur]++; lastMatch = t
                Beep.tone(880f, 0.08f, 3, 0.16f); Beep.tone(1320f, 0.12f, 3, 0.16f); vibe.tap()
            } else { flipT[a] = t; flipT[b] = t; cur = (cur + 1) % players; Beep.tone(240f, 0.12f, 1, 0.1f) }
            up.clear(); busyT = 0f
            if (gone.size == deck.size) endT = t + 0.9f
        }
        if (endT > 0f && t > endT) {
            val mn = score.min(); val losers = score.indices.filter { score[it] == mn }.map { it + 1 }
            if (!endSnd) { endSnd = true; vibe.strong(); Beep.tone(90f, 0.6f, 2, 0.2f) }
            c.resultCard(w, h, t - endT, s.shotWord, s.ybShotN(losers.joinToString(" · ")))
            if (t - endT > 1.2f) c.text(s.playAgain, w / 2, h - 22, 15f, "#9A9AB0")
        } else if (endT == 0f) {
            val again = t - lastMatch < 1.2f
            c.text(if (again) "⭐ +1 👍" else s.turnN(cur + 1), w / 2, h - 40, min(20f, w / 19f), if (again) "#FFD740" else YB_COLS[cur])
        }
    }

    fun down(x: Float, y: Float, t: Float, w: Float, h: Float) {
        if (endT > 0f) { if (t - endT > 1.2f) { reset(); vibe.tap() }; return }
        if (up.size >= 2) return
        val L = layout(w, h)
        for (i in deck.indices) {
            if (gone.containsKey(i) || up.contains(i)) continue
            val p = L.pos(i)
            if (x >= p.x && x <= p.x + L.cw && y >= p.y && y <= p.y + L.chh) {
                up.add(i); flipT[i] = t; vibe.tap(); Beep.tone(600f, 0.04f, 3, 0.1f)
                if (up.size == 2) busyT = t
                return
            }
        }
    }
}

@Composable
fun MemoryScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var n by remember { mutableIntStateOf(prefs.getInt("mm_n", 3).coerceIn(2, 6)) }
    var pr by remember { mutableIntStateOf(prefs.getInt("mm_p", 8).coerceIn(4, 10)) }
    SetupThen18(s.mmTitle, onBack, { start ->
        SetupPanel(s.mmDesc, start) {
            Stepper(s.players, n, 2..6) { n = it; prefs.edit().putInt("mm_n", it).apply() }
            Stepper(s.mmPairs, pr, 4..10) { pr = it; prefs.edit().putInt("mm_p", it).apply() }
        }
    }) {
        val g = remember { MemoryGame(n, pr, vibe) }
        Anim18(g, onDown = { x, y, t, w, h -> g.down(x, y, t, w, h) }) { c, w, h, t -> g.draw(c, w, h, t, s) }
    }
}
