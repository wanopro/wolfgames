package com.wolfgames.app.games

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.i18n.Strings
import com.wolfgames.app.ui.ChoiceRow
import com.wolfgames.app.ui.Ctx
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.PLAYER_HEX
import com.wolfgames.app.ui.Sector
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.SplitCanvas
import com.wolfgames.app.ui.SplitGame
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.TAU
import com.wolfgames.app.ui.Vibe
import com.wolfgames.app.ui.badge
import com.wolfgames.app.ui.bombArt
import com.wolfgames.app.ui.chompWolf
import com.wolfgames.app.ui.die
import com.wolfgames.app.ui.hub
import com.wolfgames.app.ui.region
import com.wolfgames.app.ui.splitLines
import com.wolfgames.app.ui.text
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

// 두 언어일 때 위아래로 붙은 글자 사이를 조금 더 벌림
private fun du(s: com.wolfgames.app.i18n.Strings) = if (com.wolfgames.app.i18n.SEP in s.start) 9f else 0f

// 순발력게임 3종(반응속도 대결 · 색 반응 대결 · 스탑워치) + 폭탄돌리기 + 주사위 대결
// 모두 원형 화면 분할 (웹 games-quick.js 와 같은 규칙)

private const val GOLD = "#FFC107"
private const val RED = "#FF3D5A"

private class Rank(val glow: String, val tag: String, val bg: String, val fg: String)
private fun rankStyle(s: Strings, rank: Int, last: Int) = when (rank) {
    0 -> Rank(GOLD, s.rankFirst, GOLD, "#000")
    last -> Rank(RED, s.rankLast, RED, "#fff")
    else -> null
}
private fun Ctx.pname(i: Int, sc: Float) = text("P${i + 1}", 0f, -78 * sc, 20 * sc, PLAYER_HEX[i])
private fun Ctx.againHint(w: Float, h: Float, s: Strings, show: Boolean) { if (show) text(s.tapHubAgain, w / 2, h / 2 + 62, 13f, "#fff") }
private fun f(v: Float, d: Int) = "%.${d}f".format(v)

/** 설정 화면 → 분할 화면 게임. makeGame 은 인원 등 설정값으로 게임을 만듦 */
@Composable
private fun SplitGameScreen(title: String, onBack: () -> Unit, setup: @Composable (start: () -> Unit) -> Unit, makeGame: (Vibe) -> SplitGame) {
    var started by rememberSaveable { mutableStateOf(false) }
    val vibe = LocalVibe.current
    if (!started) {
        GameScaffold(title, onBack) { setup { started = true } }
    } else {
        BackHandler { started = false }
        val game = remember { makeGame(vibe) }
        GameScaffold(title, onBack = { started = false }) { SplitCanvas(game) }
    }
}

