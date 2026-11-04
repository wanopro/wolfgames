package com.wolfgames.app.games

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.i18n.Strings
import com.wolfgames.app.ui.ChoiceRow
import com.wolfgames.app.ui.Ctx
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.TAU
import com.wolfgames.app.ui.Vibe
import com.wolfgames.app.ui.coinWolfFace
import com.wolfgames.app.ui.dazedWolf
import com.wolfgames.app.ui.text
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

// v1.8 새 게임 (글 없이 하는 게임): 🏗️ 탑 쌓기 · 🔴 따라 하기 · 🍺 늑대컬링 (⚡ 순발력) · ✂️ 폭탄 선 자르기 (🍀 운빨) — 웹 games-v18.js 와 같음

private fun ease18(u: Float) = 1f - (1f - u.coerceIn(0f, 1f)).pow(3)

/** 🔊 짧은 소리 (기기에서 바로 만든 소리, 실패하면 조용히). kind: 0 사인 · 1 네모 · 2 톱니 · 3 세모 */
object Beep {
    private val pool = Executors.newCachedThreadPool()
    fun tone(hz: Float, dur: Float, kind: Int = 0, vol: Float = 0.22f) {
        pool.execute {
            try {
                val sr = 22050; val n = (sr * dur).toInt().coerceAtLeast(64); val buf = ShortArray(n); val att = sr * 0.015f
                for (i in 0 until n) {
                    val ph = (i.toFloat() * hz / sr) % 1f
                    val v = when (kind) { 1 -> if (ph < 0.5f) 1f else -1f; 2 -> 2f * ph - 1f; 3 -> 4f * abs(ph - 0.5f) - 1f; else -> sin(2 * PI.toFloat() * ph) }
                    val env = min(1f, i / att) * (1f - i.toFloat() / n).pow(2)
                    buf[i] = (v * env * vol * 32767).toInt().coerceIn(-32767, 32767).toShort()
                }
                val at = AudioTrack.Builder()
                    .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
                    .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sr).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                    .setBufferSizeInBytes(n * 2).setTransferMode(AudioTrack.MODE_STATIC).build()
                at.write(buf, 0, n); at.play(); Thread.sleep((dur * 1000).toLong() + 80); at.release()
            } catch (_: Throwable) { }
        }
    }
}

@Composable
private fun SetupThen18(title: String, onBack: () -> Unit, setup: @Composable (start: () -> Unit) -> Unit, game: @Composable () -> Unit) {
    var started by rememberSaveable { mutableStateOf(com.wolfgames.app.DebugOpen.autostart) }
    var round by remember { mutableIntStateOf(0) }
    if (!started) GameScaffold(title, onBack) { setup { started = true; round++ } }
    else { BackHandler { started = false }; GameScaffold(title, onBack = { started = false }) { androidx.compose.runtime.key(round) { game() } } }
}

/** 매 프레임 다시 그리는 캔버스 + 누른 곳 (x, y, t, w, h) */
@Composable
private fun Anim18(key: Any, onDown: (Float, Float, Float, Float, Float) -> Unit, draw: (Ctx, Float, Float, Float) -> Unit) {
    val density = LocalDensity.current.density
    val t0 = remember(key) { System.nanoTime() }
    val now = { (System.nanoTime() - t0) / 1e9f }
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(key) { while (true) withFrameNanos { tick = it } }
    Canvas(Modifier.fillMaxSize().pointerInput(key) {
        awaitPointerEventScope {
            while (true) {
                val ev = awaitPointerEvent()
                for (ch in ev.changes) if (ch.changedToDown()) { onDown(ch.position.x / density, ch.position.y / density, now(), size.width / density, size.height / density); ch.consume() }
            }
        }
    }) {
        tick.let { }
        val w = size.width / density; val h = size.height / density
        drawIntoCanvas { cc -> val c = Ctx(cc.nativeCanvas, density); draw(c, w, h, now()); c.finish() }
    }
}

private fun Ctx.pill18(w: Float, h: Float, label: String) { fillStyle = "rgba(0,0,0,.55)"; beginPath(); roundRect(w / 2 - 110, h - 86, 220f, 44f, 22f); fill(); text(label, w / 2, h - 64, 17f, "#fff") }

// ===================== 🏗️ 탑 쌓기 =====================
val TS_COLS = listOf("#FF6B81", "#FFB74D", "#FFD54F", "#81C784", "#4DD0E1", "#64B5F6", "#9575CD", "#F06292")

@Composable
fun TowerScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current
    SetupThen18(s.tsTitle, onBack, { start -> SetupPanel(s.tsDesc, start) {} }) {
        val g = remember { TowerGame(vibe) }
        Anim18(g, onDown = { _, _, t, _, _ -> g.down(t) }) { c, w, h, t -> g.draw(c, w, h, t, s) }
    }
}

