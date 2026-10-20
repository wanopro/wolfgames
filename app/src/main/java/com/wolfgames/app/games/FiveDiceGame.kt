package com.wolfgames.app.games

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.ui.DieView
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.WG
import kotlinx.coroutines.delay
import kotlin.random.Random

private enum class FdPhase { Ready, Rolling, Result, Win }

/**
 * 1-3 5개 주사위
 * 각자 5개로 시작, 탭하면 모두 동시에 굴림.
 * 1 → 아웃, 6 → 시계방향 다음 사람에게. 주사위가 0개가 되면 승리.
 *
 * 자리 배치(폰을 테이블 가운데에 놓는 기준, 플레이어 번호 = 시계방향 순서)
 *  2명: 아래 P1, 위 P2(뒤집힘)
 *  3명: 아래 P1, 위왼쪽 P2, 위오른쪽 P3 (위쪽 둘은 뒤집힘)
 *  4명: 아래왼쪽 P1, 위왼쪽 P2, 위오른쪽 P3, 아래오른쪽 P4 (위쪽 둘은 뒤집힘)
 */
@Composable
fun FiveDiceGame(onBack: () -> Unit) {
    var players by rememberSaveable { mutableIntStateOf(2) }
    var started by rememberSaveable { mutableStateOf(false) }

    if (!started) {
        GameScaffold("5개 주사위", onBack) {
            SetupPanel(
                description = "각자 주사위 5개로 시작해요. 화면을 탭하면 모두의 주사위가 동시에 굴러가요.\n" +
                    "• 1이 나온 주사위는 아웃\n" +
                    "• 6이 나온 주사위는 시계방향 옆 사람에게\n" +
                    "주사위가 하나도 남지 않으면 승리!\n\n" +
                    "폰을 테이블 가운데에 두면 각자 자기 방향으로 보여요.",
                onStart = { started = true },
            ) {
                Stepper("참가 인원", players, 2..4) { players = it }
            }
        }
    } else {
        BackHandler { started = false }
        FiveDicePlay(players, onExit = { started = false })
    }
}

@Composable
private fun FiveDicePlay(n: Int, onExit: () -> Unit) {
    val counts = remember { mutableStateListOf(*Array(n) { 5 }) }
    var faces by remember { mutableStateOf(List(n) { emptyList<Int>() }) }
    var phase by remember { mutableStateOf(FdPhase.Ready) }
    var rollKey by remember { mutableIntStateOf(0) }
    var winners by remember { mutableStateOf(emptyList<Int>()) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(rollKey) {
        if (rollKey == 0) return@LaunchedEffect
        repeat(12) {
            faces = List(n) { i -> List(counts[i]) { Random.nextInt(1, 7) } }
            delay(60)
        }
        val final = List(n) { i -> List(counts[i]) { Random.nextInt(1, 7) } }
        faces = final
        phase = FdPhase.Result
        delay(2000)
        val ones = final.map { f -> f.count { it == 1 } }
        val sixes = final.map { f -> f.count { it == 6 } }
        for (i in 0 until n) {
            counts[i] = counts[i] - ones[i] - sixes[i] + sixes[(i - 1 + n) % n]
        }
        faces = List(n) { emptyList() }
        val w = (0 until n).filter { counts[it] == 0 }
        if (w.isNotEmpty()) {
            winners = w
            phase = FdPhase.Win
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } else {
            phase = FdPhase.Ready
        }
    }

    fun restart() {
        for (i in 0 until n) counts[i] = 5
        faces = List(n) { emptyList() }
        winners = emptyList()
        phase = FdPhase.Ready
    }

    val tapSource = remember { MutableInteractionSource() }
    Column(
        Modifier
            .fillMaxSize()
            .clickable(interactionSource = tapSource, indication = null) {
                if (phase == FdPhase.Ready) {
                    phase = FdPhase.Rolling
                    rollKey++
                }
            }
            .padding(4.dp),
    ) {
        when (n) {
            2 -> {
                PlayerArea(1, n, true, counts[1], faces[1], phase, 1 in winners, Modifier.weight(1f).fillMaxWidth())
                CenterBar(phase, winners, onRestart = { restart() }, onExit = onExit)
                PlayerArea(0, n, false, counts[0], faces[0], phase, 0 in winners, Modifier.weight(1f).fillMaxWidth())
            }

            3 -> {
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    PlayerArea(1, n, true, counts[1], faces[1], phase, 1 in winners, Modifier.weight(1f).fillMaxHeight())
                    PlayerArea(2, n, true, counts[2], faces[2], phase, 2 in winners, Modifier.weight(1f).fillMaxHeight())
                }
                CenterBar(phase, winners, onRestart = { restart() }, onExit = onExit)
                PlayerArea(0, n, false, counts[0], faces[0], phase, 0 in winners, Modifier.weight(1f).fillMaxWidth())
            }

            else -> {
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    PlayerArea(1, n, true, counts[1], faces[1], phase, 1 in winners, Modifier.weight(1f).fillMaxHeight())
                    PlayerArea(2, n, true, counts[2], faces[2], phase, 2 in winners, Modifier.weight(1f).fillMaxHeight())
                }
                CenterBar(phase, winners, onRestart = { restart() }, onExit = onExit)
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    PlayerArea(0, n, false, counts[0], faces[0], phase, 0 in winners, Modifier.weight(1f).fillMaxHeight())
                    PlayerArea(3, n, false, counts[3], faces[3], phase, 3 in winners, Modifier.weight(1f).fillMaxHeight())
                }
            }
        }
    }
}