// ===================== ⚡ 반응속도 대결 =====================
private class ReactionGame(override val n: Int, val vibe: Vibe) : SplitGame {
    var goAt = 0f; var tap = arrayOfNulls<Float>(n); var foul = BooleanArray(n); var doneAt = 0f; var buzz = false
    init { round(0f) }
    fun round(t: Float) { goAt = t + 2f + Random.nextFloat() * 3f; tap = arrayOfNulls(n); foul = BooleanArray(n); doneAt = 0f; buzz = false }
    private fun has(i: Int) = tap[i] != null || foul[i]
    override fun tap(i: Int, t: Float) {
        if (has(i)) return
        if (t < goAt) foul[i] = true else tap[i] = t - goAt
        vibe.tap()
        if ((0 until n).all { has(it) }) { doneAt = t; vibe.strong() }
    }
    override fun hub(t: Float) { if (doneAt > 0) { round(t); vibe.tap() } }
    override fun draw(ctx: Ctx, w: Float, h: Float, t: Float, secs: List<Sector>, s: Strings) {
        val go = t >= goAt; val done = doneAt > 0
        if (go && !buzz) { buzz = true; vibe.tick() }
        val order = (0 until n).filter { tap[it] != null }.sortedBy { tap[it] } + (0 until n).filter { foul[it] }
        secs.forEachIndexed { i, sec ->
            val st = if (done) rankStyle(s, order.indexOf(i), n - 1) else null
            val fill = if (foul[i]) "#4A1820" else if (!go) "#3B2A2E" else if (tap[i] != null) "#1F6B43" else "#2ECC71"
            ctx.region(sec, fill, st?.glow) { sc ->
                pname(i, sc)
                when {
                    foul[i] -> { text(s.reactFoul, 0f, (-10 - du(s)) * sc, 30 * sc, "#FF8A80"); text(s.reactFoulSub, 0f, (30 + du(s)) * sc, 16 * sc, "#FFCDD2", false) }
                    tap[i] != null -> text(s.secs(f(tap[i]!!, 3)), 0f, -6 * sc, 44 * sc, "#fff")
                    go -> text(s.reactGo, 0f, -6 * sc, 40 * sc, "#0B3D22")
                    else -> { text(s.reactWait, 0f, (-10 - du(s)) * sc, 30 * sc, "#F2C9CF"); text(s.reactWaitSub, 0f, (28 + du(s)) * sc, 16 * sc, "#C9A3AA", false) }
                }
                if (st != null) badge(st.tag, 0f, 52 * sc, sc, st.bg, st.fg)
            }
        }
        ctx.splitLines(secs, w, h)
        ctx.hub(w, h, 34f, if (go && !done) "#2ECC71" else "#3B2A2E") { ctx.text(if (done) "🔄" else if (go) "GO" else "⏳", w / 2, h / 2 + 1, 22f, if (go && !done) "#0B3D22" else "#fff") }
        ctx.againHint(w, h, s, done)
    }
}

@Composable
fun ReactionGameScreen(onBack: () -> Unit) {
    val s = LocalStrings.current
    var n by rememberSaveable { mutableIntStateOf(2) }
    SplitGameScreen(s.reactTitle, onBack, { start -> SetupPanel(s.reactDesc, start) { Stepper(s.players, n, 2..4) { n = it } } }) { ReactionGame(n, it) }
}