private class TowerGame(val vibe: Vibe) {
    class B(var x: Float, var w: Float)
    class Chip(val x: Float, val w: Float, val row: Int, val t: Float, val col: String, val dir: Int)
    var W = 0f; var H = 0f; var bh = 30f
    val blocks = ArrayList<B>(); var cur = B(0f, 0f); var dir = 1; var speed = 0f
    val chips = ArrayList<Chip>(); var fallT = 0f; var perfT = -9f; var passT = -9f; var landT = -9f; var cam = 0f; var last = -1f; var tip = 1
    fun reset(w: Float, h: Float) {
        W = w; H = h; val bw = w * 0.62f; bh = (h * 0.055f).coerceIn(22f, 36f)
        blocks.clear(); blocks.add(B((w - bw) / 2, bw)); cur = B(-bw * 0.5f, bw); dir = 1; speed = w * 0.5f
        chips.clear(); fallT = 0f; perfT = -9f; passT = -9f; landT = -9f; cam = 0f; last = -1f; tip = 1
    }
    fun rowY(i: Int) = H - 70 - (i + 1) * bh + cam
    fun colOf(i: Int) = if (i == 0) "#546E7A" else TS_COLS[(i - 1) % TS_COLS.size]
    fun Ctx.block(x: Float, y: Float, w: Float, col: String) {
        beginPath(); roundRect(x, y, w, bh - 2, 4f); fillStyle = col; fill()
        fillStyle = "rgba(255,255,255,.28)"; fillRect(x + 3, y + 2, max(0f, w - 6), bh * 0.2f)
    }
    fun down(t: Float) {
        if (blocks.isEmpty()) return
        if (fallT > 0) { if (t - fallT > 1.4f) { reset(W, H); vibe.tap() }; return }
        val n = blocks.size; val tb = blocks[n - 1]; val cu = cur
        val l = max(cu.x, tb.x); val r = min(cu.x + cu.w, tb.x + tb.w)
        if (r - l <= 0) { fallT = t; tip = if (cu.x < tb.x) -1 else 1; vibe.strong(); Beep.tone(110f, 0.6f, 2, 0.18f); return }
        val nb: B
        if (abs(cu.x - tb.x) < max(4f, W * 0.012f)) { nb = B(tb.x, cu.w); perfT = t; vibe.tick(); Beep.tone(880f, 0.18f) }
        else {
            val left = cu.x < tb.x
            chips.add(Chip(if (left) cu.x else r, if (left) tb.x - cu.x else cu.x + cu.w - r, n, t, colOf(n), if (left) -1 else 1))
            nb = B(l, r - l); vibe.tap(); Beep.tone(440f, 0.1f, 3)
        }
        blocks.add(nb); landT = t; passT = t
        val m = blocks.size
        cur = B(if (m % 2 == 1) -nb.w * 0.5f else W - nb.w * 0.5f, nb.w); dir = if (m % 2 == 1) 1 else -1
        speed = min(W * 1.7f, speed * 1.06f)
    }
    fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings) = with(c) {
        fillStyle = "#12121A"; fillRect(0f, 0f, w, h)
        if (blocks.isEmpty() || W != w || H != h) reset(w, h)
        val dt = if (last < 0) 0f else min(0.05f, t - last); last = t
        val n = blocks.size
        cam += (max(0f, (n + 1) * bh - h * 0.5f) - cam) * min(1f, dt * 6)
        fillStyle = "#263238"; fillRect(0f, h - 70 + cam, w, 400f)
        val r = bh * 0.8f
        if (fallT == 0f) {
            cur.x += dir * speed * dt
            if (cur.x < -cur.w * 0.5f) { cur.x = -cur.w * 0.5f; dir = 1 }
            if (cur.x > w - cur.w * 0.5f) { cur.x = w - cur.w * 0.5f; dir = -1 }
            blocks.forEachIndexed { i, b -> block(b.x, rowY(i), b.w, colOf(i)) }
            val tb = blocks[n - 1]; val hop = if (t - landT < 0.3f) sin((t - landT) / 0.3f * PI.toFloat()) * bh * 0.5f else 0f
            save(); translate(tb.x + tb.w / 2, rowY(n - 1) - r * 0.62f - hop); coinWolfFace(r); restore()
            block(cur.x, rowY(n) - bh * 1.6f, cur.w, colOf(n))
        } else {
            val u = t - fallT
            blocks.forEachIndexed { i, b ->
                val k = i.toFloat() / max(1, n - 1); val d = max(0f, u - (n - 1 - i) * 0.04f)
                save(); translate(b.x + b.w / 2 + tip * d * d * w * 0.7f * k, rowY(i) + bh / 2 + d * d * 900 * k * k); rotate(tip * d * 2.2f * k)
                block(-b.w / 2, -bh / 2, b.w, colOf(i)); restore()
            }
            val tb = blocks[n - 1]
            save(); translate(tb.x + tb.w / 2 + tip * u * u * w * 0.8f, rowY(n - 1) - r * 0.62f + u * u * 1100); rotate(tip * u * 5); coinWolfFace(r); restore()
            block(cur.x, rowY(n) - bh * 1.6f + u * u * 1400, cur.w, colOf(n))
        }
        chips.removeAll { t - it.t >= 1.5f }
        for (p in chips) { val u = t - p.t; save(); globalAlpha = max(0f, 1 - u / 1.5f); translate(p.x + p.w / 2, rowY(p.row) + bh / 2 + u * u * 900); rotate(p.dir * u * 3); block(-p.w / 2, -bh / 2, p.w, p.col); restore() }
        text(s.tsFloor(n - 1), w / 2, 38f, 30f, "#fff")
        if (t - perfT < 0.8f) { globalAlpha = 1 - (t - perfT) / 0.8f; text("✨ PERFECT!", w / 2, 76 - (t - perfT) * 30, 22f, "#FFD740"); globalAlpha = 1f }
        if (fallT == 0f) {
            if (t - passT < 0.9f) { val u = (t - passT) / 0.9f; globalAlpha = 1 - u; text("👉", w / 2 + u * 60, h * 0.62f, 54f, "#fff"); globalAlpha = 1f }
            text(s.tsTap, w / 2, h - 22, 14f, "#9A9AB0", false)
        } else {
            val u = t - fallT
            if (u > 0.6f) { fillStyle = "rgba(0,0,0,.45)"; fillRect(0f, h * 0.3f, w, 130f); text(s.tsFall, w / 2, h * 0.3f + 42, 30f, "#FFD740"); text(s.shotWord, w / 2, h * 0.3f + 92, 26f, "#FF5A6E") }
            if (u > 1.4f) pill18(w, h, s.playAgain)
        }
    }
}

// ===================== 🔴 따라 하기 (사이먼) =====================
val SM_COL = listOf("#2ECC71" to "#145A32", "#E74C3C" to "#641E16", "#3498DB" to "#154360", "#F1C40F" to "#7D6608")
val SM_HZ = listOf(330f, 392f, 494f, 659f)

/** 색약도 알아보게 모양: ● ▲ ■ ★ */
fun Ctx.smShape(k: Int, x: Float, y: Float, r: Float) {
    beginPath()
    when (k) {
        0 -> arc(x, y, r, 0f, TAU)
        1 -> { moveTo(x, y - r); lineTo(x + r * 0.95f, y + r * 0.75f); lineTo(x - r * 0.95f, y + r * 0.75f); closePath() }
        2 -> rect(x - r * 0.82f, y - r * 0.82f, r * 1.64f, r * 1.64f)
        else -> { for (i in 0 until 10) { val a = -PI.toFloat() / 2 + i * PI.toFloat() / 5; val rr = if (i % 2 == 1) r * 0.45f else r * 1.05f; if (i == 0) moveTo(x + cos(a) * rr, y + sin(a) * rr) else lineTo(x + cos(a) * rr, y + sin(a) * rr) }; closePath() }
    }
    fill()
}

