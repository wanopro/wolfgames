package com.wolfgames.app.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.VTextButton
import com.wolfgames.app.ui.WG
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/** 늑대 색 — 빨 주 노 초 파 검 보 회 분홍 */
val RaceColors = listOf(
    Color(0xFFE53935), // 빨강
    Color(0xFFFF8F00), // 주황
    Color(0xFFFDD835), // 노랑
    Color(0xFF43A047), // 초록
    Color(0xFF1E88E5), // 파랑
    Color(0xFF212121), // 검정
    Color(0xFF8E24AA), // 보라
    Color(0xFF9E9E9E), // 회색
    Color(0xFFFF6FB0), // 분홍
)

private val SandA = Color(0xFFF1E7D0)
private val SandB = Color(0xFFE4D6B8)
private val Belly = Color(0xFFFFF6E8)
private val Ink = Color(0xFF2B2B33)
private val Outline = Color(0x66000000)

private enum class RacePhase { Ready, Countdown, Running, Done }

/**
 * 한 마리의 진행 곡선. 0~1 사이 u(=시간/완주시간)에 대해 0~1 위치를 돌려줌.
 * 사인파를 섞어 중간에 앞서거니 뒤서거니 하지만 항상 앞으로만 달림.
 */
private class Runner(val finishTime: Float, private val a1: Float, private val k1: Int, private val ph1: Float,
                     private val a2: Float, private val k2: Int, private val ph2: Float) {
    fun pos(t: Float): Float {
        val u = (t / finishTime).coerceIn(0f, 1f)
        val w1 = 2f * PI.toFloat() * k1
        val w2 = 2f * PI.toFloat() * k2
        // 양 끝(0,1)에서 0이 되도록 sin(w u) 형태 + 미분이 1-|a1|-|a2| > 0 이 되도록 진폭 제한
        val p = u + a1 * (sin(w1 * u + ph1) - sin(ph1) * (1 - u) - sin(w1 + ph1) * u) / w1 +
            a2 * (sin(w2 * u + ph2) - sin(ph2) * (1 - u) - sin(w2 + ph2) * u) / w2
        return p.coerceIn(0f, 1f)
    }
}

/** 결과 시간 duration 초에 1등이 정확히 들어오도록 각자 완주 시간을 정함 */
private fun makeRunners(n: Int, duration: Float): List<Runner> {
    val order = (0 until n).shuffled()
    val times = FloatArray(n)
    times[order[0]] = duration
    // 2등부터는 조금씩 늦게 (마지막은 최대 22% 늦게)
    val gaps = List(n - 1) { Random.nextFloat() * 0.6f + 0.4f }.sorted()
    val total = gaps.sum()
    var acc = 0f
    for (r in 1 until n) {
        acc += gaps[r - 1]
        times[order[r]] = duration * (1f + 0.22f * acc / total)
    }
    return List(n) {
        Runner(
            finishTime = times[it],
            a1 = Random.nextFloat() * 0.45f + 0.15f, k1 = Random.nextInt(1, 3), ph1 = Random.nextFloat() * 6.28f,
            a2 = Random.nextFloat() * 0.25f, k2 = Random.nextInt(2, 5), ph2 = Random.nextFloat() * 6.28f,
        )
    }
}

