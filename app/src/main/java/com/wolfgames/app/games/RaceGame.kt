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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.wolfgames.app.ui.Text
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
import kotlin.math.cos
import kotlin.random.Random
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import com.wolfgames.app.ui.ANIMALS
import com.wolfgames.app.ui.Ctx
import com.wolfgames.app.ui.TAU
import com.wolfgames.app.ui.animalFace
import com.wolfgames.app.ui.cuteWolf
import com.wolfgames.app.ui.text

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
 * 진행 곡선: 구간마다 속도를 다르게(질주·주춤) 줘서 순위가 자주 뒤바뀜.
 * 미리 계산한 누적 거리표(0→1)를 finishTime 동안 따라감 → 정한 시간에 정확히 도착.
 */
private class Runner(val finishTime: Float, private val table: FloatArray) {
    fun pos(t: Float): Float {
        val m = table.size - 1
        val u = (t / finishTime).coerceIn(0f, 1f) * m
        val j = minOf(m - 1, u.toInt())
        return table[j] + (table[j + 1] - table[j]) * (u - j)
    }
}

private fun makeRunners(n: Int, duration: Float): List<Runner> {
    val order = (0 until n).shuffled()
    val times = FloatArray(n)
    times[order[0]] = duration
    // 2등부터는 조금씩 늦게 (마지막은 최대 8% 늦게 → 끝까지 접전)
    val gaps = List(n - 1) { Random.nextFloat() * 0.6f + 0.4f }.sorted()
    val total = gaps.sum()
    var acc = 0f
    for (r in 1 until n) { acc += gaps[r - 1]; times[order[r]] = duration * (1f + 0.08f * acc / total) }
    val k = 20
    val l = k - 1
    val slots = (1..l).shuffled()
    // (슬롯 위치, 속도, 랜덤 폭): 질주 4번 · 주춤 4번이 늑대마다 다른 때에
    val cfg = listOf(Triple(0, 2.8f, 0.6f), Triple(4, 2.4f, 0.5f), Triple(8, 2.0f, 0.4f), Triple(12, 1.8f, 0.4f), Triple(2, 0.1f, 0.1f), Triple(6, 0.12f, 0.1f), Triple(10, 0.15f, 0.1f), Triple(14, 0.2f, 0.1f))
    return List(n) { i ->
        val v = FloatArray(k + 1) { 0.5f + Random.nextFloat() * 0.5f }
        v[0] = 0.6f + Random.nextFloat() * 0.6f
        for ((o, a, b) in cfg) v[slots[(i * cfg.size + o) % l]] = a + Random.nextFloat() * b
        val m = 160
        val tab = FloatArray(m + 1)
        var sum = 0f
        for (j in 1..m) {
            val u = (j - 0.5f) / m * k
            val kk = u.toInt()
            val e = (1f - cos(PI.toFloat() * (u - kk))) / 2f
            sum += v[kk] * (1 - e) + v[minOf(k, kk + 1)] * e
            tab[j] = sum
        }
        for (j in 0..m) tab[j] /= sum
        Runner(times[i], tab)
    }
}

/** 동물 얼굴 하나 (고르기 화면용) */
@Composable
private fun AnimalIcon(kind: String, sizeDp: Int) {
    val d = LocalDensity.current.density
    Canvas(Modifier.size(sizeDp.dp)) {
        drawIntoCanvas { c -> val x = Ctx(c.nativeCanvas, d); val w = size.width / d; x.animalFace(w / 2, w / 2 + w * 0.08f, w * 0.33f, kind, 0); x.finish() }
    }
}