// 🔴 따라 하기: 액세서리 늑대 — 0 😎 선글라스 · 1 🎀 리본 · 2 🧢 모자 · 3 👑 왕관 (웹 smWolfHead · smAccessory 와 같음)
fun Ctx.smWolfHead(r: Float, open: Float = 0f) {
    for (s in listOf(-1f, 1f)) {
        beginPath(); moveTo(s * r * 0.2f, -r * 0.42f); lineTo(s * r * 0.62f, -r * 0.92f); lineTo(s * r * 0.72f, -r * 0.22f); closePath(); fillStyle = "#7C8593"; fill()
        beginPath(); moveTo(s * r * 0.34f, -r * 0.42f); lineTo(s * r * 0.58f, -r * 0.74f); lineTo(s * r * 0.62f, -r * 0.32f); closePath(); fillStyle = "#F6AFC0"; fill()
    }
    beginPath(); ellipse(0f, r * 0.02f, r * 0.66f, r * 0.58f, 0f, 0f, TAU); fillStyle = "#9AA3AF"; fill()
    for (s in listOf(-1f, 1f)) { beginPath(); moveTo(s * r * 0.62f, r * 0.05f); lineTo(s * r * 0.8f, r * 0.18f); lineTo(s * r * 0.6f, r * 0.28f); closePath(); fillStyle = "#9AA3AF"; fill() }
    beginPath(); ellipse(0f, r * 0.24f, r * 0.34f, r * 0.26f, 0f, 0f, TAU); fillStyle = "#F1EEE8"; fill()
    for (s in listOf(-1f, 1f)) {
        beginPath(); ellipse(s * r * 0.26f, -r * 0.08f, r * 0.11f, r * 0.13f * (1 - open * 0.5f), 0f, 0f, TAU); fillStyle = "#2B2B33"; fill()
        beginPath(); arc(s * r * 0.23f, -r * 0.13f, r * 0.045f, 0f, TAU); fillStyle = "#fff"; fill()
        beginPath(); ellipse(s * r * 0.44f, r * 0.16f, r * 0.1f, r * 0.06f, 0f, 0f, TAU); fillStyle = "rgba(255,130,150,.55)"; fill()
    }
    beginPath(); ellipse(0f, r * 0.13f, r * 0.09f, r * 0.065f, 0f, 0f, TAU); fillStyle = "#2B2B33"; fill()
    if (open > 0.05f) {
        beginPath(); ellipse(0f, r * 0.33f, r * 0.11f, r * 0.13f * open + r * 0.03f, 0f, 0f, TAU); fillStyle = "#5A1E2B"; fill()
        beginPath(); ellipse(0f, r * 0.38f + r * 0.06f * open, r * 0.06f, r * 0.04f, 0f, 0f, TAU); fillStyle = "#F48FB1"; fill()
    } else {
        beginPath(); moveTo(-r * 0.12f, r * 0.28f); quadraticCurveTo(-r * 0.06f, r * 0.36f, 0f, r * 0.28f); quadraticCurveTo(r * 0.06f, r * 0.36f, r * 0.12f, r * 0.28f); strokeStyle = "#2B2B33"; lineWidth = r * 0.04f; lineCap = "round"; stroke()
        beginPath(); ellipse(0f, r * 0.36f, r * 0.05f, r * 0.06f, 0f, 0f, PI.toFloat()); fillStyle = "#F48FB1"; fill()
    }
}
fun Ctx.smAccessory(k: Int, r: Float, t: Float) {
    when (k) {
        0 -> {
            fillStyle = "#111"
            for (s in listOf(-1f, 1f)) { beginPath(); roundRect4(s * r * 0.26f - r * 0.2f, -r * 0.2f, r * 0.4f, r * 0.26f, r * 0.06f, r * 0.06f, r * 0.14f, r * 0.14f); fill() }
            fillRect(-r * 0.08f, -r * 0.16f, r * 0.16f, r * 0.05f)
            beginPath(); moveTo(-r * 0.46f, -r * 0.14f); lineTo(-r * 0.64f, -r * 0.2f); moveTo(r * 0.46f, -r * 0.14f); lineTo(r * 0.64f, -r * 0.2f); strokeStyle = "#111"; lineWidth = r * 0.05f; stroke()
            for (s in listOf(-1f, 1f)) { beginPath(); moveTo(s * r * 0.26f - r * 0.12f, -r * 0.14f); lineTo(s * r * 0.26f - r * 0.02f, -r * 0.14f); strokeStyle = "rgba(255,255,255,.7)"; lineWidth = r * 0.04f; stroke() }
        }
        1 -> {
            save(); translate(-r * 0.38f, -r * 0.62f); rotate(-0.35f)
            fillStyle = "#FF80AB"
            for (s in listOf(-1f, 1f)) { beginPath(); moveTo(0f, 0f); quadraticCurveTo(s * r * 0.32f, -r * 0.28f, s * r * 0.36f, 0f); quadraticCurveTo(s * r * 0.32f, r * 0.26f, 0f, 0f); fill() }
            for (s in listOf(-1f, 1f)) { beginPath(); moveTo(0f, 0f); lineTo(s * r * 0.14f, r * 0.3f); lineTo(s * r * 0.04f, r * 0.26f); closePath(); fill() }
            beginPath(); arc(0f, 0f, r * 0.08f, 0f, TAU); fillStyle = "#D81B60"; fill()
            beginPath(); ellipse(-r * 0.18f, -r * 0.08f, r * 0.06f, r * 0.03f, -0.5f, 0f, TAU); fillStyle = "rgba(255,255,255,.45)"; fill()
            restore()
        }
        2 -> {
            beginPath(); ellipse(0f, -r * 0.36f, r * 0.6f, r * 0.42f, 0f, PI.toFloat(), 2 * PI.toFloat()); closePath(); fillStyle = "#0D3B8C"; fill()
            beginPath(); ellipse(r * 0.42f, -r * 0.36f, r * 0.42f, r * 0.1f, -0.05f, 0f, TAU); fillStyle = "#082A66"; fill()
            beginPath(); arc(0f, -r * 0.78f, r * 0.06f, 0f, TAU); fillStyle = "#fff"; fill()
            beginPath(); moveTo(0f, -r * 0.76f); lineTo(0f, -r * 0.38f); strokeStyle = "rgba(255,255,255,.25)"; lineWidth = r * 0.03f; stroke()
            beginPath(); ellipse(-r * 0.26f, -r * 0.58f, r * 0.12f, r * 0.06f, -0.6f, 0f, TAU); fillStyle = "rgba(255,255,255,.3)"; fill()
        }
        else -> {
            beginPath(); moveTo(-r * 0.4f, -r * 0.48f); lineTo(-r * 0.46f, -r * 0.92f); lineTo(-r * 0.22f, -r * 0.7f); lineTo(0f, -r * 1.02f); lineTo(r * 0.22f, -r * 0.7f); lineTo(r * 0.46f, -r * 0.92f); lineTo(r * 0.4f, -r * 0.48f); closePath()
            fillStyle = "#FFE082"; fill(); strokeStyle = "#A86E0C"; lineWidth = r * 0.03f; stroke()
            fillStyle = "#C8901A"; fillRect(-r * 0.4f, -r * 0.56f, r * 0.8f, r * 0.08f)
            for ((pos, col) in listOf((-0.46f to -0.92f) to "#E53950", (0f to -1.02f) to "#4FC3F7", (0.46f to -0.92f) to "#69F0AE")) { beginPath(); arc(pos.first * r, pos.second * r, r * 0.06f, 0f, TAU); fillStyle = col; fill() }
            val a0 = globalAlpha; globalAlpha = a0 * (0.5f + 0.5f * sin(t * 6)); fillStyle = "#fff"; beginPath(); arc(r * 0.18f, -r * 0.84f, r * 0.035f, 0f, TAU); fill(); globalAlpha = a0
        }
    }
}

