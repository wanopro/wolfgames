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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

// ===================== 🧱 늑대 젠가 ===================== (웹 games-v20.js 와 같은 방식)
// 12층 × 3칸. 블록을 꾹 누르고 바깥으로 끌어서 빼기 (가운데 블록은 처음 끄는 쪽으로).
// 빨리 끌수록 흔들림 게이지가 참 (위험한 블록일수록 더 빨리). 블록마다 걸리는 곳(삐걱)이 1~2군데 — 거기선 아주 천천히.
// 2026-11-27: 빼는 중에 손가락이 위아래로 흔들리면(처음 잡은 높이에서 벗어나면) 게이지 참.
// 게이지가 꽉 차면 와르르. 끝까지 빼면 맨 위로 → 다음 사람. 중간에 손 떼면 블록이 다시 들어감 (같은 사람이 다시)

private const val JG_LAYERS = 12
private const val JG_VSAFE = 1.35f
private const val JG_VSNAG = 0.35f

/** 위험도: danger(게이지 차는 배율), lethal(층이 비면 반쯤 빼는 순간 무너짐) — 2026-11-27 한 층 2칸으로 */
private class JgDanger(val lethal: Boolean, val danger: Float)

private fun jgDanger(layers: List<IntArray>, li: Int, k: Int, pulls: Int): JgDanger {
    val l = layers[li].copyOf(); l[k] = 0; val n = l.sum()
    val lethal = n == 0
    return JgDanger(lethal, 1.3f + pulls * 0.06f + (if (li < 3) 0.25f else 0f))
}

private fun jgCanPull(layers: List<IntArray>, li: Int): Boolean {
    val topFull = layers[layers.size - 1].all { it != 0 }
    val maxLi = layers.size - (if (topFull) 2 else 3)
    return li <= maxLi
}

private class JgRect(val x: Float, val y: Float, val w: Float, val h: Float)

private class JgGeo(val bh: Float, val bw: Float, val baseY: Float, val cx: Float) {
    fun rect(li: Int, k: Int) = JgRect(cx + (k - 1f) * bw, baseY - (li + 1) * bh, bw, bh)
}

/** 잡고 끄는 중인 블록. dir 0 = 가운데 블록이 아직 방향 안 정함 */
private class JgGrab(
    val li: Int, val k: Int, var dir: Int, val x0: Float, val danger: Float, val lethal: Boolean,
    val snags: FloatArray, val bw: Float, val y0: Float,
) {
    var p = 0f; var pPrev = 0f; val hit = BooleanArray(2); var fy = y0; var off = false
}

/** 손 떼서 다시 들어가는 블록 */
private class JgBack(val li: Int, val k: Int, val dir: Int, val p0: Float, val t: Float)

/** 다 빼서 맨 위로 날아가는 블록 */
private class JgFly(val li: Int, val k: Int, val dir: Int, val toLi: Int, val toK: Int, val t: Float, val after: List<IntArray>)

private class JgDebris(val x: Float, val y: Float, val vx: Float, val vy: Float, val vr: Float, val li: Int, val k: Int)

private class JgWolf(val x: Float, val y: Float, val vx: Float, val vy: Float)

private class JengaGame(val players: Int, val vibe: Vibe) {
    var layers: List<IntArray> = emptyList(); var cur = 0; var pulls = 0; var stress = 0f
    var grab: JgGrab? = null; var back: JgBack? = null; var fly: JgFly? = null
    var fallT = 0f; var lastT = 0f; var creakT = -9f
    val debris = ArrayList<JgDebris>(); var wolf: JgWolf? = null; var loser = 0

    init { reset() }

    fun reset() {
        layers = List(JG_LAYERS) { intArrayOf(1, 1) }; cur = 0; pulls = 0; stress = 0f
        grab = null; back = null; fly = null; fallT = 0f; debris.clear(); wolf = null; lastT = 0f; creakT = -9f
    }

    fun geo(w: Float, h: Float): JgGeo {
        val n = layers.size + 1; val bh = min(30f, (h * 0.66f) / n); val bw = min(min(w * 0.3f, 130f), bh * 4.4f)
        return JgGeo(bh, bw, h - 64, w / 2)
    }

