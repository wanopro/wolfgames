package com.wolfgames.app.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
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
import com.wolfgames.app.ui.runFor
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val LEVELS = 14

private class LadderData(val n: Int, val rungs: List<BooleanArray>, val prizes: List<Boolean>)

private fun makeLadder(n: Int, k: Int): LadderData {
    val rungs = List(LEVELS) { BooleanArray(n - 1) }
    for (l in 0 until LEVELS) {
        for (j in 0 until n - 1) {
            val leftBusy = j > 0 && rungs[l][j - 1]
            if (!leftBusy && Random.nextFloat() < 0.4f) rungs[l][j] = true
        }
    }
    // 모든 이웃 줄 사이에 가로선이 최소 1개는 있도록
    for (j in 0 until n - 1) {
        if ((0 until LEVELS).none { rungs[it][j] }) {
            val cand = (0 until LEVELS).filter { l ->
                (j == 0 || !rungs[l][j - 1]) && (j == n - 2 || !rungs[l][j + 1])
            }
            if (cand.isNotEmpty()) rungs[cand.random()][j] = true
        }
    }
    val prizes = (List(k) { true } + List(n - k) { false }).shuffled()
    return LadderData(n, rungs, prizes)
}

/** 경로 좌표(열, 높이 단계) 목록과 도착 열 */
private fun tracePath(d: LadderData, start: Int): Pair<List<Pair<Float, Float>>, Int> {
    var c = start
    val pts = mutableListOf(c.toFloat() to 0f)
    for (l in 0 until LEVELS) {
        val y = (l + 1).toFloat()
        if (c < d.n - 1 && d.rungs[l][c]) {
            pts.add(c.toFloat() to y); c += 1; pts.add(c.toFloat() to y)
        } else if (c > 0 && d.rungs[l][c - 1]) {
            pts.add(c.toFloat() to y); c -= 1; pts.add(c.toFloat() to y)
        }
    }
    pts.add(c.toFloat() to (LEVELS + 1).toFloat())
    return pts to c
}

private fun DrawScope.drawPolyline(points: List<Offset>, fraction: Float, color: Color, width: Float): Offset {
    if (points.size < 2) return points.firstOrNull() ?: Offset.Zero
    var tip = points.last()
    var total = 0f
    for (i in 1 until points.size) total += (points[i] - points[i - 1]).getDistance()
    var remain = total * fraction
    val path = Path().apply { moveTo(points[0].x, points[0].y) }
    for (i in 1 until points.size) {
        val seg = (points[i] - points[i - 1]).getDistance()
        if (remain >= seg) {
            path.lineTo(points[i].x, points[i].y)
            remain -= seg
        } else {
            val t = if (seg == 0f) 0f else remain / seg
            val p = points[i - 1] + (points[i] - points[i - 1]) * t
            path.lineTo(p.x, p.y)
            tip = p
            break
        }
    }
    drawPath(path, color, style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round))
    return tip
}

/** 3. 사다리게임 */
@Composable
fun LadderGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    var n by rememberSaveable { mutableIntStateOf(4) }
    var k by rememberSaveable { mutableIntStateOf(1) }
    var started by rememberSaveable { mutableStateOf(false) }

    GameScaffold(s.menuLadder, onBack) {
        if (!started) {
            SetupPanel(
                description = s.ladderDesc,
                onStart = { started = true },
            ) {
                Stepper(s.players, n, 2..10) {
                    n = it
                    if (k > n - 1) k = n - 1
                }
                Stepper(s.prizes, k, 1..(n - 1)) { k = it }
            }
        } else {
            LadderPlay(n, k, onSetup = { started = false })
        }
    }
}