@Composable
fun SimonScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current
    SetupThen18(s.smTitle, onBack, { start -> SetupPanel(s.smDesc, start) {} }) {
        val g = remember { SimonGame(vibe) }
        Anim18(g, onDown = { x, y, t, w, h -> g.down(x, y, t, w, h) }) { c, w, h, t -> g.draw(c, w, h, t, s) }
    }
}

private class SimonGame(val vibe: Vibe) {
    val seq = ArrayList<Int>(); var phase = 0 // 0 보기 · 1 누르기 · 2 성공 · 3 틀림
    var ps = 0f; var inp = 0; var tap = -1; var tapT = -9f; var shown = -1; var pt = 0f
    init { fresh(0f) }
    fun fresh(t: Float) { seq.clear(); seq.add(Random.nextInt(4)); phase = 0; ps = t + 0.8f; inp = 0; tap = -1; tapT = -9f; shown = -1; pt = t }
    fun step() = max(0.3f, 0.62f - seq.size * 0.025f)
    class L(val x0: Float, val y0: Float, val p: Float, val gap: Float)
    fun layout(w: Float, h: Float): L { val sz = min(w - 32, h - 210); val gap = 12f; return L((w - sz) / 2, max(100f, (h - sz) / 2 + 10), (sz - gap) / 2, gap) }
    fun down(x: Float, y: Float, t: Float, w: Float, h: Float) {
        if (phase == 3) { if (t - pt > 1.2f) { fresh(t); vibe.tap() }; return }
        if (phase != 1) return
        val l = layout(w, h)
        val cx = floor((x - l.x0) / (l.p + l.gap)).toInt(); val cy = floor((y - l.y0) / (l.p + l.gap)).toInt()
        if (cx !in 0..1 || cy !in 0..1) return
        val k = cy * 2 + cx
        tap = k; tapT = t
        if (k != seq[inp]) { phase = 3; pt = t; vibe.strong(); Beep.tone(140f, 0.7f, 1, 0.15f); return }
        Beep.tone(SM_HZ[k], 0.22f); vibe.tap(); inp++
        if (inp >= seq.size) { phase = 2; pt = t }
    }
    fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings) = with(c) {
        fillStyle = "#12121A"; fillRect(0f, 0f, w, h)
        val l = layout(w, h)
        var lit = -1; var lu = 0f
        if (phase == 0) {
            val st = step(); val k = floor((t - ps) / st).toInt()
            if (t >= ps) {
                if (k >= seq.size) { phase = 1; inp = 0; pt = t }
                else if (t - ps - k * st < st * 0.68f) { lit = seq[k]; lu = (t - ps - k * st) / (st * 0.68f); if (shown != k) { shown = k; Beep.tone(SM_HZ[lit], st * 0.68f) } }
            }
        } else if (phase == 2 && t - pt > 1.1f) { seq.add(Random.nextInt(4)); phase = 0; ps = t + 0.3f; shown = -1 }
        if (phase != 0 && t - tapT < 0.3f) { lit = tap; lu = (t - tapT) / 0.3f }
        val wrongK = if (phase == 3) seq[inp] else -1
        for (k in 0 until 4) {
            val x = l.x0 + (k % 2) * (l.p + l.gap); val y = l.y0 + (k / 2) * (l.p + l.gap)
            val on = k == lit || (k == wrongK && sin(t * 14) > 0)
            save(); translate(x + l.p / 2, y + l.p / 2); val sc = if (on) 1.04f else 1f; scale(sc, sc)
            beginPath(); roundRect(-l.p / 2, -l.p / 2, l.p, l.p, l.p * 0.16f); fillStyle = if (on) SM_COL[k].first else SM_COL[k].second; fill()
            if (on) { beginPath(); roundRect(-l.p / 2, -l.p / 2, l.p, l.p, l.p * 0.16f); fillStyle = "rgba(255,255,255,.22)"; fill() }
            fillStyle = if (on) "rgba(255,255,255,.95)" else "rgba(255,255,255,.35)"; smShape(k, -l.p / 2 + l.p * 0.11f, -l.p / 2 + l.p * 0.11f, l.p * 0.055f)
            val bump = if (on && k == lit) sin(min(1f, lu) * PI.toFloat()) else if (on) 0.6f else 0f
            save(); globalAlpha = if (on) 1f else 0.55f; translate(0f, l.p * 0.06f - bump * l.p * 0.08f)
            smWolfHead(l.p * 0.36f, bump); smAccessory(k, l.p * 0.36f, t); restore()
            if (on) { text("♪", l.p * 0.32f, -l.p * 0.3f - lu * 14, 22f, "rgba(255,255,255,.9)"); for (q in 0 until 5) { val a = q * 1.26f + t * 2; beginPath(); arc(cos(a) * l.p * 0.42f, sin(a) * l.p * 0.42f, 3f, 0f, TAU); fillStyle = "rgba(255,255,255,.7)"; fill() } }
            restore()
        }
        text(s.smLevel(seq.size), w / 2, 38f, 30f, "#fff")
        val msg = when (phase) { 0 -> s.smWatch; 1 -> s.smGo; 2 -> s.smOk; else -> s.smWrong }
        val my = min(h - 110, l.y0 + l.p * 2 + l.gap + 34)
        if (phase != 3) text(msg, w / 2, my, 22f, if (phase == 2) "#FFD740" else "#fff")
        if (phase == 1) {
            val n = seq.size; val dw = min(14f, (w - 40) / n)
            for (i in 0 until n) { beginPath(); arc(w / 2 + (i - (n - 1) / 2f) * dw, 74f, min(4f, dw * 0.35f), 0f, TAU); fillStyle = if (i < inp) "#FFD740" else "rgba(255,255,255,.25)"; fill() }
        }
        if (phase == 3) { // ❌ 틀렸다! 가운데 크게
            val u = t - pt; val k = 0.6f + 0.4f * ease18(u / 0.25f); val sh = if (u < 0.4f) sin(u * 60) * 8 * (1 - u / 0.4f) else 0f
            val cy = l.y0 + l.p + l.gap / 2; val bw = min(w - 40, 300f); val bh = 210f
            save(); translate(w / 2 + sh, cy); scale(k, k)
            beginPath(); roundRect(-bw / 2, -bh / 2, bw, bh, 26f); fillStyle = "rgba(12,12,18,.9)"; fill(); strokeStyle = "#FF5A6E"; lineWidth = 4f; stroke()
            text("❌", 0f, -bh / 2 + 52, 60f, "#fff"); text(s.smWrong.replace("❌ ", ""), 0f, 18f, 34f, "#FF5A6E"); text(s.shotWord, 0f, 66f, 30f, "#FFD740")
            restore()
            if (u > 1.2f) pill18(w, h, s.playAgain)
        }
    }
}

