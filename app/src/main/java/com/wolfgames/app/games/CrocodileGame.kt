package com.wolfgames.app.games

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.WG
import kotlin.random.Random

private const val TEETH = 20
private val CrocGreen = Color(0xFF2E9E4F)
private val CrocDark = Color(0xFF1F7A3A)
private val MouthRed = Color(0xFF7A1020)

/** 2. 악어이빨 — 20개 중 1개만 당첨 */
@Composable
fun CrocodileGame(onBack: () -> Unit) {
    var round by remember { mutableIntStateOf(0) }
    val trap = remember(round) { Random.nextInt(TEETH) }
    val pressed = remember(round) { mutableStateListOf(*Array(TEETH) { false }) }
    var bitten by remember(round) { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val gap by animateDpAsState(
        targetValue = if (bitten) 0.dp else 140.dp,
        animationSpec = tween(if (bitten) 140 else 500),
        label = "jaw",
    )

    val onPress: (Int) -> Unit = { i ->
        if (!bitten && !pressed[i]) {
            pressed[i] = true
            if (i == trap) {
                bitten = true
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            } else {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    GameScaffold("악어이빨", onBack) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                if (bitten) "꽉! 🐊 당첨!" else "이빨을 하나씩 눌러보세요",
                color = if (bitten) WG.Win else WG.Text,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            val count = pressed.count { it }
            Text(
                if (bitten) "${count}번째 이빨에서 물렸어요" else "남은 이빨 ${TEETH - count}개 · 당첨 1개",
                color = WG.SubText,
                fontSize = 15.sp,
            )
            Spacer(Modifier.weight(1f))

            // 악어 머리
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 90.dp, topEnd = 90.dp, bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(CrocGreen)
                    .padding(horizontal = 14.dp),
            ) {
                Box(Modifier.fillMaxWidth().height(90.dp)) {
                    Row(Modifier.align(Alignment.Center), horizontalArrangement = Arrangement.spacedBy(80.dp)) {
                        Eye(closed = bitten)
                        Eye(closed = bitten)
                    }
                }
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(MouthRed)) {
                    TeethRow(0 until 10, down = true, pressed = pressed, trap = trap, bitten = bitten, onPress = onPress)
                    Spacer(Modifier.fillMaxWidth().height(gap))
                    TeethRow(10 until TEETH, down = false, pressed = pressed, trap = trap, bitten = bitten, onPress = onPress)
                }
                Box(Modifier.fillMaxWidth().height(36.dp).padding(top = 10.dp)) {
                    Box(
                        Modifier
                            .align(Alignment.TopCenter)
                            .size(width = 120.dp, height = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(CrocDark),
                    )
                }
            }

            Spacer(Modifier.weight(1f))
            BigButton(if (bitten) "다시 하기" else "새로 섞기", Modifier.fillMaxWidth()) { round++ }
        }
    }
}

@Composable
private fun Eye(closed: Boolean) {
    Box(
        Modifier.size(38.dp).clip(CircleShape).background(Color(0xFFFFF59D)),
        contentAlignment = Alignment.Center,
    ) {
        if (closed) {
            Box(Modifier.size(width = 26.dp, height = 6.dp).clip(RoundedCornerShape(3.dp)).background(Color.Black))
        } else {
            Box(Modifier.size(width = 10.dp, height = 26.dp).clip(RoundedCornerShape(5.dp)).background(Color.Black))
        }
    }
}

@Composable
private fun TeethRow(
    range: IntRange,
    down: Boolean,
    pressed: List<Boolean>,
    trap: Int,
    bitten: Boolean,
    onPress: (Int) -> Unit,
) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        for (i in range) {
            Tooth(
                down = down,
                isPressed = pressed[i],
                isTrap = bitten && i == trap,
                enabled = !bitten && !pressed[i],
                onClick = { onPress(i) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun Tooth(
    down: Boolean,
    isPressed: Boolean,
    isTrap: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val color = when {
        isTrap -> WG.Win
        isPressed -> Color(0xFF6B6B6B)
        else -> Color(0xFFFFFBEA)
    }
    Canvas(modifier.height(48.dp).clickable(enabled = enabled, onClick = onClick)) {
        val w = size.width
        val h = size.height
        val inset = w * 0.08f
        val path = Path().apply {
            if (down) {
                moveTo(inset, 0f)
                lineTo(w - inset, 0f)
                lineTo(w / 2f, h * (if (isPressed) 0.45f else 1f))
            } else {
                moveTo(inset, h)
                lineTo(w - inset, h)
                lineTo(w / 2f, h * (if (isPressed) 0.55f else 0f))
            }
            close()
        }
        drawPath(path, color)
    }
}
