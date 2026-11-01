package com.wolfgames.app.games

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.i18n.Strings
import com.wolfgames.app.i18n.cat
import com.wolfgames.app.ui.ChoiceRow
import com.wolfgames.app.ui.Ctx
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.ResultOverlay
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.TAU
import com.wolfgames.app.ui.Text
import com.wolfgames.app.ui.Vibe
import com.wolfgames.app.ui.WG
import com.wolfgames.app.ui.bombArt
import com.wolfgames.app.ui.dazedWolf
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.coinWolfFace
import com.wolfgames.app.ui.text
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

// v1.7 새 게임 (웹 games-v17.js 와 같은 규칙): 숫자 폭탄 · 풍선 불기 (🍀 운빨)

private fun ease17(u: Float) = 1f - (1f - u.coerceIn(0f, 1f)).pow(3)

@Composable
private fun SetupThen17(title: String, onBack: () -> Unit, setup: @Composable (start: () -> Unit) -> Unit, game: @Composable (restart: () -> Unit) -> Unit) {
    var started by rememberSaveable { mutableStateOf(com.wolfgames.app.DebugOpen.autostart) }
    var round by remember { mutableIntStateOf(0) }
    if (!started) GameScaffold(title, onBack) { setup { started = true; round++ } }
    else { BackHandler { started = false }; GameScaffold(title, onBack = { started = false }) { androidx.compose.runtime.key(round) { game { round++ } } } }
}

/** 매 프레임 다시 그리는 캔버스 + 누른 곳 */
@Composable
private fun Anim17(modifier: Modifier, key: Any, onDown: ((Float, Float, Float, Float, Float) -> Unit)? = null, draw: (Ctx, Float, Float, Float) -> Unit) {
    val density = LocalDensity.current.density
    val t0 = remember(key) { System.nanoTime() }
    val now = { (System.nanoTime() - t0) / 1e9f }
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(key) { while (true) withFrameNanos { tick = it } }
    val input = if (onDown == null) Modifier else Modifier.pointerInput(key) {
        awaitPointerEventScope {
            while (true) {
                val ev = awaitPointerEvent()
                for (ch in ev.changes) if (ch.changedToDown()) { onDown(ch.position.x / density, ch.position.y / density, now(), size.width / density, size.height / density); ch.consume() }
            }
        }
    }
    Canvas(modifier.then(input)) {
        tick.let { }
        val w = size.width / density; val h = size.height / density
        drawIntoCanvas { cc -> val c = Ctx(cc.nativeCanvas, density); draw(c, w, h, now()); c.finish() }
    }
}

// ===================== 🔢 숫자 폭탄 =====================
val NB_MAX = listOf(30, 50, 100, 200)

@Composable
fun NumberBombScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var maxI by remember { mutableIntStateOf(NB_MAX.indexOf(prefs.getInt("nb_max", 100)).coerceAtLeast(0)) }
    var toSetup by remember { mutableIntStateOf(0) }
    androidx.compose.runtime.key(toSetup) {
        SetupThen17(s.nbTitle, onBack, { start ->
            SetupPanel(s.nbDesc, start) { ChoiceRow(s.nbMax, NB_MAX.map { "1 ~ $it" }, maxI) { maxI = it; prefs.edit().putInt("nb_max", NB_MAX[it]).apply() } }
        }) { restart -> NumberBombGame(NB_MAX[maxI], s, vibe, restart) { com.wolfgames.app.DebugOpen.autostart = false; toSetup++ } }
    }
}