// ===================== ✂️ 폭탄 선 자르기 =====================
val WC_N = listOf(4, 5, 6, 8)
val WC_COLS = listOf("#E53935", "#1E88E5", "#FDD835", "#43A047", "#8E24AA", "#FB8C00", "#ECEFF1", "#8D6E63")

@Composable
fun WireCutScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var ni by remember { mutableIntStateOf(WC_N.indexOf(prefs.getInt("wc_n", 6)).coerceAtLeast(0)) }
    SetupThen18(s.wcTitle, onBack, { start ->
        SetupPanel(s.wcDesc, start) { ChoiceRow(s.wcWires, WC_N.map { "$it" }, ni) { ni = it; prefs.edit().putInt("wc_n", WC_N[it]).apply() } }
    }) {
        val g = remember { WireGame(WC_N[ni], vibe) }
        Anim18(g, onDown = { x, y, t, w, h -> g.down(x, y, t, w, h) }) { c, w, h, t -> g.draw(c, w, h, t, s) }
    }
}

private class WireGame(val n: Int, val vibe: Vibe) {
    var cols = IntArray(n); val cut = FloatArray(n); var bomb = 0; var boomT = 0f; var safeT = -9f; var cuts = 0; var clock = 0f; var last = -1f
    var snipX = 0f; var snipY = 0f; var snipT = -9f
    init { round() }
    fun round() {
        cols = (0 until WC_COLS.size).shuffled().take(n).toIntArray(); cut.fill(0f); bomb = Random.nextInt(n)
        boomT = 0f; safeT = -9f; cuts = 0; clock = 0f; last = -1f; snipT = -9f
    }
    class G(val bx: Float, val bw: Float, val by: Float, val bhh: Float, val y1: Float, val y2: Float, val left: Float, val gapX: Float)
    fun geo(w: Float, h: Float): G { val bx = w * 0.1f; val bw = w * 0.8f; val by = max(24f, h * 0.07f); val bhh = min(140f, h * 0.2f); return G(bx, bw, by, bhh, by + bhh, h * 0.8f, bx + bw * 0.04f, bw * 0.92f / n) }
    fun wireX(g: G, i: Int) = g.left + (i + 0.5f) * g.gapX
    fun Ctx.wirePath(g: G, i: Int) { val x = wireX(g, i); val amp = g.gapX * 0.32f * (if (i % 2 == 1) 1 else -1); val d = g.y2 - g.y1; beginPath(); moveTo(x, g.y1); bezierCurveTo(x + amp, g.y1 + d * 0.33f, x - amp, g.y1 + d * 0.66f, x, g.y2) }
    fun down(x: Float, y: Float, t: Float, w: Float, h: Float) {
        if (boomT > 0) { if (t - boomT > 1.3f) { round(); vibe.tap() }; return }
        val g = geo(w, h)
        if (y < g.y1 - 10 || y > g.y2 + 30) return
        var best = -1; var bd = g.gapX * 0.5f
        for (i in 0 until n) { if (cut[i] > 0) continue; val d = abs(x - wireX(g, i)); if (d < bd) { bd = d; best = i } }
        if (best < 0) return
        cut[best] = t; snipX = wireX(g, best); snipY = (g.y1 + g.y2) / 2; snipT = t
        if (best == bomb) { boomT = t; vibe.strong(); Beep.tone(70f, 0.9f, 2, 0.25f) }
        else { cuts++; safeT = t; vibe.tap(); Beep.tone(1200f, 0.06f, 1, 0.12f) }
    }
    fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings) = with(c) {
        fillStyle = "#12121A"; fillRect(0f, 0f, w, h)
        val g = geo(w, h); val dt = if (last < 0) 0f else min(0.05f, t - last); last = t
        if (boomT == 0f) clock += dt * (1 + cuts * 0.8f)
        val shake = if (boomT > 0 && t - boomT < 0.5f) (1 - (t - boomT) / 0.5f) * 10 else 0f
        save(); if (shake > 0) translate((Random.nextFloat() - 0.5f) * shake, (Random.nextFloat() - 0.5f) * shake)
        beginPath(); roundRect(g.bx, g.y2 - 6, g.bw, 26f, 8f); fillStyle = "#37474F"; fill()
        val midY = (g.y1 + g.y2) / 2
        for (i in 0 until n) {
            val col = WC_COLS[cols[i]]
            val drawWire = { wirePath(g, i); strokeStyle = "rgba(0,0,0,.35)"; lineWidth = 12f; lineCap = "round"; stroke(); wirePath(g, i); strokeStyle = col; lineWidth = 8f; stroke() }
            if (cut[i] == 0f) { drawWire(); continue }
            val u = t - cut[i]; val sag = ease18(u / 0.4f) * 14
            save(); beginPath(); rect(0f, 0f, w, midY - 9); clip(); drawWire(); restore()
            save(); beginPath(); rect(0f, midY + 9, w, h); clip(); translate(0f, sag); drawWire(); restore()
            val x = wireX(g, i)
            fillStyle = "#FFB300"; beginPath(); arc(x, midY - 9, 4f, 0f, TAU); fill(); beginPath(); arc(x, midY + 9 + sag, 4f, 0f, TAU); fill()
            if (u < 0.35f && i != bomb) for (k in 0 until 6) { val a = k + t * 30; beginPath(); moveTo(x, midY); lineTo(x + cos(a) * 16, midY + sin(a) * 16); strokeStyle = "#FFF59D"; lineWidth = 2f; stroke() }
        }
        for (i in 0 until n) { beginPath(); arc(wireX(g, i), g.y2 + 7, 5f, 0f, TAU); fillStyle = "#90A4AE"; fill() }
        for (k in 0 until 3) {
            beginPath(); roundRect(g.bx + g.bw * (0.08f + k * 0.3f), g.by - 10, g.bw * 0.24f, g.bhh + 12, 10f); fillStyle = "#C62828"; fill()
            fillStyle = "rgba(255,255,255,.18)"; fillRect(g.bx + g.bw * (0.1f + k * 0.3f), g.by - 4, g.bw * 0.04f, g.bhh)
        }
        beginPath(); roundRect(g.bx, g.by + g.bhh * 0.22f, g.bw, g.bhh * 0.78f, 12f); fillStyle = "#263238"; fill()
        val tw = g.bw * 0.5f; val th = g.bhh * 0.42f; val tx = w / 2 - tw / 2; val ty = g.by + g.bhh * 0.38f
        beginPath(); roundRect(tx, ty, tw, th, 8f); fillStyle = "#0B0B0B"; fill()
        val sec = if (boomT > 0) 0 else 59 - (floor(clock).toInt() % 60)
        if (boomT > 0 || sin(t * (6 + cuts * 4)) > -0.3f) text("00:" + sec.toString().padStart(2, '0'), w / 2, ty + th / 2 + 1, th * 0.72f, "#FF1744")
        restore()
        if (t - snipT < 0.6f) { val u = (t - snipT) / 0.6f; globalAlpha = 1 - u; text("✂️", snipX, snipY - u * 20, 40f, "#fff"); globalAlpha = 1f }
        val left = cut.count { it == 0f }
        if (boomT == 0f) {
            if (t - safeT < 1.4f) { val u = (t - safeT) / 1.4f; globalAlpha = min(1f, (1 - u) * 3); text(s.wcSafe, w / 2, g.y2 + 52, 19f, "#69F0AE"); globalAlpha = 1f }
            else text(s.wcLeft(left), w / 2, g.y2 + 52, 17f, "#fff")
            text(s.wcTap, w / 2, h - 22, 14f, "#9A9AB0", false)
            return@with
        }
        val u = t - boomT
        if (u < 0.5f) { fillStyle = "rgba(255,240,200,${0.85f * (1 - u / 0.5f)})"; fillRect(0f, 0f, w, h) }
        text("💥", w / 2, g.by + g.bhh / 2, min(w, h) * 0.45f * (0.5f + 0.5f * ease18(u / 0.25f)), "#fff")
        if (u > 0.55f) {
            val p = ease18((u - 0.55f) / 0.35f); val r = min(w * 0.27f, h * 0.2f)
            fillStyle = "rgba(0,0,0,${0.6f * p})"; fillRect(0f, 0f, w, h)
            save(); globalAlpha = p; translate(w / 2, h * 0.45f); scale(0.7f + 0.3f * p, 0.7f + 0.3f * p); dazedWolf(0f, 0f, r, t); restore()
            globalAlpha = p; text(s.wcBoom, w / 2, h * 0.45f + r * 1.35f, 26f, "#FFD740"); text(s.shotWord, w / 2, h * 0.45f + r * 1.35f + 44, 26f, "#FF5A6E"); globalAlpha = 1f
        }
        if (u > 1.3f) pill18(w, h, s.playAgain)
    }
}