@Composable
private fun LadderPlay(n: Int, k: Int, onSetup: () -> Unit) {
    val s = LocalStrings.current
    var seed by remember { mutableIntStateOf(0) }
    val data = remember(seed) { makeLadder(n, k) }
    val paths = remember(data) { (0 until n).map { tracePath(data, it) } }
    val done = remember(seed) { mutableStateListOf<Int>() }
    val animating = remember(seed) { mutableStateListOf<Int>() }
    var prog by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val haptic = LocalVibe.current

    /** 하나 또는 여러 줄을 동시에 5초 동안 내려보냄 */
    fun start(list: List<Int>) {
        val go = list.filter { it !in done }
        if (animating.isNotEmpty() || go.isEmpty()) return
        animating.addAll(go)
        haptic.tap()
        scope.launch {
            prog = 0f
            runFor(5000L) { prog = it }
            done.addAll(go)
            animating.clear()
            if (go.any { data.prizes[paths[it].second] }) haptic.strong() else haptic.tap()
        }
    }

    val small = n > 7
    Column(
        Modifier.fillMaxSize().padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(s.ladderHint, color = WG.SubText, fontSize = 14.sp)

        // 위쪽 번호
        Row(Modifier.fillMaxWidth()) {
            for (s in 0 until n) {
                val active = s in done || s in animating
                Box(
                    Modifier
                        .weight(1f)
                        .padding(3.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(WG.PlayerColors[s].copy(alpha = if (active) 1f else 0.35f))
                        .clickable { start(listOf(s)) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "${s + 1}",
                        color = Color.Black,
                        fontSize = if (small) 13.sp else 18.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
            }
        }

        // 사다리
        Canvas(Modifier.fillMaxWidth().weight(1f)) {
            val w = size.width
            val h = size.height
            val rail = Color(0xFF55556A)
            val sw = 4.dp.toPx()
            fun px(p: Pair<Float, Float>) = Offset((p.first + 0.5f) * w / n, p.second * h / (LEVELS + 1))
            for (c in 0 until n) {
                val x = (c + 0.5f) * w / n
                drawLine(rail, Offset(x, 0f), Offset(x, h), strokeWidth = sw)
            }
            for (l in 0 until LEVELS) {
                for (j in 0 until n - 1) {
                    if (data.rungs[l][j]) {
                        val y = (l + 1) * h / (LEVELS + 1)
                        drawLine(rail, Offset((j + 0.5f) * w / n, y), Offset((j + 1.5f) * w / n, y), strokeWidth = sw)
                    }
                }
            }
            val lw = 7.dp.toPx()
            for (s in done) drawPolyline(paths[s].first.map { px(it) }, 1f, WG.PlayerColors[s], lw)
            for (s in animating) {
                val tip = drawPolyline(paths[s].first.map { px(it) }, prog, WG.PlayerColors[s], lw)
                // 내려가는 공
                drawCircle(Color.White, radius = lw * 1.25f, center = tip)
                drawCircle(WG.PlayerColors[s], radius = lw * 0.95f, center = tip)
            }
        }

        // 아래 결과
        Row(Modifier.fillMaxWidth()) {
            for (e in 0 until n) {
                val owner = (0 until n).firstOrNull { paths[it].second == e && it in done }
                val revealed = owner != null
                val prize = data.prizes[e]
                Box(
                    Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .height(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                !revealed -> WG.Surface2
                                prize -> WG.Win
                                else -> WG.Surface
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            when {
                                !revealed -> "?"
                                prize -> s.win
                                else -> s.pass
                            },
                            color = if (revealed && !prize) WG.SubText else Color.White,
                            fontSize = if (small) 11.sp else 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        if (owner != null) {
                            Text(s.numberN(owner + 1), color = WG.PlayerColors[owner], fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        val winnersDone = done.filter { data.prizes[paths[it].second] }.sorted()
        Text(
            when {
                done.isEmpty() -> s.hiddenPrizes(k)
                winnersDone.isEmpty() -> s.noWinnerYet
                else -> s.winnersList(winnersDone.joinToString(", ") { s.numberN(it + 1) })
            },
            color = if (winnersDone.isEmpty()) WG.Text else WG.Win,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BigButton(
                s.showAll,
                Modifier.weight(1f),
                enabled = animating.isEmpty() && done.size < n,
            ) {
                start((0 until n).toList())
            }
            BigButton(
                s.newLadder,
                Modifier.weight(1f),
                enabled = animating.isEmpty(),
                color = WG.Surface2,
                contentColor = WG.Text,
            ) { seed++ }
        }
        VTextButton(onClick = onSetup) { Text(s.changeLadder, color = WG.SubText) }
    }
}