// ===================== 🎨 색 반응 대결 =====================
val REACT_COLORS = listOf("#E53935", "#FB8C00", "#FDD835", "#43A047", "#1E88E5", "#8E24AA")
private const val COLOR_WINDOW = 1.5f
private class ColorGame(override val n: Int, val vibe: Vibe) : SplitGame {
    var target = 0; var seq = ArrayList<Pair<Float, Int>>(); var appearAt = 0f
    var tap = arrayOfNulls<Float>(n); var bad = arrayOfNulls<String>(n); var doneAt = 0f; var buzz = false
    init { round(0f) }
    fun round(t0: Float) {
        target = Random.nextInt(REACT_COLORS.size); seq = ArrayList(); var t = t0 + 0.6f; var c = -1
        val appear = t0 + 2.5f + Random.nextFloat() * 4f
        while (t < appear) { var k: Int; do k = Random.nextInt(REACT_COLORS.size) while (k == target || k == c); c = k; seq.add(t to k); t += 0.1f + Random.nextFloat() * 0.4f }
        appearAt = t; tap = arrayOfNulls(n); bad = arrayOfNulls(n); doneAt = 0f; buzz = false
    }
    fun colorAt(t: Float): Int { if (t >= appearAt) return target; var c = -1; for ((at, k) in seq) if (t >= at) c = k; return c }
    private fun has(i: Int) = tap[i] != null || bad[i] != null
    override fun tap(i: Int, t: Float) {
        if (doneAt > 0 || has(i)) return
        val cur = colorAt(t); if (cur < 0) return
        if (cur == target && t <= appearAt + COLOR_WINDOW) { tap[i] = t - appearAt; vibe.tap() } else { bad[i] = "wrong"; vibe.strong() }
        if ((0 until n).all { has(it) }) doneAt = t
    }
    override fun hub(t: Float) { if (doneAt > 0) { round(t); vibe.tap() } }
    override fun draw(ctx: Ctx, w: Float, h: Float, t: Float, secs: List<Sector>, s: Strings) {
        if (doneAt == 0f && t > appearAt + COLOR_WINDOW) { for (i in 0 until n) if (!has(i)) bad[i] = "miss"; doneAt = t; vibe.strong() }
        if (t >= appearAt && !buzz) { buzz = true; vibe.tick() }
        val done = doneAt > 0; val cur = colorAt(t); val names = s.colorNames.split(',')
        val caught = (0 until n).filter { bad[it] != null }
        val order = (0 until n).filter { tap[it] != null }.sortedBy { tap[it] }
        secs.forEachIndexed { i, sec ->
            val st = if (!done) null else if (caught.isNotEmpty()) (if (i in caught) Rank(RED, s.caught, RED, "#fff") else null) else rankStyle(s, order.indexOf(i), n - 1)
            val fill = if (done) (if (i in caught) "#4A1820" else "#1E1E2A") else if (cur < 0) "#1E1E2A" else REACT_COLORS[cur]
            ctx.region(sec, fill, st?.glow) { sc ->
                pname(i, sc)
                when {
                    bad[i] == "wrong" -> text(s.colorWrong, 0f, -6 * sc, 32 * sc, "#FF8A80")
                    bad[i] == "miss" -> text(s.colorMiss, 0f, -6 * sc, 32 * sc, "#FF8A80")
                    tap[i] != null -> text(s.secs(f(tap[i]!!, 3)), 0f, -6 * sc, 42 * sc, "#fff")
                    else -> { text(s.colorWhen(names[target]), 0f, (-22 - du(s)) * sc, 26 * sc, "#fff"); text(s.colorTap, 0f, (16 + du(s)) * sc, 34 * sc, "#fff") }
                }
                if (st != null) badge(st.tag, 0f, 52 * sc, sc, st.bg, st.fg)
            }
        }
        ctx.splitLines(secs, w, h)
        ctx.hub(w, h, 46f, "#1E1E2A") {
            ctx.beginPath(); ctx.arc(w / 2, h / 2, 34f, 0f, TAU); ctx.fillStyle = REACT_COLORS[target]; ctx.fill(); ctx.lineWidth = 4f; ctx.strokeStyle = "#fff"; ctx.stroke()
            ctx.text(if (done) "🔄" else s.colorThis, w / 2, h / 2 + 1, if (done) 22f else 14f, "#fff")
        }
        ctx.againHint(w, h, s, done)
    }
}

@Composable
fun ColorGameScreen(onBack: () -> Unit) {
    val s = LocalStrings.current
    var n by rememberSaveable { mutableIntStateOf(2) }
    SplitGameScreen(s.colorTitle, onBack, { start -> SetupPanel(s.colorDesc, start) { Stepper(s.players, n, 2..4) { n = it } } }) { ColorGame(n, it) }
}