// ===================== 🍺 늑대컬링 =====================
/** 🍺 늑대 맥주잔 (앞모습) — 원점 = 잔 바닥 가운데, s = 잔 너비 정도. col = 두건 색, num = 두건 숫자, slosh = 거품 출렁임 */
fun Ctx.wolfMug(x: Float, y: Float, s: Float, col: String, num: String = "", slosh: Float = 0f) {
    save(); translate(x, y)
    beginPath(); ellipse(0f, s * 0.02f, s * 0.55f, s * 0.1f, 0f, 0f, TAU); fillStyle = "rgba(0,0,0,.35)"; fill()
    for ((cl, lw) in listOf("#5E6773" to 0.15f, "#8E98A5" to 0.08f)) {
        beginPath(); moveTo(s * 0.38f, -s * 0.82f); bezierCurveTo(s * 0.82f, -s * 0.85f, s * 0.82f, -s * 0.22f, s * 0.38f, -s * 0.28f)
        strokeStyle = cl; lineWidth = s * lw; lineCap = "round"; stroke()
    }
    beginPath(); arc(s * 0.4f, -s * 0.28f, s * 0.09f, 0f, TAU); fillStyle = "#F3F3F3"; fill()
    for (k in listOf(-1f, 1f)) {
        beginPath(); moveTo(k * s * 0.42f, -s * 0.9f); lineTo(k * s * 0.33f, -s * 1.42f); lineTo(k * s * 0.08f, -s * 1.02f); closePath(); fillStyle = "#6B7480"; fill()
        beginPath(); moveTo(k * s * 0.34f, -s * 0.98f); lineTo(k * s * 0.3f, -s * 1.27f); lineTo(k * s * 0.15f, -s * 1.03f); closePath(); fillStyle = "#FF9EB5"; fill()
    }
    beginPath(); roundRect4(-s * 0.44f, -s * 1.0f, s * 0.88f, s, s * 0.1f, s * 0.1f, s * 0.2f, s * 0.2f)
    linearStops(-s * 0.45f, 0f, s * 0.45f, 0f, listOf(0f to "#7C8591", 0.35f to "#B4BCC6", 1f to "#6B7480")); fill()
    for (k in listOf(-1f, 1f)) { beginPath(); moveTo(k * s * 0.44f, -s * 0.5f); lineTo(k * s * 0.56f, -s * 0.38f); lineTo(k * s * 0.44f, -s * 0.28f); closePath(); fillStyle = "#9AA3AE"; fill() }
    val fy = -s * 1.0f
    fillStyle = "#FFFDF5"
    listOf(-0.3f to 0.13f, -0.12f to 0.16f, 0.08f to 0.15f, 0.27f to 0.13f).forEachIndexed { i, (fx, r) -> beginPath(); arc(fx * s + slosh * s * 0.05f, fy - s * 0.03f + sin(i * 2 + slosh * 3) * s * 0.02f, r * s, 0f, TAU); fill() }
    beginPath(); roundRect(-s * 0.44f, fy - s * 0.02f, s * 0.88f, s * 0.1f, s * 0.05f); fill()
    beginPath(); ellipse(-s * 0.22f + slosh * s * 0.04f, fy + s * 0.13f, s * 0.05f, s * 0.09f, 0f, 0f, TAU); fill()
    beginPath(); ellipse(0f, -s * 0.34f, s * 0.25f, s * 0.17f, 0f, 0f, TAU); fillStyle = "#E9ECEF"; fill()
    for (k in listOf(-1f, 1f)) {
        beginPath(); ellipse(k * s * 0.17f, -s * 0.62f, s * 0.085f, s * 0.1f, 0f, 0f, TAU); fillStyle = "#fff"; fill()
        beginPath(); arc(k * s * 0.16f, -s * 0.6f, s * 0.05f, 0f, TAU); fillStyle = "#1B1B1F"; fill()
        beginPath(); arc(k * s * 0.14f, -s * 0.63f, s * 0.018f, 0f, TAU); fillStyle = "#fff"; fill()
        beginPath(); moveTo(k * s * 0.08f, -s * 0.76f); lineTo(k * s * 0.26f, -s * 0.8f); strokeStyle = "#4A525C"; lineWidth = s * 0.03f; stroke()
    }
    beginPath(); ellipse(0f, -s * 0.42f, s * 0.08f, s * 0.055f, 0f, 0f, TAU); fillStyle = "#1B1B1F"; fill()
    beginPath(); moveTo(-s * 0.1f, -s * 0.3f); quadraticCurveTo(-s * 0.05f, -s * 0.24f, 0f, -s * 0.31f); quadraticCurveTo(s * 0.05f, -s * 0.24f, s * 0.1f, -s * 0.3f); strokeStyle = "#1B1B1F"; lineWidth = s * 0.025f; stroke()
    beginPath(); ellipse(s * 0.03f, -s * 0.24f, s * 0.04f, s * 0.035f, 0f, 0f, TAU); fillStyle = "#FF7A95"; fill()
    beginPath(); roundRect(-s * 0.44f, -s * 0.17f, s * 0.88f, s * 0.11f, s * 0.03f); fillStyle = col; fill()
    beginPath(); moveTo(-s * 0.13f, -s * 0.12f); lineTo(s * 0.13f, -s * 0.12f); lineTo(0f, s * 0.08f); closePath(); fill()
    if (num.isNotEmpty()) text(num, 0f, -s * 0.06f, s * 0.15f, "#fff")
    restore()
}

