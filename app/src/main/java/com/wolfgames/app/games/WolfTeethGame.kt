package com.wolfgames.app.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.Ctx
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.TAU
import com.wolfgames.app.ui.VTextButton
import com.wolfgames.app.ui.WG
import com.wolfgames.app.ui.text
import kotlin.math.PI
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

// 늑대이빨: 화면 가득 벌린 늑대 입(큰 이빨 20개) → 당첨이면 콱 닫히고 늑대 얼굴 + 다시 버튼 (웹 teeth-art.js 와 같은 그림)

private const val TEETH = 20
private const val TW = 393f
private const val TH = 716f
private const val FUR = "#A3ABB7"
private const val FUR_D = "#6F7887"
private const val FUR_L = "#CCD2DA"
private const val INK = "#25252D"
private const val OL = "#4A515D"
private const val PIf = PI.toFloat()

/** 이빨 자리 (설계 좌표): x, y, 위턱? */
private class ToothPos(val x: Float, val y: Float, val up: Boolean)
private val LAYOUT: List<ToothPos> = run {
    val t = ArrayList<ToothPos>()
    fun X(f: Float) = f * TW
    fun Y(f: Float) = f * TH
    for ((fx, fy) in listOf(0.19f to 0.225f, 0.39f to 0.21f, 0.61f to 0.21f, 0.81f to 0.225f)) t.add(ToothPos(X(fx), Y(fy), true))
    for (fy in listOf(0.305f, 0.385f, 0.465f)) t.add(ToothPos(X(0.11f), Y(fy), true))
    for (fy in listOf(0.305f, 0.385f, 0.465f)) t.add(ToothPos(X(0.89f), Y(fy), true))
    for ((fy, fx) in listOf(0.585f to 0.135f, 0.665f to 0.12f, 0.745f to 0.115f)) t.add(ToothPos(X(fx), Y(fy), false))
    for ((fy, fx) in listOf(0.585f to 0.865f, 0.665f to 0.88f, 0.745f to 0.885f)) t.add(ToothPos(X(fx), Y(fy), false))
    for ((fx, fy) in listOf(0.19f to 0.825f, 0.39f to 0.84f, 0.61f to 0.84f, 0.81f to 0.825f)) t.add(ToothPos(X(fx), Y(fy), false))
    t
}
private val MID = 0.525f * TH
private val RESTART_Y = 0.74f * TH
private const val RESTART_R = 46f

private fun Ctx.tooth(x: Float, y: Float, up: Boolean, pressed: Boolean, red: Boolean) {
    save(); translate(x, y); if (!up) rotate(PIf)
    val w = 27f; val h = 30f
    beginPath(); ellipse(0f, -h * 0.62f, w * 1.08f, h * 0.32f, 0f, 0f, TAU); fillStyle = "rgba(60,0,10,.5)"; fill()
    if (pressed) { translate(0f, -h * 0.4f); scale(0.88f, 0.55f) }
    beginPath(); moveTo(-w, -h * 0.55f); bezierCurveTo(-w, h * 0.25f, -w * 0.5f, h, 0f, h); bezierCurveTo(w * 0.5f, h, w, h * 0.25f, w, -h * 0.55f)
    bezierCurveTo(w, -h * 0.85f, -w, -h * 0.85f, -w, -h * 0.55f); closePath()
    val stops = if (red) listOf(0f to "#FF8A9A", 0.55f to "#FF3D5A", 1f to "#B3132E") else if (pressed) listOf(0f to "#d6d0c8", 0.55f to "#b3aca4", 1f to "#8f8880") else listOf(0f to "#FFFFFF", 0.55f to "#F1EDE7", 1f to "#BDB5AC")
    radialFill(-w * 0.12f, -h * 0.03f, 3f, h * 1.15f, stops); fill(); lineWidth = 2.5f; strokeStyle = "rgba(80,55,45,.45)"; stroke()
    if (!pressed) { beginPath(); ellipse(-w * 0.32f, h * 0.05f, w * 0.16f, h * 0.32f, 0.15f, 0f, TAU); fillStyle = "rgba(255,255,255,.85)"; fill() }
    restore()
}

