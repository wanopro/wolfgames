package com.wolfgames.app.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.DieView
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.ResultOverlay
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.WG
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val BOARD = 12

private enum class SbPhase { Roll, Rolling, Pick, NoMove, Win }

/**
 * 1-2 숫자 뒤집기 잭팟 (1~12)
 * 주사위 2개 → 두 눈과 합 중 하나를 뒤집음. 뒤집을 게 없으면 다음 사람.
 * 마지막 숫자를 뒤집은 사람이 승리.
 */
@Composable
fun ShutBoxGame(onBack: () -> Unit) {
    var players by rememberSaveable { mutableIntStateOf(2) }
    var started by rememberSaveable { mutableStateOf(false) }

    GameScaffold("숫자 뒤집기 잭팟", onBack) {
        if (!started) {
            SetupPanel(
                description = "주사위 2개를 던져 나온 두 숫자와 그 합 중 하나를 골라 뒤집어요.\n" +
                    "뒤집을 숫자가 없으면 다음 사람 차례.\n" +
                    "1~12를 모두 뒤집어서 마지막 숫자를 뒤집은 사람이 승리!",
                onStart = { started = true },
            ) {
                Stepper("참가 인원", players, 1..8) { players = it }
            }
        } else {
            ShutBoxPlay(players, onSetup = { started = false })
        }
    }
}

private fun options(a: Int, b: Int, flipped: List<Boolean>): Set<Int> =
    setOf(a, b, a + b).filter { it in 1..BOARD && !flipped[it - 1] }.toSet()

@Composable
private fun ShutBoxPlay(playerCount: Int, onSetup: () -> Unit) {
    val flipped = remember { mutableStateListOf(*Array(BOARD) { false }) }
    var turn by remember { mutableIntStateOf(0) }
    var dice by remember { mutableStateOf(listOf(1, 1)) }
    var phase by remember { mutableStateOf(SbPhase.Roll) }
    var rollKey by remember { mutableIntStateOf(0) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(rollKey) {
        if (rollKey == 0) return@LaunchedEffect
        repeat(10) {
            dice = listOf(Random.nextInt(1, 7), Random.nextInt(1, 7))
            delay(60)
        }
        val a = Random.nextInt(1, 7)
        val b = Random.nextInt(1, 7)
        dice = listOf(a, b)
        phase = if (options(a, b, flipped).isEmpty()) SbPhase.NoMove else SbPhase.Pick
    }

    fun nextTurn() {
        turn = (turn + 1) % playerCount
        phase = SbPhase.Roll
    }

    fun reset() {
        for (i in 0 until BOARD) flipped[i] = false
        turn = 0
        dice = listOf(1, 1)
        phase = SbPhase.Roll
    }

    val avail = if (phase == SbPhase.Pick) options(dice[0], dice[1], flipped) else emptySet()
    val pColor = WG.PlayerColors[turn % WG.PlayerColors.size]

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                if (playerCount == 1) "혼자 하기" else "플레이어 ${turn + 1} 차례",
                color = pColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Text("남은 숫자 ${flipped.count { !it }}개", color = WG.SubText, fontSize = 14.sp)

            for (row in 0 until 3) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (col in 0 until 4) {
                        val num = row * 4 + col + 1
                        NumberTile(
                            num = num,
                            isFlipped = flipped[num - 1],
                            available = num in avail,
                            modifier = Modifier.weight(1f),
                        ) {
                            flipped[num - 1] = true
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (flipped.all { it }) {
                                phase = SbPhase.Win
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            } else {
                                nextTurn()
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DieView(dice[0], 76.dp)
                DieView(dice[1], 76.dp)
                Text("= ${dice[0] + dice[1]}", color = WG.Text, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }

            val msg = when (phase) {
                SbPhase.Roll -> "주사위를 던지세요"
                SbPhase.Rolling -> "굴리는 중..."
                SbPhase.Pick -> "뒤집을 숫자를 고르세요: ${avail.sorted().joinToString(", ")}"
                SbPhase.NoMove -> "뒤집을 숫자가 없어요!"
                SbPhase.Win -> ""
            }
            Text(
                msg,
                color = if (phase == SbPhase.NoMove) WG.Win else WG.Text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            when (phase) {
                SbPhase.Roll -> BigButton("🎲 주사위 던지기", Modifier.fillMaxWidth()) {
                    phase = SbPhase.Rolling
                    rollKey++
                }
                SbPhase.NoMove -> BigButton("다음 사람 ▶", Modifier.fillMaxWidth()) { nextTurn() }
                else -> Spacer(Modifier.height(56.dp))
            }
        }

        if (phase == SbPhase.Win) {
            ResultOverlay(
                title = if (playerCount == 1) "🎉 잭팟!" else "🎉 플레이어 ${turn + 1} 승리!",
                subtitle = "마지막 숫자를 뒤집었어요",
                primary = "다시 하기",
                onPrimary = { reset() },
                secondary = "인원 변경",
                onSecondary = onSetup,
            )
        }
    }
}

@Composable
private fun NumberTile(
    num: Int,
    isFlipped: Boolean,
    available: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val bg = when {
        isFlipped -> Color(0xFF15151F)
        available -> WG.Accent
        else -> WG.Surface2
    }
    val fg = when {
        isFlipped -> Color(0xFF34344A)
        available -> Color.Black
        else -> WG.Text
    }
    Box(
        modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .clickable(enabled = available, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text("$num", color = fg, fontSize = 30.sp, fontWeight = FontWeight.Black)
    }
}