@Composable
private fun CenterBar(phase: FdPhase, winners: List<Int>, onRestart: () -> Unit, onExit: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onExit) { Text("나가기", color = WG.SubText) }
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            val text = when (phase) {
                FdPhase.Ready -> "🎲 화면을 탭하면 모두 굴려요"
                FdPhase.Rolling -> "굴리는 중..."
                FdPhase.Result -> "1은 아웃 · 6은 시계방향으로!"
                FdPhase.Win -> "🏆 " + winners.joinToString(", ") { "P${it + 1}" } + " 승리!"
            }
            Text(
                text,
                color = WG.Accent,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        if (phase == FdPhase.Win) {
            TextButton(onClick = onRestart) { Text("다시", color = WG.Accent, fontWeight = FontWeight.Bold) }
        } else {
            Spacer(Modifier.width(64.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlayerArea(
    i: Int,
    n: Int,
    rotated: Boolean,
    count: Int,
    faces: List<Int>,
    phase: FdPhase,
    winner: Boolean,
    modifier: Modifier,
) {
    val color = WG.PlayerColors[i]
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier
            .padding(4.dp)
            .rotate(if (rotated) 180f else 0f)
            .clip(shape)
            .background(color.copy(alpha = if (winner) 0.35f else 0.12f))
            .border(2.dp, color.copy(alpha = 0.7f), shape)
            .padding(10.dp),
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("플레이어 ${i + 1}", color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                if (winner) "🏆 승리!" else "남은 주사위 ${count}개",
                color = if (winner) WG.Accent else WG.SubText,
                fontSize = if (winner) 22.sp else 14.sp,
                fontWeight = if (winner) FontWeight.Black else FontWeight.Normal,
            )
            Spacer(Modifier.weight(1f))
            val showing = phase == FdPhase.Rolling || phase == FdPhase.Result
            val values = if (showing) faces else List(count) { 0 }
            val dieSize = when {
                values.size <= 6 -> 44.dp
                values.size <= 10 -> 34.dp
                else -> 26.dp
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                values.forEach { v ->
                    val out = phase == FdPhase.Result && v == 1
                    val pass = phase == FdPhase.Result && v == 6
                    DieView(
                        value = v,
                        dieSize = dieSize,
                        alpha = if (out) 0.25f else 1f,
                        border = when {
                            pass -> WG.Accent
                            out -> WG.Win
                            else -> null
                        },
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            if (phase == FdPhase.Result) {
                val ones = faces.count { it == 1 }
                val sixes = faces.count { it == 6 }
                val next = (i + 1) % n + 1
                Text(
                    "아웃 ${ones}개 · P${next}에게 ${sixes}개",
                    color = WG.Text,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