private fun Ctx.ears(w: Float, y: Float, s: Float) {
    for (sd in listOf(-1f, 1f)) {
        save(); translate(w / 2 + sd * w * 0.3f, y); scale(sd, 1f)
        beginPath(); moveTo(-58 * s, 20f); quadraticCurveTo(-4 * s, -92 * s, 22 * s, -96 * s); quadraticCurveTo(46 * s, -50 * s, 58 * s, 20f); closePath()
        fillStyle = FUR_D; fill(); lineWidth = 5f; strokeStyle = OL; stroke()
        beginPath(); moveTo(-30 * s, 12f); quadraticCurveTo(0f, -60 * s, 18 * s, -62 * s); quadraticCurveTo(32 * s, -34 * s, 30 * s, 12f); closePath(); fillStyle = "#F6AFC0"; fill()
        restore()
    }
}

private fun Ctx.openMouth(pressed: BooleanArray, traps: Set<Int>) {
    fun X(f: Float) = f * TW
    fun Y(f: Float) = f * TH
    ears(TW, Y(0.122f), 0.86f)
    // 털 테두리
    beginPath(); roundRect(X(0.005f), Y(0.085f), X(0.99f), Y(0.915f) - Y(0.085f), X(0.2f))
    linearStops(X(0.005f), 0f, X(0.995f), 0f, listOf(0f to FUR_D, 0.18f to FUR, 0.5f to FUR_L, 0.82f to FUR, 1f to FUR_D)); fill(); lineWidth = 5f; strokeStyle = OL; stroke()
    // 코
    save(); translate(TW / 2, Y(0.12f))
    beginPath(); moveTo(-46f, -12f); bezierCurveTo(-46f, -30f, 46f, -30f, 46f, -12f); bezierCurveTo(46f, 8f, 12f, 22f, 0f, 22f); bezierCurveTo(-12f, 22f, -46f, 8f, -46f, -12f); closePath()
    fillStyle = INK; fill()
    for (sd in listOf(-1f, 1f)) { beginPath(); ellipse(sd * 18, -2f, 10f, 5f, sd * 0.5f, 0f, TAU); fillStyle = "#000"; fill() }
    beginPath(); ellipse(-16f, -18f, 16f, 5f, -0.15f, 0f, TAU); fillStyle = "rgba(255,255,255,.4)"; fill(); restore()
    // 입 안
    val mx = X(0.025f); val my = Y(0.165f); val mw = X(0.95f); val mh = Y(0.885f) - Y(0.165f)
    beginPath(); roundRect(mx, my, mw, mh, X(0.13f))
    linearStops(0f, my, 0f, my + mh, listOf(0f to "#B8323A", 0.12f to "#6E1219", 0.5f to "#1E0306", 0.8f to "#5A0E14", 1f to "#D9473F")); fill(); lineWidth = 4f; strokeStyle = "#3A060B"; stroke()
    beginPath(); roundRect(X(0.2f), Y(0.27f), X(0.6f), Y(0.6f), X(0.08f)); radialFill(TW / 2, MID, 20f, TW * 0.6f, listOf(0f to "rgba(0,0,0,.55)", 1f to "rgba(0,0,0,0)")); fill()
    beginPath(); moveTo(mx + 6, MID); lineTo(mx + mw - 6, MID); strokeStyle = "rgba(0,0,0,.35)"; lineWidth = 2f; stroke()
    // 혀
    beginPath(); roundRect(X(0.25f), Y(0.6f), X(0.5f), Y(0.2f), X(0.12f)); linearStops(0f, Y(0.6f), 0f, Y(0.8f), listOf(0f to "#5E1622", 1f to "#C94A5C")); fill()
    LAYOUT.forEachIndexed { i, p -> tooth(p.x, p.y, p.up, pressed[i], pressed[i] && i in traps) }
}