// ===================== ⏱️ 스탑워치 =====================
private class StopGame(override val n: Int, val target: Int, val vibe: Vibe) : SplitGame {
    var startAt = 0f; var tap = arrayOfNulls<Float>(n); var doneAt = 0f; var last = 4
    init { round(0f) }
    fun round(t: Float) { startAt = t + 3f; tap = arrayOfNulls(n); doneAt = 0f; last = 4 }
    override fun tap(i: Int, t: Float) { if (doneAt > 0 || t < startAt || tap[i] != null) return; tap[i] = t - startAt; vibe.tap(); if (tap.all { it != null }) { doneAt = t; vibe.strong() } }
    override fun hub(t: Float) { if (doneAt > 0) { round(t); vibe.tap() } }
    override fun draw(ctx: Ctx, w: Float, h: Float, t: Float, secs: List<Sector>, s: Strings) {
        val started = t >= startAt; val done = doneAt > 0
        val cd = ceil(startAt - t).toInt(); if (!started && cd != last) { last = cd; vibe.tick() }
        if (started && last > 0) { last = 0; vibe.strong() }
        val order = (0 until n).sortedBy { abs((tap[it] ?: 1e9f) - target) }
        secs.forEachIndexed { i, sec ->
            val v = tap[i]; val st = if (done) rankStyle(s, order.indexOf(i), n - 1) else null
            ctx.region(sec, if (done) "#1E1E2A" else if (v != null) "#2A3550" else "#24304A", st?.glow) { sc ->
                pname(i, sc)
                when {
                    done && v != null -> { text(s.secs(f(v, 2)), 0f, -14 * sc, 44 * sc, "#fff"); val d = v - target; text(s.secs((if (d >= 0) "+" else "") + f(d, 2)), 0f, 24 * sc, 20 * sc, if (abs(d) < 0.3f) "#69F0AE" else "#FFAB91") }
                    v != null -> text(s.stopDone, 0f, -6 * sc, 26 * sc, "#BBDEFB")
                    started -> { text(s.stopCount(target), 0f, (-20 - du(s)) * sc, 24 * sc, "#fff"); text(s.stopTapNow, 0f, (14 + du(s)) * sc, 34 * sc, "#FFD740") }
                    else -> text("$cd", 0f, -6 * sc, 56 * sc, "#fff")
                }
                if (st != null) badge(st.tag, 0f, 58 * sc, sc, st.bg, st.fg)
            }
        }
        ctx.splitLines(secs, w, h)
        ctx.hub(w, h, 44f, "#121826") {
            if (done) ctx.text("🔄", w / 2, h / 2 + 1, 24f, "#fff")
            else { ctx.text("$target", w / 2, h / 2 - 5, 34f, "#FFD740"); ctx.text(s.secs(""), w / 2, h / 2 + 22, 12f, "#FFD740") }
        }
        ctx.againHint(w, h, s, done)
    }
}

@Composable
fun StopwatchScreen(onBack: () -> Unit) {
    val s = LocalStrings.current
    var n by rememberSaveable { mutableIntStateOf(2) }
    var target by rememberSaveable { mutableIntStateOf(7) }
    SplitGameScreen(s.stopTitle, onBack, { start ->
        SetupPanel(s.stopDesc, start) { Stepper(s.players, n, 2..4) { n = it }; Stepper(s.stopSeconds, target, 3..30) { target = it } }
    }) { StopGame(n, target, it) }
}