@Composable
private fun NumberBombGame(max: Int, s: Strings, vibe: Vibe, restart: () -> Unit, toSetup: () -> Unit) {
    // 점검용(adb 로 바로 열 때만): 폭탄 숫자를 1로 고정해서 터지는 화면을 확인
    val bomb = remember { if (com.wolfgames.app.DebugOpen.autostart && com.wolfgames.app.DebugOpen.screen == "NumberBomb") 1 else 1 + Random.nextInt(max) }
    var lo by remember { mutableIntStateOf(1) }
    var hi by remember { mutableIntStateOf(max) }
    var typed by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf(s.nbEnter) }
    var msgCol by remember { mutableStateOf(WG.Text) }
    var boomAt by remember { mutableFloatStateOf(0f) }
    var shakeAt by remember { mutableFloatStateOf(-9f) }
    var showRes by remember { mutableStateOf(false) }
    val hist = remember { mutableStateListOf<Pair<Int, Boolean>>() }
    val t0 = remember { System.nanoTime() }
    val now = { (System.nanoTime() - t0) / 1e9f }
    LaunchedEffect(boomAt) { if (boomAt > 0) { kotlinx.coroutines.delay(900); msg = cat(s.nbBoom, " $bomb"); msgCol = Color(0xFFFF8A80); showRes = true } }
    fun press(k: String) {
        if (boomAt > 0) return
        vibe.tap()
        when (k) {
            "⌫" -> typed = typed.dropLast(1)
            "ok" -> {
                val n = typed.toIntOrNull() ?: 0; typed = ""
                if (n < lo || n > hi) { msg = s.nbOut; msgCol = Color(0xFFFFD740); return }
                if (n == bomb) { lo = n; hi = n; boomAt = now(); vibe.strong(); return }
                if (n < bomb) { lo = n + 1; msg = cat(s.nbUp, " · ", s.nbPass); msgCol = Color(0xFF69F0AE); hist.add(n to true) }
                else { hi = n - 1; msg = cat(s.nbDown, " · ", s.nbPass); msgCol = Color(0xFFFF8A80); hist.add(n to false) }
                shakeAt = now()
            }
            else -> if (typed.length < max.toString().length) typed = (typed + k).trimStart('0')
        }
    }
    Column(Modifier.fillMaxSize().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Anim17(Modifier.fillMaxWidth().weight(1f), "nb") { c, w, h, t ->
            val fuse = (hi - lo + 1).toFloat() / max; val cx = w / 2; val cy = h * 0.58f; val r = min(w, h) * 0.3f
            if (boomAt == 0f) {
                val k = t - shakeAt; val sh = if (k < 0.3f) sin(k * 60) * 4 * (1 - k / 0.3f) else 0f
                c.bombArt(cx + sh, cy, r, max(0.05f, fuse), t)
            } else {
                val u = t - boomAt
                if (u > 0.55f) {
                    // 💥 다음 연기 속에서 그을린 늑대가 헤롱헤롱
                    val p = ease17((u - 0.55f) / 0.35f); val rw = min(w * 0.27f, h * 0.3f)
                    c.save(); c.globalAlpha = p; c.translate(cx, cy); c.scale(0.7f + 0.3f * p, 0.7f + 0.3f * p); c.dazedWolf(0f, 0f, rw, t); c.restore()
                    c.globalAlpha = max(0f, 1 - (u - 0.55f) / 0.5f); c.beginPath(); c.arc(cx, cy, rw * 1.5f, 0f, TAU); c.fillStyle = "#9097A3"; c.fill(); c.globalAlpha = 1f
                    if (u > 1f) c.text(s.nbDazed, cx + sin(t * 3) * 6, min(h - 14, cy + rw * 1.05f + 14), 18f, "#FFD740")
                } else {
                    val cols = listOf("#FFD740", "#FF7043", "#FF3D5A")
                    for (k in 0 until 18) {
                        val a = k / 18f * 2 * PI.toFloat(); val d = ease17(u / 0.6f) * min(w, h) * (0.3f + 0.2f * (k % 3))
                        c.globalAlpha = max(0f, 1 - u / 1.4f); c.beginPath(); c.arc(cx + cos(a) * d, cy + sin(a) * d, 10f - (k % 3) * 2, 0f, TAU); c.fillStyle = cols[k % 3]; c.fill()
                    }
                    c.globalAlpha = 1f
                    c.text("💥", cx, cy, min(w, h) * 0.5f * (0.6f + 0.4f * ease17(u / 0.25f)), "#fff")
                }
            }
        }
        Text("$lo ~ $hi", color = WG.Accent, fontSize = 44.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text(msg, color = msgCol, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        if (showRes) {
            // 결과: 창으로 덮지 않고 키패드 자리에 (늑대가 가려지지 않게)
            Text(s.shotWord, color = Color(0xFFFF3D5A), fontSize = 26.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            BigButton(s.playAgain, Modifier.fillMaxWidth(), onClick = restart)
            BigButton(s.again, Modifier.fillMaxWidth(), color = WG.Surface2, contentColor = WG.Text, onClick = toSetup)
            return@Column
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)) {
            hist.takeLast(8).forEach { (n, up) -> Text("$n${if (up) "⬆️" else "⬇️"}", color = WG.Text, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(WG.Surface).padding(horizontal = 8.dp, vertical = 2.dp)) }
        }
        Text(typed.ifEmpty { "—" }, color = WG.Text, fontSize = 34.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(WG.Surface).padding(6.dp))
        for (row in listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("⌫", "0", "ok"))) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { k ->
                    val ok = k == "ok"
                    Box(Modifier.weight(1f).heightIn(min = 54.dp).clip(RoundedCornerShape(14.dp)).background(if (ok) WG.Accent else WG.Surface2).clickable { press(k) }, contentAlignment = Alignment.Center) {
                        Text(if (ok) s.nbOk else k, color = if (ok) Color.Black else WG.Text, fontSize = if (ok) 18.sp else 24.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

// ===================== 🎈 풍선 불기 =====================
private val BL_COLS = listOf("#FF6B81" to "#E0435C", "#4FC3F7" to "#1E88E5", "#81C784" to "#2E7D32", "#BA68C8" to "#7B1FA2", "#FFB74D" to "#EF6C00")
const val BL_MIN = 4
const val BL_MAX = 24

private class BalloonGame(val vibe: Vibe) {
    var ci = 0; var pumps = 0; var popAt = 0; var lastPump = -9f; var popT = 0f; var col = BL_COLS[0]
    val plus = ArrayList<FloatArray>()
    init { round() }
    fun round() { pumps = 0; popAt = BL_MIN + Random.nextInt(BL_MAX - BL_MIN + 1); lastPump = -9f; popT = 0f; col = BL_COLS[ci++ % BL_COLS.size]; plus.clear() }
    fun down(x: Float, t: Float, w: Float) {
        if (popT > 0) { if (t - popT > 1.2f) { round(); vibe.tap() }; return }
        pumps++; lastPump = t; plus.add(floatArrayOf(t, x.coerceIn(30f, w - 30)))
        if (pumps >= popAt) { popT = t; vibe.strong() } else vibe.tap()
    }
    fun draw(c: Ctx, w: Float, h: Float, t: Float, s: Strings) = with(c) {
        fillStyle = "#12121A"; fillRect(0f, 0f, w, h)
        val cx = w / 2; val cy = h * 0.44f; val base = min(w, h) * 0.15f
        val R = base * (1 + min(1f, pumps.toFloat() / BL_MAX) * 1.7f)
        if (popT == 0f) {
            val grow = min(1f, pumps.toFloat() / BL_MAX)
            val k = t - lastPump; val sq = if (k < 0.35f) sin(k * 30) * 0.08f * (1 - k / 0.35f) else 0f
            val wob = sin(t * 2.2f) * 0.04f * (1 + grow * 2)
            beginPath(); moveTo(cx, cy + R * 1.12f); bezierCurveTo(cx + 18, cy + R * 1.4f, cx - 18, cy + R * 1.6f, cx + 6, h - 70); strokeStyle = "rgba(255,255,255,.6)"; lineWidth = 2f; stroke()
            save(); translate(cx, cy); rotate(wob); scale(1 + sq, 1 - sq)
            beginPath(); moveTo(-8f, R * 1.13f); lineTo(8f, R * 1.13f); lineTo(0f, R * 1.0f); closePath(); fillStyle = col.second; fill()
            beginPath(); ellipse(0f, 0f, R * 0.92f, R * 1.05f, 0f, 0f, TAU)
            radialFill(-R * 0.35f, -R * 0.45f, R * 0.1f, R * 1.15f, listOf(0f to "#ffffff", 0.18f to col.first, 1f to col.second)); fill()
            beginPath(); ellipse(-R * 0.4f, -R * 0.5f, R * 0.14f, R * 0.26f, -0.5f, 0f, TAU); fillStyle = "rgba(255,255,255,.55)"; fill()
            save(); globalAlpha = 0.9f; translate(0f, R * 0.08f); coinWolfFace(R * 0.55f); restore()
            restore()
            plus.removeAll { t - it[0] >= 0.8f }
            for (p in plus) { val u = (t - p[0]) / 0.8f; globalAlpha = 1 - u; text("+1", p[1], cy - R * 1.1f - u * 40, 26f, "#FFD740"); globalAlpha = 1f }
            text(s.blPumps(pumps), w / 2, h - 52, 20f, "#fff")
            text(s.blTap, w / 2, h - 22, 14f, "#9A9AB0", false)
            return@with
        }
        val u = t - popT
        for (k in 0 until 14) {
            val a = k / 14f * 2 * PI.toFloat() + k; val d = ease17(u / 0.5f) * R * (1.4f + (k % 4) * 0.35f)
            save(); translate(cx + cos(a) * d, cy + sin(a) * d + u * u * 120); rotate(a + u * 6); globalAlpha = max(0f, 1 - u / 1.6f)
            beginPath(); moveTo(-10f, -6f); lineTo(12f, -3f); lineTo(6f, 8f); lineTo(-8f, 5f); closePath(); fillStyle = if (k % 2 == 0) col.first else col.second; fill(); restore()
        }
        globalAlpha = 1f
        text("💥", cx, cy, R * 1.6f * (0.6f + 0.4f * ease17(u / 0.2f)), "#fff")
        if (u > 0.5f) { text(s.blPop, w / 2, cy + R * 1.25f + 10, 30f, "#FFD740"); text(s.shotWord, w / 2, cy + R * 1.25f + 56, 26f, "#FF5A6E") }
        if (u > 1.2f) { fillStyle = "rgba(0,0,0,.55)"; beginPath(); roundRect(w / 2 - 110, h - 86, 220f, 44f, 22f); fill(); text(s.playAgain, w / 2, h - 64, 17f, "#fff") }
    }
}

@Composable
fun BalloonScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current
    SetupThen17(s.blTitle, onBack, { start -> SetupPanel(s.blDesc, start) {} }) {
        val g = remember { BalloonGame(vibe) }
        Anim17(Modifier.fillMaxSize(), g, onDown = { x, _, t, w, _ -> g.down(x, t, w) }) { c, w, h, t -> g.draw(c, w, h, t, s) }
    }
}