/** 닫은 입: 원래 게임의 늑대 얼굴 그대로, 입만 꽉 닫음 + 다시 버튼 */
private fun Ctx.closedFace(bite: Float) {
    val k = (TW * 0.92f) / 722f; val cy = TH * 0.42f
    save(); translate(TW / 2 - 361 * k, cy - 252 * k); scale(k, k)
    for (e in listOf(floatArrayOf(60f, 80f, 158f, -87f, 300f, 60f, 118f, 40f, 158f, -36f, 212f, 30f), floatArrayOf(662f, 80f, 564f, -87f, 422f, 60f, 604f, 40f, 564f, -36f, 510f, 30f))) {
        beginPath(); moveTo(e[0], e[1]); lineTo(e[2], e[3]); lineTo(e[4], e[5]); closePath(); fillStyle = "#6E7A8A"; fill()
        beginPath(); moveTo(e[6], e[7]); lineTo(e[8], e[9]); lineTo(e[10], e[11]); closePath(); fillStyle = "#F6AFC0"; fill()
    }
    beginPath(); roundRect4(0f, 0f, 722f, 503f, 230f, 230f, 160f, 160f); fillStyle = "#9AA5B4"; fill()
    save(); clip()
    beginPath(); moveTo(310f, 0f); lineTo(361f, 78f); lineTo(412f, 0f); closePath(); fillStyle = "#D9D5CE"; fill()
    beginPath(); ellipse(361f, 186f, 150f, 72f, 0f, 0f, TAU); fillStyle = "#F2EAE0"; fill()
    beginPath(); ellipse(361f, 448f, 200f, 44f, 0f, 0f, TAU); fillStyle = "#F2EAE0"; fill()
    restore()
    lineWidth = 7f; strokeStyle = "#2B2B33"
    beginPath(); moveTo(200f, 60f); lineTo(260f, 90f); lineTo(200f, 120f); stroke()
    beginPath(); moveTo(522f, 60f); lineTo(462f, 90f); lineTo(522f, 120f); stroke()
    for (x in listOf(211f, 511f)) { beginPath(); ellipse(x, 153f, 44f, 22f, 0f, 0f, TAU); fillStyle = "rgba(255,143,163,.4)"; fill() }
    beginPath(); ellipse(361f, 165f, 43f, 18f, 0f, 0f, TAU); fillStyle = "#2B2B33"; fill()
    beginPath(); ellipse(352f, 160f, 12f, 5f, 0f, 0f, TAU); fillStyle = "rgba(255,255,255,.5)"; fill()
    val my = 268 + bite * 6
    beginPath(); moveTo(361f, 183f); lineTo(361f, my); lineWidth = 5f; stroke()
    beginPath(); moveTo(80f, my - 18); quadraticCurveTo(220f, my + 4, 361f, my); quadraticCurveTo(502f, my + 4, 642f, my - 18); lineWidth = 7f; stroke()
    for (t in listOf(floatArrayOf(170f, 34f, 30f), floatArrayOf(278f, 40f, 40f), floatArrayOf(444f, 40f, 40f), floatArrayOf(552f, 34f, 30f))) {
        val yy = my - 18 * ((t[0] - 361) / 281).pow(2) + 2
        beginPath(); moveTo(t[0] - t[1] / 2, yy); lineTo(t[0], yy + t[2]); lineTo(t[0] + t[1] / 2, yy); closePath(); fillStyle = "#FFFDF5"; fill()
    }
    restore()
    val bx = TW / 2; val by = RESTART_Y
    beginPath(); arc(bx, by, RESTART_R, 0f, TAU); radialFill(bx - 8, by - 10, 6f, 50f, listOf(0f to "#FF6B6B", 1f to "#D32F2F")); fill()
    beginPath(); arc(bx, by + 3, 22f, -2.4f, 2.6f); strokeStyle = "#F5E6E6"; lineWidth = 9f; stroke()
    beginPath(); moveTo(bx - 30, by - 22); lineTo(bx - 13, by - 22); lineTo(bx - 22, by - 6); closePath(); fillStyle = "#F5E6E6"; fill()
}

@Composable
fun WolfTeethGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    var prizes by rememberSaveable { mutableIntStateOf(1) }
    var started by rememberSaveable { mutableStateOf(com.wolfgames.app.DebugOpen.autostart) }
    GameScaffold(s.menuCroc, onBack) {
        if (!started) SetupPanel(description = s.teethDesc, onStart = { started = true }) { Stepper(s.prizes, prizes, 1..10) { prizes = it } }
        else WolfTeethPlay(prizes, onSetup = { started = false })
    }
}

private class TeethState(val k: Int) {
    var traps = setOf<Int>(); var pressed = BooleanArray(TEETH); var found = 0; var bitAt = -1f
    val ripples = ArrayDeque<Pair<Float, Int>>()
    init { reset() }
    fun reset() { traps = (0 until TEETH).shuffled().take(k).toSet(); pressed = BooleanArray(TEETH); found = 0; bitAt = -1f; ripples.clear() }
}