// ===================== 💣 폭탄돌리기 =====================
private class BombGame(override val n: Int, val dir: Int, val vibe: Vibe) : SplitGame {
    var holder = 0; var start = 0f; var explodeAt = 0f; var flyFrom = -1; var flyTo = -1; var flyAt = 0f; var boomed = false
    init { round(0f) }
    fun round(t: Float) { holder = Random.nextInt(n); start = t; explodeAt = t + 10f + Random.nextFloat() * 20f; flyFrom = -1; boomed = false }
    override fun tap(i: Int, t: Float) {
        if (boomed || i != holder || flyFrom >= 0 || t >= explodeAt) return
        val to = when (dir) { 0 -> (i + 1) % n; 1 -> (i - 1 + n) % n; else -> { var k: Int; do k = Random.nextInt(n) while (k == i); k } }
        flyFrom = i; flyTo = to; flyAt = t; holder = to; vibe.tap()
    }
    override fun hub(t: Float) { if (boomed && t - explodeAt > 1.5f) { round(t); vibe.tap() } }
    override fun draw(ctx: Ctx, w: Float, h: Float, t: Float, secs: List<Sector>, s: Strings) {
        val boom = t >= explodeAt; val bt = t - explodeAt
        if (boom && !boomed) { boomed = true; flyFrom = -1; vibe.strong() }
        if (flyFrom >= 0 && t - flyAt > 0.35f) flyFrom = -1
        secs.forEachIndexed { i, sec ->
            val mine = i == holder && flyFrom < 0; val lose = boom && i == holder
            val fill = if (lose) (if (bt < 0.15f) "#FFF3E0" else "#5A1A22") else if (mine) "#4A3A1E" else "#1E1E2A"
            ctx.region(sec, fill, if (lose && bt > 0.6f) RED else null) { sc ->
                pname(i, sc)
                if (lose) { if (bt > 0.6f) badge(s.caught, 0f, 70 * sc, sc, RED, "#fff") }
                else if (mine && !boom) text(s.bombPass, 0f, 62 * sc, 22 * sc, "#FFD740")
            }
        }
        ctx.splitLines(secs, w, h)
        ctx.hub(w, h, 34f, "#121826") { ctx.text(if (boom && bt > 1.5f) "🔄" else when (dir) { 0 -> "↻"; 1 -> "↺"; else -> "🔀" }, w / 2, h / 2 + 2, if (boom) 24f else 30f, "#FFD740") }
        val fuse = max(0f, 1f - (t - start) / (explodeAt - start))
        if (!boom) {
            var x: Float; var y: Float; var sc = 1f
            if (flyFrom >= 0) {
                val u = min(1f, (t - flyAt) / 0.35f); val a = secs[flyFrom]; val b = secs[flyTo]
                x = a.ax + (b.ax - a.ax) * u; y = a.ay + (b.ay - a.ay) * u - sin(u * PI.toFloat()) * 60; sc = 1 + sin(u * PI.toFloat()) * 0.3f
            } else { x = secs[holder].ax + sin(t * 30) * 2 * (1.3f - fuse); y = secs[holder].ay }
            ctx.bombArt(x, y, 30 * sc, fuse, t)
        } else {
            val sec = secs[holder]; val x = sec.ax; val y = sec.ay
            if (bt < 0.6f) {
                val u = bt / 0.6f; ctx.save(); ctx.globalAlpha = 1 - u
                for (k in 0 until 14) { val a = k * PI.toFloat() * 2 / 14; ctx.beginPath(); ctx.moveTo(x, y); ctx.lineTo(x + cos(a) * 150 * u + 20, y + sin(a) * 150 * u); ctx.strokeStyle = if (k % 2 == 1) "#FFB300" else "#FF5722"; ctx.lineWidth = 10f; ctx.stroke() }
                ctx.beginPath(); ctx.arc(x, y, 30 + 110 * u, 0f, TAU); ctx.fillStyle = "rgba(255,200,80,.6)"; ctx.fill(); ctx.restore()
                ctx.save(); ctx.translate(x, y); ctx.rotate(sec.rot); ctx.text(s.bombBoom, 0f, -60f, 46 * (0.6f + u), "#FF5722"); ctx.restore()
            }
            if (bt > 0.35f) {
                val u = min(1f, (bt - 0.35f) / 0.25f); val pop = if (u < 1) 1 - (1 - u).pow(3) * cos(u * 8) else 1f
                val ch = bt - 0.6f; val bite = if (ch < 0) 1f else if (ch > 1.4f) 0f else abs(cos(ch * PI.toFloat() * 1.4f))
                ctx.save(); ctx.translate(x, y); ctx.rotate(sec.rot); ctx.chompWolf(0f, -8f, 46f, bite, pop)
                if (ch > 0.1f) ctx.text(s.biteWord, 60f, -58f, 26f, RED); ctx.restore()
            }
            ctx.againHint(w, h, s, bt > 1.5f)
        }
    }
}

@Composable
fun BombGameScreen(onBack: () -> Unit) {
    val s = LocalStrings.current
    var n by rememberSaveable { mutableIntStateOf(4) }
    var dir by rememberSaveable { mutableIntStateOf(0) }
    SplitGameScreen(s.bombTitle, onBack, { start ->
        SetupPanel(s.bombDesc, start) {
            Stepper(s.players, n, 2..8) { n = it }
            ChoiceRow(s.bombDir, listOf("↻ ${s.dirCw}", "↺ ${s.dirCcw}", "🔀 ${s.dirRandom}"), dir) { dir = it }
        }
    }) { BombGame(n, dir, it) }
}

// ===================== ⚔️ 주사위 대결 =====================
fun duelValue(faces: IntArray, mul: Boolean) = if (mul) faces.fold(1) { a, b -> a * b } else faces.sum()

