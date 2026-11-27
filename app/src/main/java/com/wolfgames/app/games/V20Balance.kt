package com.wolfgames.app.games

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

// ===================== ⚖️ 늑대 균형 잡기 (웹 games-v20.js balance 와 같음) =====================
// 2026-11-27: 한 턴 지날 때마다 Lv 올라감 — 접시 더 작게, 더 미끄럽게, 바람 세고 자주, 계속 미는 바람(빙글 도는 방향).
// 기울기 센서(없으면 손가락)로 접시를 기울여 늑대를 위에. 한 사람 6초 → "넘겨!" 3초 (늑대 멈춤) → 다음 사람. 갈수록 접시 작아지고 바람 세짐

private const val BB_HOLD = 6f
private const val BB_PASS = 3f
private const val BB_ANG = (PI * 2).toFloat() // 진짜 한 바퀴 (방향 계산용)

@Composable
fun BalanceScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var n by remember { mutableIntStateOf(prefs.getInt("bb_n", 4).coerceIn(1, 8)) }
    SetupThen18(s.bbTitle, onBack, { start ->
        SetupPanel(s.bbDesc, start) {
            Stepper(s.players, n, 1..8) { n = it; prefs.edit().putInt("bb_n", it).apply() }
        }
    }) {
        val g = remember { BalanceGame(n, vibe) }
        // 가속도 센서 (폰 평평하게, 화면 위, 세로)
        DisposableEffect(g) {
            val sm = ctx.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            val sensor = sm?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val v = event.values
                    if (v.size < 2) return
                    g.tiltX = (-v[0] / 9.81f * 2f).coerceIn(-1f, 1f) // 오른쪽 내리면 → 오른쪽으로
                    g.tiltY = (v[1] / 9.81f * 2f).coerceIn(-1f, 1f)  // 위쪽 들면 → 아래로
                    g.sensor = true
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            if (sm != null && sensor != null) sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
            onDispose { sm?.unregisterListener(listener) }
        }
        AnimPtr(g, onDown = { x, y, t, w, h -> g.finger(x, y, w, h); g.down(t) }, onMove = { x, y, w, h -> g.finger(x, y, w, h) }) { c, w, h, t -> g.draw(c, w, h, t, s) }
    }
}

private class BalanceGame(val P: Int, val vibe: Vibe) {
    class Gust(val a: Float, val t: Float, val f: Float)
    class Drift(var a: Float, val w: Float, val f: Float)

    // 입력
    @Volatile var tiltX = 0f
    @Volatile var tiltY = 0f
    @Volatile var sensor = false
    var fingerX = 0f; var fingerY = 0f; var useFinger = false

    // 상태 (play = 실제로 버틴 시간, 넘기는 동안은 안 셈)
    var started = false
    var x = 0f; var y = 0f; var vx = 0f; var vy = 0f
    var t0 = 0f; var play = 0f; var cur = 0; var phaseT = 0f; var passing = false; var lastCount = -1
    var dropped = false; var dropT = 0f; var dropA = 0f
    var gust: Gust? = null; var nextGust = 3.5f; var lastT = 0f
    var lv = 0; var drift: Drift? = null

    fun reset(t: Float) {
        x = 0f; y = 0f; vx = 0f; vy = 0f; t0 = t; play = 0f; cur = 0; phaseT = t; passing = false; lastCount = -1
        dropped = false; dropT = 0f; gust = null; nextGust = 3.5f; lastT = t; lv = 0; drift = null
    }

    fun finger(px: Float, py: Float, w: Float, h: Float) {
        if (w <= 0f) return
        fingerX = ((px - w / 2) / (w * 0.4f)).coerceIn(-1f, 1f)
        fingerY = ((py - h * 0.52f) / (w * 0.4f)).coerceIn(-1f, 1f)
    }

    fun down(t: Float) {
        if (dropped && t - dropT > 1.8f) { reset(t); vibe.tap() }
    }