@Composable
private fun WolfTeethPlay(k: Int, onSetup: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    val density = LocalDensity.current.density
    val st = remember(k) { TeethState(k) }
    val t0 = remember { System.nanoTime() }
    val now = { (System.nanoTime() - t0) / 1e9f }
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) { while (true) withFrameNanos { tick = it } }
    val view = remember { floatArrayOf(1f, 0f, 0f) } // 배율, x 여백, y 여백
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().weight(1f)) {
            Canvas(
                Modifier.fillMaxSize().pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val ev = awaitPointerEvent()
                            for (ch in ev.changes) if (ch.changedToDown()) {
                                ch.consume()
                                val x = (ch.position.x / density - view[1]) / view[0]; val y = (ch.position.y / density - view[2]) / view[0]; val t = now()
                                if (st.bitAt >= 0) {
                                    if (t - st.bitAt > 0.6f && hypot(x - TW / 2, y - RESTART_Y) < RESTART_R + 14) { vibe.tap(); if (st.found >= k) st.reset() else st.bitAt = -1f }
                                    continue
                                }
                                var best = -1; var bd = 46f // 손가락이 닿은 곳에서 가장 가까운 이빨 (넉넉하게)
                                LAYOUT.forEachIndexed { i, p -> if (!st.pressed[i]) { val d = hypot(x - p.x, y - p.y); if (d < bd) { bd = d; best = i } } }
                                if (best < 0) continue
                                st.pressed[best] = true; st.ripples.addLast(t to best); if (st.ripples.size > 8) st.ripples.removeFirst()
                                if (best in st.traps) { st.found++; st.bitAt = t; vibe.strong() } else vibe.tap()
                            }
                        }
                    }
                },
            ) {
                tick.let { }
                val w = size.width / density; val h = size.height / density
                val sc = min(w / TW, h / TH); view[0] = sc; view[1] = (w - TW * sc) / 2; view[2] = (h - TH * sc) / 2
                drawIntoCanvas { cc ->
                    val c = Ctx(cc.nativeCanvas, density)
                    c.fillStyle = "#12121A"; c.fillRect(0f, 0f, w, h)
                    c.save(); c.translate(view[1], view[2]); c.scale(sc, sc)
                    val t = now(); val bt = if (st.bitAt >= 0) t - st.bitAt else -1f
                    val count = st.pressed.count { it }; val all = st.found >= k
                    when {
                        bt < 0.1f -> {
                            c.openMouth(st.pressed, st.traps)
                            if (bt < 0) c.text(s.teethLeft(TEETH - count, k - st.found), TW / 2, TH * 0.035f, 15f, "#FFD740")
                            for ((tt, i) in st.ripples) { val u = (t - tt) / 0.4f; if (u < 0 || u > 1) continue; val p = LAYOUT[i]; c.beginPath(); c.arc(p.x, p.y, 26 + 26 * u, 0f, TAU); c.strokeStyle = "rgba(255,255,255,${0.8f * (1 - u)})"; c.lineWidth = 4f; c.stroke() }
                        }
                        bt < 0.22f -> { val u = (bt - 0.1f) / 0.12f; c.save(); c.translate(0f, MID); c.scale(1f, 1 - 0.8f * u); c.translate(0f, -MID); c.openMouth(st.pressed, st.traps); c.restore() }
                        else -> {
                            val sh = if (bt < 0.7f) sin(bt * 70) * 5 * (1 - (bt - 0.22f) / 0.5f) else 0f
                            c.save(); c.translate(sh, 0f); c.closedFace(if (bt < 0.6f) 1f else 0f); c.restore()
                            if (bt < 0.4f) { c.fillStyle = "rgba(255,40,60,${0.4f * (1 - (bt - 0.22f) / 0.18f)})"; c.fillRect(0f, 0f, TW, TH) }
                            val u = min(1f, (bt - 0.22f) / 0.2f)
                            c.save(); c.translate(TW / 2, TH * 0.08f); c.scale(0.6f + 0.4f * u, 0.6f + 0.4f * u)
                            c.text(s.bitten, 0f, 0f, 34f, "#FF3D5A"); c.restore()
                            c.text(if (all && k > 1) s.allFound else s.bittenAt(count), TW / 2, TH * 0.14f, 15f, "#bbbbbb")
                            c.text(if (all) s.playAgain else s.continueN(k - st.found), TW / 2, RESTART_Y + RESTART_R + 26, 17f, "#fff")
                        }
                    }
                    c.restore(); c.finish()
                }
            }
        }
        Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            VTextButton(onClick = { vibe.tap(); st.reset() }) { Text("🔀 ${s.shuffle}", color = WG.SubText, fontSize = 15.sp) }
            VTextButton(onClick = onSetup) { Text(s.changePrizes, color = WG.SubText, fontSize = 15.sp) }
        }
    }
}