val CU_COLS = listOf("#FF5A6E", "#4FC3F7", "#FFD54F", "#69F0AE", "#BA68C8", "#FF9800")

@Composable
fun CurlingScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var n by remember { mutableIntStateOf(prefs.getInt("cu_n", 4).coerceIn(2, 6)) }
    SetupThen18(s.cuTitle, onBack, { start -> SetupPanel(s.cuDesc, start) { Stepper(s.players, n, 2..6) { n = it; prefs.edit().putInt("cu_n", it).apply() } } }) {
        val g = remember { CurlingGame(n, vibe) }
        val density = LocalDensity.current.density
        val t0 = remember { System.nanoTime() }
        val now = { (System.nanoTime() - t0) / 1e9f }
        var tick by remember { mutableLongStateOf(0L) }
        LaunchedEffect(g) { while (true) withFrameNanos { tick = it } }
        Canvas(Modifier.fillMaxSize().pointerInput(g) {
            awaitPointerEventScope {
                while (true) {
                    val ev = awaitPointerEvent()
                    for (ch in ev.changes) {
                        val y = ch.position.y / density
                        when { ch.changedToDown() -> g.down(y, now()); ch.changedToUp() -> g.up(now()); ch.pressed -> g.move(y) }
                        ch.consume()
                    }
                }
            }
        }) {
            tick.let { }
            val w = size.width / density; val h = size.height / density
            drawIntoCanvas { cc -> val c = Ctx(cc.nativeCanvas, density); g.draw(c, w, h, now(), s); c.finish() }
        }
    }
}