private class DuelGame(override val n: Int, val dice: Int, val mul: Boolean, val vibe: Vibe) : SplitGame {
    var faces = Array(n) { IntArray(dice) }; var rollAt = arrayOfNulls<Float>(n); var done = false
    fun round() { faces = Array(n) { IntArray(dice) }; rollAt = arrayOfNulls(n); done = false }
    override fun tap(i: Int, t: Float) { if (done || rollAt[i] != null) return; rollAt[i] = t; faces[i] = IntArray(dice) { Random.nextInt(1, 7) }; vibe.tap() }
    override fun hub(t: Float) { if (done) { round(); vibe.tap() } }
    override fun draw(ctx: Ctx, w: Float, h: Float, t: Float, secs: List<Sector>, s: Strings) {
        val roll = 0.7f
        val settled = BooleanArray(n) { rollAt[it] != null && t - rollAt[it]!! >= roll }
        if (!done && settled.all { it }) { done = true; vibe.strong() }
        val vals = IntArray(n) { duelValue(faces[it], mul) }; val hi = vals.max(); val lo = vals.min(); val mark = done && hi != lo
        secs.forEachIndexed { i, sec ->
            val isHi = mark && vals[i] == hi; val isLo = mark && vals[i] == lo; val rolling = rollAt[i] != null && !settled[i]
            val fill = if (isHi) "#3D3416" else if (isLo) "#4A1820" else if (rollAt[i] == null) "#24304A" else "#1E1E2A"
            ctx.region(sec, fill, if (isHi) GOLD else if (isLo) RED else null) { sc ->
                text("P${i + 1}", 0f, -82 * sc, 20 * sc, PLAYER_HEX[i])
                val ds = 46 * sc; val gap = 8 * sc; val total = dice * ds + (dice - 1) * gap
                for (k in 0 until dice) {
                    val v = if (rolling) Random.nextInt(1, 7) else faces[i][k]
                    val jx = if (rolling) sin(t * 50 + k) * 3 else 0f; val jy = if (rolling) cos(t * 43 + k * 2) * 3 else 0f
                    save(); translate(-total / 2 + ds / 2 + k * (ds + gap) + jx, -22 * sc + jy); if (rolling) rotate(sin(t * 30 + k) * 0.4f)
                    die(0f, 0f, ds, if (rollAt[i] == null) 0 else v, if (isHi) GOLD else if (isLo) RED else null); restore()
                }
                if (rollAt[i] == null) text(s.tapToRoll, 0f, 30 * sc, 20 * sc, PLAYER_HEX[i])
                else if (!rolling) text(if (mul) s.duelMulN(vals[i]) else s.duelSumN(vals[i]), 0f, 34 * sc, 30 * sc, "#fff")
                if (isHi) badge(s.hiTag, 0f, 72 * sc, sc, GOLD, "#000")
                if (isLo) badge(s.loTag, 0f, 72 * sc, sc, RED, "#fff")
            }
        }
        ctx.splitLines(secs, w, h)
        ctx.hub(w, h, 34f, "#121826") { ctx.text(if (done) "🔄" else if (mul) "✖️" else "➕", w / 2, h / 2 + 2, 24f, "#FFD740") }
        ctx.againHint(w, h, s, done)
    }
}

@Composable
fun DiceDuelScreen(onBack: () -> Unit) {
    val s = LocalStrings.current
    var n by rememberSaveable { mutableIntStateOf(2) }
    var dice by rememberSaveable { mutableIntStateOf(2) }
    var mul by rememberSaveable { mutableIntStateOf(0) }
    SplitGameScreen(s.duelTitle, onBack, { start ->
        SetupPanel(s.duelDesc, start) {
            Stepper(s.players, n, 2..4) { n = it }
            Stepper(s.duelDice, dice, 2..4) { dice = it }
            ChoiceRow(s.duelMode, listOf("➕ ${s.duelSum}", "✖️ ${s.duelMul}"), mul) { mul = it }
        }
    }) { DuelGame(n, dice, mul == 1, it) }
}