/** 5. 동물달리기 */
@Composable
fun RaceGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    var n by rememberSaveable { mutableIntStateOf(4) }
    var secs by rememberSaveable { mutableIntStateOf(8) }
    var started by rememberSaveable { mutableStateOf(false) }

    GameScaffold(s.menuRace, onBack) {
        if (!started) {
            SetupPanel(description = s.raceDesc, onStart = { started = true }) {
                Stepper(s.players, n, 2..9) { n = it }
                Stepper(s.raceSeconds, secs, 3..30) { secs = it }
                // 색 미리보기
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(WG.Surface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    for (i in 0 until n) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Canvas(Modifier.size(34.dp)) {
                                drawWolf(center = Offset(size.width * 0.42f, size.height * 0.6f), s = size.height * 0.62f, fur = RaceColors[i], phase = 0f)
                            }
                            Text("${i + 1}", color = WG.Text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            RacePlay(n, secs, onSetup = { started = false })
        }
    }
}

@Composable
private fun RacePlay(n: Int, secs: Int, onSetup: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    var round by remember { mutableIntStateOf(0) }
    val runners = remember(round) { makeRunners(n, secs.toFloat()) }
    var phase by remember(round) { mutableStateOf(RacePhase.Ready) }
    var count by remember(round) { mutableIntStateOf(3) }
    var t by remember(round) { mutableFloatStateOf(0f) }
    val finished = remember(round) { mutableStateListOf<Int>() }
    var go by remember(round) { mutableIntStateOf(0) }
    val tm = rememberTextMeasurer()

    // 카운트다운 → 달리기
    LaunchedEffect(round, go) {
        if (go == 0) return@LaunchedEffect
        for (c in 3 downTo 1) {
            count = c
            vibe.tick()
            delay(650)
        }
        count = 0
        vibe.strong()
        phase = RacePhase.Running
        val start = withFrameNanos { it }
        val endAll = runners.maxOf { it.finishTime }
        while (true) {
            val now = withFrameNanos { it }
            t = (now - start) / 1_000_000_000f
            for (i in runners.indices) {
                if (i !in finished && t >= runners[i].finishTime) {
                    finished.add(i)
                    if (finished.size == 1) vibe.strong() else vibe.tap()
                }
            }
            if (t >= endAll) break
        }
        phase = RacePhase.Done
    }

    Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val title = when (phase) {
            RacePhase.Ready -> s.raceTapStart
            RacePhase.Countdown -> if (count > 0) "$count" else s.raceGo
            RacePhase.Running -> if (finished.isEmpty()) s.raceRunning + "  ${max(0, secs - t.toInt())}" else s.raceRunning
            RacePhase.Done -> s.raceResult(finished.first() + 1, finished.last() + 1)
        }
        Text(
            title,
            color = if (phase == RacePhase.Done) WG.Accent else WG.Text,
            fontSize = if (phase == RacePhase.Countdown) 40.sp else 22.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.height(54.dp),
        )

        // 트랙
        Canvas(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
        ) {
            val laneH = size.height / n
            val startX = 44.dp.toPx()
            val finishX = size.width - 20.dp.toPx()
            val wolfS = min(laneH * 0.62f, 58.dp.toPx())
            for (i in 0 until n) {
                val top = i * laneH
                drawRect(if (i % 2 == 0) SandA else SandB, topLeft = Offset(0f, top), size = Size(size.width, laneH))
                // 번호
                drawCircle(RaceColors[i], radius = min(laneH * 0.3f, 15.dp.toPx()), center = Offset(22.dp.toPx(), top + laneH / 2))
                val lay = tm.measure(
                    "${i + 1}",
                    TextStyle(color = if (i == 2 || i == 7) Ink else Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black),
                )
                drawText(lay, topLeft = Offset(22.dp.toPx() - lay.size.width / 2f, top + laneH / 2 - lay.size.height / 2f))
            }
            // 출발선
            drawLine(Color.White, Offset(startX, 0f), Offset(startX, size.height), strokeWidth = 3.dp.toPx())
            // 결승선 (체크무늬)
            val cell = 8.dp.toPx()
            var y = 0f
            var row = 0
            while (y < size.height) {
                for (c in 0 until 2) {
                    val dark = (row + c) % 2 == 0
                    drawRect(
                        if (dark) Ink else Color.White,
                        topLeft = Offset(finishX + c * cell, y),
                        size = Size(cell, min(cell, size.height - y)),
                    )
                }
                y += cell
                row++
            }
            // 늑대
            val running = phase == RacePhase.Running || phase == RacePhase.Done
            for (i in 0 until n) {
                val p = if (running) runners[i].pos(t) else 0f
                val cy = (i + 0.5f) * laneH
                val run = finishX - startX - wolfS * 1.25f
                val cx = startX + wolfS * 0.55f + p * run
                val moving = phase == RacePhase.Running && i !in finished
                val legPhase = if (moving) t * 16f + i else 0f
                val bob = if (moving) abs(sin(legPhase)) * wolfS * 0.06f else 0f
                drawWolf(center = Offset(cx, cy + wolfS * 0.12f - bob), s = wolfS, fur = RaceColors[i], phase = legPhase)
                // 순위 표시
                val rank = finished.indexOf(i)
                if (rank >= 0) {
                    val bx = cx - wolfS * 0.9f
                    val r = min(laneH * 0.22f, 13.dp.toPx())
                    val badge = when (rank) {
                        0 -> Color(0xFFFFC107)
                        n - 1 -> WG.Win
                        else -> Color(0xFF555566)
                    }
                    drawCircle(badge, radius = r, center = Offset(bx, cy))
                    val lay = tm.measure("${rank + 1}", TextStyle(color = if (rank == 0) Ink else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black))
                    drawText(lay, topLeft = Offset(bx - lay.size.width / 2f, cy - lay.size.height / 2f))
                }
            }
        }

        Box(Modifier.height(10.dp))
        when (phase) {
            RacePhase.Ready -> BigButton(s.raceStart, Modifier.fillMaxWidth()) {
                phase = RacePhase.Countdown
                go++
            }
            RacePhase.Done -> BigButton(s.raceAgain, Modifier.fillMaxWidth()) { round++ }
            else -> Box(Modifier.height(56.dp))
        }
        VTextButton(onClick = onSetup, enabled = phase == RacePhase.Ready || phase == RacePhase.Done) {
            Text(s.changeRace, color = WG.SubText)
        }
    }
}

/**
 * 귀여운 옆모습 늑대 (오른쪽을 봄)
 * center: 몸통 중심, s: 크기 기준(몸통 길이), phase: 다리 흔들림
 */
fun DrawScope.drawWolf(center: Offset, s: Float, fur: Color, phase: Float) {
    val sw = max(1.5f, s * 0.035f)
    val stroke = Stroke(width = sw)
    val swing = sin(phase) * 28f
    translate(center.x, center.y) {
        // 꼬리 (흔들흔들)
        rotate(degrees = -25f + sin(phase * 0.5f) * 15f, pivot = Offset(-s * 0.42f, -s * 0.05f)) {
            val tl = Offset(-s * 0.85f, -s * 0.2f)
            val ts = Size(s * 0.5f, s * 0.22f)
            drawOval(fur, topLeft = tl, size = ts)
            drawOval(Outline, topLeft = tl, size = ts, style = stroke)
            drawOval(Belly, topLeft = Offset(-s * 0.86f, -s * 0.16f), size = Size(s * 0.16f, s * 0.13f))
        }
        // 다리 (뒤쪽 한 쌍은 어둡게)
        fun leg(x: Float, ang: Float, dark: Boolean) {
            rotate(degrees = ang, pivot = Offset(x, s * 0.1f)) {
                drawLine(
                    if (dark) fur.copy(alpha = 0.75f) else fur,
                    Offset(x, s * 0.1f), Offset(x, s * 0.42f),
                    strokeWidth = s * 0.13f, cap = StrokeCap.Round,
                )
                drawCircle(Belly, radius = s * 0.065f, center = Offset(x, s * 0.43f))
            }
        }
        leg(-s * 0.28f, -swing, true)
        leg(s * 0.22f, swing, true)
        leg(-s * 0.2f, swing, false)
        leg(s * 0.3f, -swing, false)
        // 몸통
        val bt = Offset(-s * 0.5f, -s * 0.24f)
        val bs = Size(s * 1.0f, s * 0.48f)
        drawOval(fur, topLeft = bt, size = bs)
        drawOval(Outline, topLeft = bt, size = bs, style = stroke)
        drawOval(Belly, topLeft = Offset(-s * 0.3f, s * 0.02f), size = Size(s * 0.6f, s * 0.2f))
        // 머리
        val hc = Offset(s * 0.52f, -s * 0.3f)
        val hr = s * 0.27f
        // 귀
        for (ex in listOf(hc.x - hr * 0.55f, hc.x + hr * 0.25f)) {
            val ear = Path().apply {
                moveTo(ex - hr * 0.32f, hc.y - hr * 0.55f)
                lineTo(ex + hr * 0.02f, hc.y - hr * 1.45f)
                lineTo(ex + hr * 0.4f, hc.y - hr * 0.55f)
                close()
            }
            drawPath(ear, fur)
            drawPath(ear, Outline, style = stroke)
            val inner = Path().apply {
                moveTo(ex - hr * 0.14f, hc.y - hr * 0.65f)
                lineTo(ex + hr * 0.03f, hc.y - hr * 1.15f)
                lineTo(ex + hr * 0.22f, hc.y - hr * 0.65f)
                close()
            }
            drawPath(inner, Color(0xFFF6AFC0))
        }
        drawCircle(fur, radius = hr, center = hc)
        drawCircle(Outline, radius = hr, center = hc, style = stroke)
        // 주둥이
        val mt = Offset(hc.x + hr * 0.25f, hc.y - hr * 0.05f)
        val ms = Size(hr * 1.15f, hr * 0.75f)
        drawOval(Belly, topLeft = mt, size = ms)
        drawOval(Outline, topLeft = mt, size = ms, style = stroke)
        drawCircle(Ink, radius = hr * 0.17f, center = Offset(mt.x + ms.width * 0.92f, mt.y + ms.height * 0.3f))
        // 눈 (흰자 + 검은 눈동자 + 반짝)
        val eye = Offset(hc.x + hr * 0.15f, hc.y - hr * 0.25f)
        drawCircle(Color.White, radius = hr * 0.27f, center = eye)
        drawCircle(Ink, radius = hr * 0.19f, center = Offset(eye.x + hr * 0.05f, eye.y))
        drawCircle(Color.White, radius = hr * 0.07f, center = Offset(eye.x + hr * 0.0f, eye.y - hr * 0.07f))
        // 볼터치
        drawCircle(Color(0x88FF8FA3), radius = hr * 0.17f, center = Offset(hc.x + hr * 0.05f, hc.y + hr * 0.35f))
    }
}