private class CurlingGame(val n: Int, val vibe: Vibe) {
    val PMAX = 90f
    var turn = 0; val res = arrayOfNulls<Int>(n); var phase = 0 // 0 겨냥 · 1 당기는 중 · 2 미끄러짐 · 3 떨어짐 · 4 다음 · 5 끝
    var pull = 0f; var downY = 0f; var t0 = 0f; var y0 = 0f; var D = 0f; var dur = 1f; var fallY = 0f; var doneT = 0f
    var X0 = 0f; var X1 = 0f; var EDGE = 0f; var START = 0f; var pxcm = 1f; var base = 60f
    fun fresh() { turn = 0; res.fill(null); phase = 0; pull = 0f; fallY = 0f }
    fun laneX(i: Int) = X0 + (X1 - X0) * (i + 0.5f) / n
    fun size(y: Float) = base * (if (n > 4) 0.85f else 1f) * (0.7f + 0.3f * ((y - EDGE) / (START - EDGE)).coerceIn(0f, 1f))
    fun cmOf(y: Float) = max(0, Math.round((y - EDGE) / pxcm))
    fun down(y: Float, t: Float) {
        if (phase == 5) { if (t - doneT > 1.2f) { fresh(); vibe.tap() }; return }
        if (phase != 0) return
        downY = y; phase = 1; pull = 0f
    }
    fun move(y: Float) {
        if (phase != 1) return
        val p = (y - downY).coerceIn(0f, PMAX)
        if (floor(p / 20) != floor(pull / 20) && p > pull) vibe.tap()
        pull = p
    }
    fun up(t: Float) {
        if (phase != 1) return
        if (pull < 8) { phase = 0; pull = 0f; return }
        val k = pull / PMAX; val dmax = (START + 30 + PMAX - EDGE) * 1.25f
        y0 = START + 30 + pull; D = k.pow(1.5f) * dmax; dur = 0.6f + 1.5f * kotlin.math.sqrt(D / dmax)
        fallY = if (y0 - D < EDGE - 4) EDGE - 4 else 0f
        t0 = t; phase = 2; vibe.tick(); Beep.tone(180f, 0.12f, 3)
    }
    fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings) = with(c) {
        X0 = w * 0.13f; X1 = w * 0.87f; EDGE = max(92f, h * 0.13f); START = h - 150; pxcm = (START - EDGE) / 100; base = min(74f, w * 0.19f)
        fillStyle = "#12121A"; fillRect(0f, 0f, w, h)
        fillStyle = "#1E1E28"; fillRect(0f, 40f, w, EDGE - 40)
        for (i in 0 until 9) { fillStyle = "rgba(255,255,255,.03)"; fillRect(i * 52f, 40f, 26f, EDGE - 40) }
        beginPath(); roundRect4(X0, EDGE, X1 - X0, h - EDGE + 20, 6f, 6f, 0f, 0f); fillStyle = "#8D5A34"; fill()
        for (i in 1 until 5) { fillStyle = "rgba(0,0,0,.18)"; fillRect(X0 + (X1 - X0) * i / 5 - 1, EDGE, 2f, h) }
        beginPath(); rect(X0, EDGE, X1 - X0, pxcm * 18); linearStops(0f, EDGE, 0f, EDGE + pxcm * 18, listOf(0f to "rgba(255,215,64,.55)", 1f to "rgba(255,215,64,0)")); fill()
        fillStyle = "#C08A5B"; fillRect(X0, EDGE - 3, X1 - X0, 6f)
        text("🔥", X0 + 14, EDGE + 15, 14f, "#fff"); text("🔥", X1 - 14, EDGE + 15, 14f, "#fff")
        for (k in 0..90 step 10) { val y = EDGE + k * pxcm; fillStyle = "rgba(255,255,255,.35)"; fillRect(X1 + 3, y, 8f, 2f); text("$k", X1 + 22, y + 1, 10f, "rgba(255,255,255,.5)", false) }
        lineDash = floatArrayOf(8f, 8f); beginPath(); moveTo(X0, START); lineTo(X1, START); strokeStyle = "rgba(255,255,255,.4)"; lineWidth = 2f; stroke(); lineDash = null
        for (i in 0 until n) { val x = w / 2 + (i - (n - 1) / 2f) * 30; val on = i == turn && phase != 5; globalAlpha = if (on) 1f else 0.45f; beginPath(); arc(x, 20f, if (on) 12f else 9f, 0f, TAU); fillStyle = CU_COLS[i]; fill(); globalAlpha = 1f; text("${i + 1}", x, 21f, 11f, "#000") }
        var best = -1
        for (i in 0 until n) { val r = res[i] ?: continue; if (r >= 0 && (best < 0 || r < res[best]!!)) best = i }
        for (i in 0 until n) {
            val r = res[i] ?: continue; if (r < 0 || (i == turn && phase != 4 && phase != 5)) continue
            val y = EDGE + r * pxcm; wolfMug(laneX(i), y, size(y), CU_COLS[i], "${i + 1}")
            text((if (i == best) "🥇 " else "") + "${r}cm", laneX(i), y + 14, 12f, "#fff")
        }
        val i = turn; val x = if (i < n) laneX(i) else 0f
        if (phase == 0 || phase == 1) {
            val d = pull; val my = START + 30 + d; val k = d / PMAX
            for (sx in listOf(-1, 1)) { beginPath(); arc(x + sx * 34, START + 4, 6f, 0f, TAU); fillStyle = "#5D4037"; fill() }
            beginPath(); moveTo(x - 34, START + 4); lineTo(x - 18, my - 6); moveTo(x + 34, START + 4); lineTo(x + 18, my - 6); strokeStyle = "#FF7043"; lineWidth = 3.5f; lineCap = "round"; stroke()
            wolfMug(x, my, size(START), CU_COLS[i], "${i + 1}", sin(t * 30) * 0.3f * k)
            if (d > 4) {
                lineDash = floatArrayOf(6f, 8f); beginPath(); moveTo(x, START - 60); lineTo(x, START - 150); strokeStyle = "rgba(255,255,255,.55)"; lineWidth = 3f; stroke(); lineDash = null
                beginPath(); moveTo(x - 10, START - 140); lineTo(x, START - 156); lineTo(x + 10, START - 140); stroke()
            }
            if (phase == 0) { val b = sin(t * 4) * 8; text("👆", x + 6, my + 28 + b, 34f, "#fff"); text(s.cuHint, w / 2, h - 22, 15f, "#fff") }
        } else if (phase == 2) {
            val u = min(1f, (t - t0) / dur); val y = y0 - D * (1 - (1 - u) * (1 - u))
            if (fallY > 0 && y <= fallY) { phase = 3; t0 = t; vibe.strong(); Beep.tone(220f, 0.25f, 3); Beep.tone(90f, 0.5f, 2, 0.12f) }
            else {
                if (u < 1) for (k in 1 until 4) { globalAlpha = 0.12f; wolfMug(x, y + k * 14 * (1 - u), size(y), CU_COLS[i]) }
                globalAlpha = 1f
                wolfMug(x, y, size(y), CU_COLS[i], "${i + 1}", sin(t * 22) * (1 - u))
                if (u >= 1) { res[i] = cmOf(y); phase = 4; t0 = t; vibe.tap(); Beep.tone(523f, 0.15f) }
            }
        }
        if (phase == 3) {
            val f = t - t0; val ye = EDGE - 12; val sz = size(EDGE) * max(0.15f, 1 - f * 0.9f); val rot = min(1.9f, f * 4)
            if (f > 0.15f) { val p = min(1f, (f - 0.15f) / 0.5f); beginPath(); ellipse(x - 8, EDGE - 34, 50 * p, 13 * p, 0f, 0f, TAU); fillStyle = "rgba(255,179,0,.75)"; fill() }
            if (f < 1.1f) { save(); globalAlpha = max(0f, 1 - f / 1.1f); translate(x, ye + f * 25); rotate(-rot); wolfMug(0f, 0f, sz, CU_COLS[i], "${i + 1}", 1f); restore() }
            if (f < 1.2f) for (k in 0 until 7) { val a = -PI.toFloat() / 2 - 1.1f + k * 0.36f; val dd = f * 80; globalAlpha = max(0f, 1 - f); text("💦", x + cos(a) * dd, EDGE - 26 + sin(a) * dd, 15f, "#fff") }
            globalAlpha = 1f
            if (f > 0.3f) text(s.cuFall, w / 2, EDGE + 60, 24f, "#4FC3F7")
            if (f > 1.3f) { res[i] = -1; phase = 4; t0 = t }
        }
        if (phase == 4 && t - t0 > 0.9f) {
            turn++; pull = 0f
            if (turn >= n) { phase = 5; doneT = t; vibe.tick(); if (res.any { (it ?: 0) < 0 }) Beep.tone(330f, 0.4f, 3) } else phase = 0
        }
        if (phase == 5) {
            val v = min(1f, (t - doneT) / 0.4f); globalAlpha = v
            val r = IntArray(n) { res[it] ?: 0 }
            val fallen = (0 until n).filter { r[it] < 0 }
            val losers = if (fallen.isNotEmpty()) fallen else listOf((0 until n).maxByOrNull { r[it] }!!)
            val order = (0 until n).sortedWith(compareBy({ if (r[it] < 0) 1 else 0 }, { r[it] }))
            // 🏆 결과: 크게 (가운데)
            val ph = 330f; val py = max(50f, h * 0.42f - ph / 2); val sc = 0.7f + 0.3f * ease18((t - doneT) / 0.3f)
            fillStyle = "rgba(0,0,0,${0.5f * v})"; fillRect(0f, 0f, w, h)
            save(); translate(w / 2, py + ph / 2); scale(sc, sc); translate(-w / 2, -(py + ph / 2))
            beginPath(); roundRect(14f, py, w - 28, ph, 26f); fillStyle = "rgba(14,14,22,.96)"; fill(); strokeStyle = "#FFD740"; lineWidth = 3f; stroke()
            text("🏆", w / 2, py + 46, 50f, "#fff")
            val cw = min(96f, (w - 48) / n); val cr = min(26f, cw * 0.36f)
            order.forEachIndexed { j, k ->
                val rx = w / 2 + (j - (n - 1) / 2f) * cw; val ry = py + 140
                if (j == 0 && r[k] >= 0) text("🥇", rx, ry - cr - 18, 26f, "#fff")
                beginPath(); arc(rx, ry, cr, 0f, TAU); fillStyle = CU_COLS[k]; fill(); text("${k + 1}", rx, ry + 1, cr * 0.95f, "#000")
                text(if (r[k] < 0) "💦" else "${r[k]}cm", rx, ry + cr + 22, if (r[k] < 0) 26f else 18f, "#fff")
            }
            text(losers.joinToString(" · ") { "${it + 1}" } + " →", w / 2, py + 248, 34f, "#fff")
            text(s.shotWord, w / 2, py + 292, 36f, "#FF5A6E")
            restore()
            globalAlpha = 1f
            if (t - doneT > 1.2f) pill18(w, h, s.playAgain)
        }
    }
}