/** 5. 늑대달리기: 인원수 → 각자 동물 고르기 → 동물은 결승선에서 대기, 늑대가 먼저 무는 동물이 1등 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RaceGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    val names = s.animalNames.split(',')
    var n by rememberSaveable { mutableIntStateOf(4) }
    var secs by rememberSaveable { mutableIntStateOf(8) }
    var started by rememberSaveable { mutableStateOf(false) }
    val picks = remember { mutableStateListOf(0, 1, 3, 10) }
    fun fixPicks() { while (picks.size > n) picks.removeAt(picks.size - 1); var k = 0; while (picks.size < n && k < ANIMALS.size) { if (k !in picks) picks.add(k); k++ } }

    GameScaffold(s.menuRace, onBack) {
        if (!started) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(s.raceDesc, color = WG.SubText, fontSize = 15.sp, lineHeight = 22.sp)
                Stepper(s.players, n, 2..9) { n = it; fixPicks() }
                Stepper(s.raceSeconds, secs, 3..30) { secs = it }
                Text(s.racePick(n), color = WG.Text, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                // 동물 12종 (6개씩 2줄)
                for (row in 0 until 2) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (col in 0 until 6) {
                            val i = row * 6 + col
                            val j = picks.indexOf(i)
                            Box(Modifier.weight(1f)) {
                                Column(
                                    Modifier.fillMaxWidth().heightIn(min = 72.dp).clip(RoundedCornerShape(14.dp))
                                        .background(if (j >= 0) Color(0xFF2C2A1E) else WG.Surface)
                                        .border(2.dp, if (j >= 0) WG.Accent else Color.Transparent, RoundedCornerShape(14.dp))
                                        .clickable { vibe.tap(); if (j >= 0) picks.removeAt(j) else if (picks.size < n) picks.add(i) },
                                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
                                ) {
                                    AnimalIcon(ANIMALS[i], 44)
                                    Text(names[i], color = WG.SubText, fontSize = 10.sp, maxLines = 1)
                                }
                                if (j >= 0) Box(
                                    Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp).size(22.dp).clip(CircleShape).background(WG.Accent),
                                    contentAlignment = Alignment.Center,
                                ) { Text("${j + 1}", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Black) }
                            }
                        }
                    }
                }
                FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    picks.forEachIndexed { i, a ->
                        Column(Modifier.clip(RoundedCornerShape(12.dp)).background(WG.Surface).padding(horizontal = 10.dp, vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            AnimalIcon(ANIMALS[a], 30)
                            Text(s.raceLane(i + 1, names[a]), color = WG.SubText, fontSize = 11.sp)
                        }
                    }
                }
                BigButton(if (picks.size == n) s.start else s.raceNeed(n), Modifier.fillMaxWidth(), enabled = picks.size == n) { started = true }
            }
        } else {
            RacePlay(n, secs, picks.toList(), onSetup = { started = false })
        }
    }
}

private const val BITE = 0.7f

@Composable
private fun RacePlay(n: Int, secs: Int, picks: List<Int>, onSetup: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    val names = s.animalNames.split(',')
    val density = LocalDensity.current.density
    var round by remember { mutableIntStateOf(0) }
    val runners = remember(round) { makeRunners(n, secs.toFloat()) }
    var phase by remember(round) { mutableStateOf(RacePhase.Ready) }
    var count by remember(round) { mutableIntStateOf(3) }
    var t by remember(round) { mutableFloatStateOf(0f) }
    val finished = remember(round) { mutableStateListOf<Int>() }
    var go by remember(round) { mutableIntStateOf(0) }
    var idle by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) { val st = withFrameNanos { it }; while (true) { val now = withFrameNanos { it }; idle = (now - st) / 1e9f } }
    LaunchedEffect(round, go) {
        if (go == 0) return@LaunchedEffect
        for (c in 3 downTo 1) { count = c; vibe.tick(); delay(650) }
        count = 0; vibe.strong(); phase = RacePhase.Running
        val start = withFrameNanos { it }
        val endAll = runners.maxOf { it.finishTime } + BITE
        while (true) {
            val now = withFrameNanos { it }
            t = (now - start) / 1_000_000_000f
            for (i in runners.indices) if (i !in finished && t >= runners[i].finishTime) { finished.add(i); if (finished.size == 1) vibe.strong() else vibe.tap() }
            if (t >= endAll) break
        }
        phase = RacePhase.Done
    }

    Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val title = when (phase) {
            RacePhase.Ready -> s.raceTapStart
            RacePhase.Countdown -> if (count > 0) "$count" else s.raceGo
            RacePhase.Running -> if (finished.isEmpty()) s.raceRunning + "  ${max(0, secs - t.toInt())}" else s.raceRunning
            RacePhase.Done -> s.raceResultNames(names[picks[finished.first()]], names[picks[finished.last()]])
        }
        Text(title, color = if (phase == RacePhase.Done) WG.Accent else WG.Text, fontSize = if (phase == RacePhase.Countdown) 40.sp else 21.sp,
            fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.heightIn(min = 54.dp))
        Canvas(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(16.dp))) {
            idle.let { }
            drawIntoCanvas { cc ->
                val ctx = Ctx(cc.nativeCanvas, density)
                val w = size.width / density; val hh = size.height / density
                val lh = hh / n; val finish = w - 66; val ws = min(lh * 0.42f, 70f); val ar = min(lh * 0.14f, ws * 0.34f); val ax = w - 34
                for (i in 0 until n) {
                    ctx.fillStyle = if (i % 2 == 0) "#F1E7D0" else "#E4D6B8"; ctx.fillRect(0f, i * lh, w, lh)
                    ctx.fillStyle = "rgba(0,0,0,.07)"; ctx.fillRect(0f, (i + 1) * lh - 6, w, 6f)
                    ctx.beginPath(); ctx.arc(18f, i * lh + lh / 2, min(lh * 0.28f, 12f), 0f, TAU); ctx.fillStyle = "#5B616B"; ctx.fill()
                    ctx.text("${i + 1}", 18f, i * lh + lh / 2 + 1, 12f, "#fff")
                }
                ctx.fillStyle = "#fff"; ctx.fillRect(34f, 0f, 3f, hh)
                var y = 0f; var r = 0
                while (y < hh) { for (c in 0 until 2) { ctx.fillStyle = if ((r + c) % 2 == 1) "#fff" else "#2B2B33"; ctx.fillRect(finish + c * 8, y, 8f, min(8f, hh - y)) }; y += 8; r++ }
                val running = phase == RacePhase.Running || phase == RacePhase.Done
                for (i in 0 until n) {
                    val gy = (i + 0.84f) * lh; val ay = gy - ws * 0.5f; val rt = runners[i].finishTime; val bt = if (running) t - rt else -1f
                    // 동물
                    ctx.save(); ctx.translate(ax, ay)
                    if (bt < 0) {
                        val near = running && t > rt - 1.4f
                        ctx.rotate(sin((if (running) t else idle) * (if (near) 26f else 12f) + i) * (if (near) 0.12f else 0.05f))
                        ctx.animalFace(0f, 0f, ar, ANIMALS[picks[i]], if (near) 1 else 0)
                        if (near) ctx.text("💦", ar * 0.9f, -ar * 0.9f, ar * 0.7f, "#fff")
                    } else {
                        val hit = bt > 0.22f
                        if (hit) { ctx.translate(sin(bt * 40) * 2 * max(0f, 1 - bt), 0f); ctx.rotate(0.3f); ctx.scale(1.12f, 0.84f) }
                        ctx.animalFace(0f, 0f, ar, ANIMALS[picks[i]], if (hit) 2 else 1)
                        if (bt > 0.3f) { ctx.rotate(-0.3f); ctx.text("💫", 0f, -ar * 1.25f, ar * 0.65f, "#fff") }
                    }
                    ctx.restore()
                    // 늑대
                    val p = if (running) runners[i].pos(t) else 0f
                    val startX = 40 + ws * 0.9f; val endX = finish - 4 - ws * 0.84f
                    var wx = startX + p * (endX - startX); var open = 0f; var run = if (phase == RacePhase.Running && bt < 0) 1f else 0f; var ph = t * 16 + i * 1.7f
                    if (run > 0 && t > rt - 1.4f) { val k = min(1f, (t - (rt - 1.4f)) / 0.3f); open = k * (0.55f + 0.25f * abs(sin(t * 9 + i))) }
                    if (bt >= 0) {
                        val u = min(1f, bt / BITE); val reach = (ax - ar * 0.6f) - (endX + 0.84f * ws); val e = if (u < 0.4f) 1 - (1 - u / 0.4f) * (1 - u / 0.4f) else 1f
                        wx = endX + reach * e; run = if (u < 0.4f) 0.6f else 0f; ph = 0.8f + i; open = if (u < 0.3f) u / 0.3f else if (u < 0.45f) 1 - (u - 0.3f) / 0.15f else 0f
                    }
                    if (run > 0.5f && t > 0) {
                        val sp = (runners[i].pos(t + 0.06f) - p) / 0.06f * rt
                        if (sp > 1.25f) {
                            val k = min(1f, (sp - 1.25f) / 1.2f)
                            for (d in 0 until 3) { val q = (t * 3 + d / 3f + i * 0.3f) % 1f; ctx.globalAlpha = (1 - q) * 0.55f * k; ctx.beginPath(); ctx.arc(wx - ws * (0.5f + q * 0.9f), gy - ws * 0.06f - q * ws * 0.12f, ws * (0.07f + q * 0.12f), 0f, TAU); ctx.fillStyle = "#B8A27E"; ctx.fill() }
                            ctx.globalAlpha = 0.5f * k; ctx.strokeStyle = "#fff"; ctx.lineWidth = 2f
                            for (d in 0 until 3) { val yy = gy - ws * (0.45f + d * 0.18f); ctx.beginPath(); ctx.moveTo(wx - ws * (0.95f + d * 0.1f), yy); ctx.lineTo(wx - ws * (1.35f + d * 0.15f), yy); ctx.stroke() }
                            ctx.globalAlpha = 1f
                        }
                    }
                    ctx.cuteWolf(wx, gy, ws, ph, run, open, happy = bt > BITE * 0.6f)
                    if (bt >= 0.25f && bt < 0.9f) { ctx.text(s.biteWord, ax - 6, ay - ar * 1.6f - (bt - 0.25f) * 20, 20f, "#E53950"); ctx.text("💥", ax - ar * 0.9f, ay + ar * 0.4f, 18f, "#fff") }
                    val rank = finished.indexOf(i)
                    if (rank >= 0 && bt >= BITE * 0.6f) {
                        ctx.beginPath(); ctx.arc(ax + 2, ay + ar * 1.45f, min(lh * 0.2f, 12f), 0f, TAU); ctx.fillStyle = if (rank == 0) "#FFC107" else if (rank == n - 1) "#FF3D5A" else "#555566"; ctx.fill()
                        ctx.text("${rank + 1}", ax + 2, ay + ar * 1.45f + 1, 12f, if (rank == 0) "#2B2B33" else "#fff")
                    }
                }
                ctx.finish()
            }
        }
        Box(Modifier.height(10.dp))
        when (phase) {
            RacePhase.Ready -> BigButton(s.raceStart, Modifier.fillMaxWidth()) { phase = RacePhase.Countdown; go++ }
            RacePhase.Done -> BigButton(s.raceAgain, Modifier.fillMaxWidth()) { round++ }
            else -> Box(Modifier.height(56.dp))
        }
        VTextButton(onClick = onSetup, enabled = phase == RacePhase.Ready || phase == RacePhase.Done) { Text(s.changeRace, color = WG.SubText) }
    }
}