    fun draw(c: Ctx, W: Float, H: Float, t: Float, s: Strings) {
        if (!started) { started = true; reset(t) }
        val dt = min(0.05f, t - lastT); lastT = t
        if (!passing && !dropped) play += dt
        val el = play
        val r0 = min(W, H - 160) * 0.42f
        val pr = r0 * max(0.32f, 1 - el * 0.02f - lv * 0.035f)
        val cx = W / 2; val cy = H * 0.52f
        if (!sensor && el > 1.2f) useFinger = true
        val tx = if (useFinger) fingerX else tiltX
        val ty = if (useFinger) fingerY else tiltY

        // 물리 (바람은 실제로 버틴 시간 el 기준)
        if (!dropped && !passing) {
            var ax = tx * 460 * (1 + lv * 0.1f); var ay = ty * 460 * (1 + lv * 0.1f)
            val dr = drift
            if (dr != null) { dr.a += dr.w * dt; ax += cos(dr.a) * dr.f; ay += sin(dr.a) * dr.f } // 한 턴 지나면: 계속 미는 바람
            if (el > nextGust && gust == null) { gust = Gust(Random.nextFloat() * BB_ANG, el, 320 + el * 10 + lv * 70) }
            val gu = gust
            if (gu != null) {
                val u = (el - gu.t) / 0.9f
                if (u > 1) { gust = null; nextGust = el + max(if (lv > 0) 1.1f else 1.8f, 4.5f - el * 0.06f - lv * 0.4f) + Random.nextFloat() * (if (lv > 0) 1f else 1.5f) }
                else { val k = sin(u * PI.toFloat()); ax += cos(gu.a) * gu.f * k; ay += sin(gu.a) * gu.f * k }
            }
            val damp = 0.986f + min(0.007f, lv * 0.0015f); vx = (vx + ax * dt) * damp; vy = (vy + ay * dt) * damp; x += vx * dt; y += vy * dt
            if (hypot(x, y) > pr) {
                dropped = true; dropT = t; dropA = atan2(y, x); vibe.strong()
                Beep.tone(120f, 0.5f, 2, 0.2f); Beep.tone(70f, 0.6f, 2, 0.2f)
            }
            // 차례
            val ph = t - phaseT
            if (!dropped && ph > BB_HOLD) { passing = true; phaseT = t; vx = 0f; vy = 0f; gust = null; lastCount = -1; vibe.tap(); Beep.tone(880f, 0.1f, 3, 0.18f) }
        } else if (!dropped && passing) { // 넘기는 중: 늑대 멈춤
            val ph = t - phaseT
            if (ph > BB_PASS) { passing = false; phaseT = t; cur = (cur + 1) % P; vx = 0f; vy = 0f; lv = min(10, lv + 1)
                drift = Drift(Random.nextFloat() * BB_ANG, (if (Random.nextFloat() < 0.5f) -1f else 1f) * (0.5f + lv * 0.08f), 70f + lv * 22f)
                nextGust = el + (if (lv > 1) 0.8f else 1.5f); Beep.tone(660f, 0.08f, 3, 0.14f); vibe.tap() }
            else if (ph.toInt() != lastCount) { lastCount = ph.toInt(); Beep.tone(520f, 0.06f, 3, 0.1f) }
        }
        c.turnDots(W, P, cur, 30f)
        if (lv > 0) c.text("🔥 Lv." + (lv + 1), W / 2, 58f, 15f, if (lv > 4) "#FF5A6E" else "#FFB74D")

        // 접시 (기울기 따라 그림자·하이라이트)
        c.save(); c.translate(cx, cy)
        c.beginPath(); c.ellipse(tx * 12, ty * 12 + 14, pr * 1.04f, pr * 1.04f, 0f, 0f, V20_TAU); c.fillStyle = "rgba(0,0,0,.35)"; c.fill()
        // 웹은 두 원 그라데이션 (밝은 점 -tx,-ty 쪽) — 여기선 밝은 점을 중심으로 하고 반지름을 그만큼 늘려 비슷하게
        val hx = -tx * pr * 0.5f; val hy = -ty * pr * 0.5f
        c.beginPath(); c.arc(0f, 0f, pr, 0f, V20_TAU)
        c.radialFill(hx, hy, pr * 0.1f, pr * 1.05f + hypot(hx, hy), listOf(0f to "#FFFFFF", 1f to "#C9D3E0")); c.fill()
        c.lineWidth = 8f; c.strokeStyle = "#90A4AE"; c.stroke()
        c.beginPath(); c.arc(0f, 0f, pr * 0.78f, 0f, V20_TAU); c.lineWidth = 2f; c.strokeStyle = "rgba(144,164,174,.6)"; c.stroke()
        // 기울기 화살표
        val tl = hypot(tx, ty)
        if (tl > 0.08f) {
            c.save(); c.rotate(atan2(ty, tx)); c.globalAlpha = min(0.5f, tl); c.fillStyle = "#FF7043"
            c.beginPath(); c.moveTo(pr * 0.55f, 0f); c.lineTo(pr * 0.38f, -14f); c.lineTo(pr * 0.38f, 14f); c.closePath(); c.fill(); c.restore()
        }
        // 바람
        val gw = gust
        if (gw != null) {
            val u = (el - gw.t) / 0.9f
            c.save(); c.rotate(gw.a); c.globalAlpha = sin(u * PI.toFloat()).coerceIn(0f, 1f)
            for (i in -1..1) {
                c.beginPath(); val yy = i * 26f; val xx = -pr * 1.1f + u * pr * 0.6f
                c.moveTo(xx, yy); c.quadraticCurveTo(xx + 30, yy - 12, xx + 60, yy); c.lineWidth = 5f; c.strokeStyle = "#4FC3F7"; c.lineCap = "round"; c.stroke()
            }
            c.restore()
        }
        // 계속 미는 바람 (한 턴 지난 뒤)
        val dw = drift
        if (dw != null && !dropped) {
            c.save(); c.rotate(dw.a); c.globalAlpha = if (passing) 0.25f else 0.6f; c.fillStyle = "#4FC3F7"
            for (k in 0..1) { val xx = -pr - 22 + k * 14; c.beginPath(); c.moveTo(xx + 10, 0f); c.lineTo(xx, -9f); c.lineTo(xx, 9f); c.closePath(); c.fill() }
            c.restore()
        }
        // 늑대
        val wr = min(36f, pr * 0.3f)
        if (!dropped) {
            val d = hypot(x, y)
            c.yWolf(x, y, wr, min(1f, d / pr * 1.3f), if (d > pr * 0.7f) -1 else 0, t)
        } else {
            val u = min(1f, (t - dropT) / 0.8f)
            c.save(); c.translate(cos(dropA) * (pr + u * 40), sin(dropA) * (pr + u * 40)); c.rotate(u * 5); c.scale(1 - u * 0.6f, 1 - u * 0.6f); c.globalAlpha = 1 - u * 0.5f
            c.yWolf(0f, 0f, wr, 1f, -1, t); c.restore()
        }
        c.restore()

        // 안내
        val fs = min(20f, W / 19); val ph = t - phaseT
        if (dropped) {
            val v = t - dropT
            if (v > 0.6f) c.resultCard(W, H, v - 0.6f, s.shotWord, s.ybShotN((cur + 1).toString()), "#FF5A6E", H * 0.5f)
            if (v > 1.8f) c.text(s.playAgain, W / 2, H - 22, 15f, "#9A9AB0", false)
        } else if (passing) {
            val k = 1 + 0.15f * abs(sin(t * 8))
            val nx = (cur + 1) % P
            val left = max(1, kotlin.math.ceil(BB_PASS - ph).toInt())
            c.text(s.bbPass + " " + left, W / 2, H - 70, fs * 1.6f * k, "#FFD740")
            c.text("→ " + (nx + 1), W / 2, H - 36, fs, YB_COLS[nx])
            c.text("⏸️", W / 2, H * 0.52f - pr - 24, 26f, "#fff")
        } else {
            c.text(s.bbHold(cur + 1), W / 2, H - 70, fs, YB_COLS[cur])
            // 남은 시간 막대
            val bw = min(260f, W * 0.6f); val u = min(1f, ph / BB_HOLD)
            c.beginPath(); c.roundRect(W / 2 - bw / 2, H - 46, bw, 10f, 5f); c.fillStyle = "#2A2A3A"; c.fill()
            c.beginPath(); c.roundRect(W / 2 - bw / 2, H - 46, bw * (1 - u), 10f, 5f); c.fillStyle = YB_COLS[cur]; c.fill()
            if (useFinger) c.text(s.bbNoSensor, W / 2, H - 20, 11f, "#9A9AB0", false)
        }
    }
}