    private fun Ctx.blockDraw(x: Float, y: Float, w: Float, hh: Float, li: Int, hot: Boolean = false) {
        val b0 = if (li % 2 != 0) "#E7B77A" else "#F2C98E"; val b1 = if (li % 2 != 0) "#C98B4A" else "#D69B57"
        beginPath(); roundRect(x + 1.5f, y + 1.5f, w - 3, hh - 3, 4f); linearStops(0f, y, 0f, y + hh, listOf(0f to b0, 1f to b1)); fill()
        lineWidth = if (hot) 3f else 1.5f; strokeStyle = if (hot) "#FFD740" else "#8D5A2B"; stroke()
        strokeStyle = "rgba(141,90,43,.35)"; lineWidth = 1f
        if (li % 2 != 0) {
            beginPath(); arc(x + w / 2, y + hh / 2, hh * 0.28f, 0f, V20_TAU); stroke()
            beginPath(); arc(x + w / 2, y + hh / 2, hh * 0.14f, 0f, V20_TAU); stroke()
        } else for (f in listOf(0.35f, 0.62f)) {
            beginPath(); moveTo(x + 4, y + hh * f); quadraticCurveTo(x + w / 2, y + hh * (f + 0.12f), x + w - 4, y + hh * f); stroke()
        }
    }

    private fun collapse(t: Float, g: JgGeo, li: Int) {
        fallT = t; loser = cur; vibe.strong(); Beep.tone(70f, 0.9f, 2, 0.24f)
        val topY = g.baseY - layers.size * g.bh; val gr = grab
        for (l in li until layers.size) for (k in 0 until 2) if (layers[l][k] != 0) {
            val r = g.rect(l, k)
            val off = if (gr != null && gr.li == l && gr.k == k) gr.dir * gr.p * g.bw else 0f
            debris.add(JgDebris(r.x + off, r.y, (Random.nextFloat() - 0.5f) * 260 + (l - li) * (if (Random.nextFloat() < 0.5f) -12 else 12), -Random.nextFloat() * 160, (Random.nextFloat() - 0.5f) * 8, l, k))
        }
        wolf = JgWolf(g.cx, topY - g.bh * 1.25f, (Random.nextFloat() - 0.5f) * 200, -260f)
        layers = layers.take(li); grab = null; back = null
    }

    private fun snagIdx(gr: JgGrab): Int { for (i in gr.snags.indices) if (abs(gr.p - gr.snags[i]) < 0.06f) return i; return -1 }

    fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings) {
        val g = geo(w, h); val dt = min(0.05f, max(0.001f, t - lastT)); lastT = t
        c.turnDots(w, players, cur, 30f)
        // 흔들림 게이지 계산
        val gr = grab; var snag = false
        if (gr != null && fallT == 0f) {
            val v = max(0f, (gr.p - gr.pPrev) / dt); gr.pPrev = gr.p
            val si = snagIdx(gr); snag = si >= 0
            val vs = if (snag) JG_VSNAG else JG_VSAFE; val over = max(0f, v - vs)
            stress += over * 0.55f * gr.danger * dt * 1.6f
            if (snag && over > 0f && !gr.hit[si]) { gr.hit[si] = true; stress += 0.12f * gr.danger; creakT = t; Beep.tone(180f, 0.15f, 2, 0.12f); vibe.tap() }
            if (snag && t - creakT > 0.25f && v > 0.05f) { creakT = t; Beep.tone(240f + Random.nextFloat() * 60, 0.06f, 1, 0.06f) }
            // 위아래: 빼는 중에 손가락이 위아래로 움직이면(처음 잡은 높이에서 벗어나면) 게이지 참
            val tol = g.bh * 0.45f; val err = abs(gr.fy - gr.y0); gr.off = err > tol
            if (gr.off) {
                stress += min(2.5f, err / tol - 1f + 0.4f) * 0.5f * gr.danger * dt
                if (t - creakT > 0.3f) { creakT = t; Beep.tone(150f + Random.nextFloat() * 40, 0.06f, 1, 0.07f) }
            } else if (v < 0.2f) stress = max(0f, stress - 0.18f * dt)
            if (gr.lethal && gr.p > 0.55f) stress = 1f
            if (stress >= 1f) collapse(t, g, gr.li)
            else if (gr.p >= 1f) { // 다 뺐다 → 맨 위로
                val after = ArrayList<IntArray>(layers.map { it.copyOf() }); after[gr.li][gr.k] = 0
                var toLi = after.size - 1; if (after[toLi].all { it != 0 }) { after.add(intArrayOf(0, 0)); toLi++ }
                val toK = after[toLi].indexOf(0)
                fly = JgFly(gr.li, gr.k, gr.dir, toLi, toK, t, after); grab = null; pulls++; Beep.tone(660f, 0.08f, 3, 0.14f); vibe.tap()
            }
        } else if (fallT == 0f) stress = max(0f, stress - 0.3f * dt)
        // 바닥
        c.fillStyle = "#3E3E52"; c.beginPath(); c.roundRect(g.cx - g.bw * 1.5f, g.baseY, g.bw * 3f, 14f, 7f); c.fill()
        // 탑 (게이지만큼 흔들)
        val st = stress; val gb = grab; val fy = fly
        val sway = if (fallT != 0f) 0f else st.pow(1.5f) * 0.07f * sin(t * 14) + (if (snag && gb != null) sin(t * 60) * 0.004f else 0f)
        val bob = if (fallT != 0f) 0f else st.pow(1.5f) * 4f * sin(t * 23) + (if (gb != null && gb.off) sin(t * 50) * 1.5f else 0f)
        c.save(); c.translate(g.cx, g.baseY + bob); c.rotate(sway); c.translate(-g.cx, -g.baseY)
        for (li in layers.indices) for (k in 0 until 2) {
            if (layers[li][k] == 0) continue
            val r = g.rect(li, k)
            if (gb != null && gb.li == li && gb.k == k) { c.blockDraw(r.x + gb.dir * gb.p * g.bw * 1.05f, r.y + ((gb.fy - gb.y0) * 0.3f).coerceIn(-g.bh * 0.3f, g.bh * 0.3f) + (if (snag) sin(t * 70) * 1.2f else 0f), r.w, r.h, li, true); continue }
            if (fy != null && fy.li == li && fy.k == k) continue
            val bk = back
            if (bk != null && bk.li == li && bk.k == k) {
                val u = min(1f, (t - bk.t) / 0.25f); val pp = bk.p0 * (1 - v20EaseOut(u))
                c.blockDraw(r.x + bk.dir * pp * g.bw * 1.05f, r.y, r.w, r.h, li); if (u >= 1f) back = null; continue
            }
            c.blockDraw(r.x, r.y, r.w, r.h, li)
        }
        val topY = g.baseY - layers.size * g.bh
        if (fallT == 0f) c.yWolf(g.cx, topY - g.bh * 1.25f, g.bh * 1.1f, if (st > 0.5f) 0.6f else 0f, if (st > 0.5f) -1 else 1, t)
        c.restore()
        // 손가락 높이 점 (처음 잡은 높이 가로선에서 벗어나면 🔴)
        if (gb != null && fallT == 0f) {
            val r = g.rect(gb.li, gb.k); val cy0 = r.y + r.h / 2 + bob; val fx = r.x + r.w / 2 + gb.dir * gb.p * g.bw * 1.05f; val fyy = cy0 + (gb.fy - gb.y0)
            c.lineWidth = 2f; c.strokeStyle = "rgba(255,255,255,.35)"
            for (d in listOf(-1f, 1f)) { c.beginPath(); c.moveTo(fx - 16, cy0 + d * g.bh * 0.45f); c.lineTo(fx + 16, cy0 + d * g.bh * 0.45f); c.stroke() }
            c.beginPath(); c.arc(fx, fyy, 7f, 0f, V20_TAU); c.fillStyle = if (gb.off) "#FF5A6E" else "#69F0AE"; c.fill(); c.lineWidth = 2f; c.strokeStyle = "#fff"; c.stroke()
        }
        // 삐걱 표시 (지그재그)
        if (gb != null && snag) {
            val r = g.rect(gb.li, gb.k); val x = r.x + r.w / 2 + gb.dir * (gb.p * g.bw * 1.05f + r.w * 0.6f)
            c.strokeStyle = "#FFD740"; c.lineWidth = 3f
            for (d in listOf(-1, 1)) {
                c.beginPath()
                for (i in 0 until 4) { val px = x + gb.dir * i * 6f; val py = r.y + r.h / 2 + d * (8f + (i % 2) * 6f); if (i == 0) c.moveTo(px, py) else c.lineTo(px, py) }
                c.stroke()
            }
        }
        // 위로 올라가는 블록
        if (fy != null) {
            val u = v20Ease((t - fy.t) / 0.4f); val r = g.rect(fy.li, fy.k); val d = g.rect(fy.toLi, fy.toK); val sx = r.x + fy.dir * g.bw * 1.05f
            c.blockDraw(sx + (d.x - sx) * u, r.y + (d.y - r.y) * u - sin(u * V20_PI) * 60, r.w, r.h, fy.toLi)
            if (t - fy.t >= 0.4f) { fy.after[fy.toLi][fy.toK] = 1; layers = fy.after; fly = null; cur = (cur + 1) % players; stress *= 0.5f }
        }
        // 흔들림 게이지 (사람 점 아래)
        if (fallT == 0f) {
            val bw = min(220f, w * 0.55f); val x0 = w / 2 - bw / 2; val y0 = 54f
            c.beginPath(); c.roundRect(x0, y0, bw, 12f, 6f); c.fillStyle = "#2A2A3A"; c.fill()
            val col = if (st < 0.5f) "#69F0AE" else if (st < 0.8f) "#FFD740" else "#FF5A6E"
            c.beginPath(); c.roundRect(x0, y0, max(12f, bw * min(1f, st)), 12f, 6f); c.fillStyle = col; c.fill()
            c.text(if (st > 0.8f && sin(t * 16) > 0) "⚠️" else "〰️", x0 - 16, y0 + 6, 16f, "#fff")
        }
        if (fallT != 0f) {
            val u = t - fallT; val gy = 900f
            for (d in debris) {
                val x = d.x + d.vx * u; val y = min(g.baseY - g.bh, d.y + d.vy * u + 0.5f * gy * u * u)
                c.save(); c.translate(x + g.bw / 2, y + g.bh / 2); c.rotate(d.vr * min(u, 0.9f)); c.blockDraw(-g.bw / 2, -g.bh / 2, g.bw, g.bh, d.li); c.restore()
            }
            val wf = wolf
            if (wf != null) {
                c.save(); c.translate(wf.x + wf.vx * u, min(g.baseY - g.bh * 1.2f, wf.y + wf.vy * u + 0.5f * gy * u * u)); c.rotate(u * 6); c.yWolf(0f, 0f, g.bh * 1.1f, 1f, -1, t); c.restore()
            }
            if (u > 0.9f) c.resultCard(w, h, u - 0.9f, "💥 " + s.shotWord, s.ybShotN("${loser + 1}"), "#FF5A6E", h * 0.36f)
            if (u > 2f) c.text(s.playAgain, w / 2, h - 22, 15f, "#9A9AB0", false)
        } else if (grab == null && fly == null) c.text(s.turnN(cur + 1) + " 👆 ⇄", w / 2, h - 24, min(18f, w / 20f), YB_COLS[cur])
    }

    fun down(x: Float, y: Float, t: Float, w: Float, h: Float) {
        if (fallT != 0f) { if (t - fallT > 2f) { reset(); vibe.tap() }; return }
        if (fly != null || grab != null) return
        val g = geo(w, h)
        for (li in layers.size - 1 downTo 0) for (k in 0 until 2) {
            if (layers[li][k] == 0) continue
            val r = g.rect(li, k)
            if (x >= r.x && x <= r.x + r.w && y >= r.y - 3 && y <= r.y + r.h + 3) {
                if (!jgCanPull(layers, li)) { Beep.tone(200f, 0.08f, 1, 0.1f); vibe.tap(); return }
                val bk = back; if (bk != null && bk.li == li && bk.k == k) back = null
                val d = jgDanger(layers, li, k, pulls); val nS = if (Random.nextFloat() < 0.5f) 1 else 2
                val snags = FloatArray(nS) { 0.18f + Random.nextFloat() * 0.62f }
                grab = JgGrab(li, k, if (k == 0) -1 else 1, x, d.danger, d.lethal, snags, g.bw, y)
                vibe.tap(); Beep.tone(520f, 0.05f, 3, 0.1f)
                return
            }
        }
    }

    /** 누른 채로 끌기 (가로 = 빼기, 세로 = 길 따라가기) */
    fun move(x: Float, y: Float) {
        val gr = grab ?: return; if (fallT != 0f) return
        val dx = x - gr.x0; gr.fy = y
        if (gr.dir == 0) {
            if (abs(dx) <= 6f) return
            gr.dir = if (dx > 0f) 1 else -1
        }
        gr.p = ((dx * gr.dir) / (gr.bw * 1.05f)).coerceIn(0f, 1f)
    }

    /** 손 뗌 → 블록이 다시 들어가고 같은 사람이 다시 */
    fun up(t: Float) {
        val gr = grab ?: return; if (fallT != 0f) return
        if (gr.dir != 0) back = JgBack(gr.li, gr.k, gr.dir, gr.p, t)
        grab = null
    }
}

@Composable
fun JengaScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var n by remember { mutableIntStateOf(prefs.getInt("jg_n", 4).coerceIn(1, 8)) }
    SetupThen18(s.jgTitle, onBack, { start ->
        SetupPanel(s.jgDesc, start) {
            Stepper(s.players, n, 1..8) { n = it; prefs.edit().putInt("jg_n", it).apply() }
        }
    }) {
        val g = remember { JengaGame(n, vibe) }
        AnimPtr(g, onDown = { x, y, t, w, h -> g.down(x, y, t, w, h) }, onMove = { x, y, _, _ -> g.move(x, y) }, onUp = { t -> g.up(t) }) { c, w, h, t -> g.draw(c, w, h, t, s) }
    }
}
