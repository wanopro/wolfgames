package com.wolfgames.app.games

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.wolfgames.app.ui.LocalVibe
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.VTextButton
import com.wolfgames.app.ui.WG

private const val TEETH = 20
private val Fur = Color(0xFF9AA5B4)
private val FurDark = Color(0xFF6E7A8A)
private val Cream = Color(0xFFF4ECE1)
private val EarPink = Color(0xFFF6AFC0)
private val Blush = Color(0x66FF8FA3)
private val Mouth = Color(0xFF8E2440)
private val Tongue = Color(0xFFE86A86)

/** 2. 늑대이빨 — 20개 이빨 중 당첨 개수 지정 */
@Composable
fun WolfTeethGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    var prizes by rememberSaveable { mutableIntStateOf(1) }
    var started by rememberSaveable { mutableStateOf(false) }

    GameScaffold(s.menuCroc, onBack) {
        if (!started) {
            SetupPanel(description = s.teethDesc, onStart = { started = true }) {
                Stepper(s.prizes, prizes, 1..10) { prizes = it }
            }
        } else {
            WolfTeethPlay(prizes, onSetup = { started = false })
        }
    }
}

@Composable
private fun WolfTeethPlay(k: Int, onSetup: () -> Unit) {
    val s = LocalStrings.current
    var round by remember { mutableIntStateOf(0) }
    val traps = remember(round) { (0 until TEETH).shuffled().take(k).toSet() }
    val pressed = remember(round) { mutableStateListOf(*Array(TEETH) { false }) }
    var bitten by remember(round) { mutableStateOf(false) }
    var found by remember(round) { mutableIntStateOf(0) }
    val haptic = LocalVibe.current
    val gap by animateDpAsState(
        targetValue = if (bitten) 0.dp else 130.dp,
        animationSpec = tween(if (bitten) 140 else 500),
        label = "jaw",
    )
    val allFound = found >= k

    val onPress: (Int) -> Unit = { i ->
        if (!bitten && !pressed[i]) {
            pressed[i] = true
            if (i in traps) {
                found++
                bitten = true
                haptic.strong()
            } else {
                haptic.tap()
            }
        }
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            when {
                bitten && allFound -> s.allFound
                bitten -> s.bitten
                else -> s.pressTeeth
            },
            color = if (bitten) WG.Win else WG.Text,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
        )
        val count = pressed.count { it }
        Text(
            if (bitten) s.bittenAt(count) else s.teethLeft(TEETH - count, k - found),
            color = WG.SubText,
            fontSize = 15.sp,
        )
        Spacer(Modifier.weight(1f))

        // 늑대 얼굴
        Box(Modifier.fillMaxWidth()) {
            // 귀 (얼굴 뒤에)
            Canvas(Modifier.fillMaxWidth().height(80.dp)) { drawEars() }
            Column(
                Modifier
                    .padding(top = 44.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 110.dp, topEnd = 110.dp, bottomStart = 70.dp, bottomEnd = 70.dp))
                    .background(Fur)
                    .padding(horizontal = 16.dp),
            ) {
                Canvas(Modifier.fillMaxWidth().height(120.dp)) { drawUpperFace(bitten) }
                // 입
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Mouth),
                ) {
                    TeethRow(0 until 10, down = true, pressed = pressed, traps = traps, onPress = onPress, enabled = !bitten)
                    Box(Modifier.fillMaxWidth().height(gap), contentAlignment = Alignment.BottomCenter) {
                        if (gap > 30.dp) {
                            Canvas(Modifier.fillMaxWidth(0.5f).height(gap * 0.45f)) {
                                drawOval(Tongue, topLeft = Offset(0f, 0f), size = Size(size.width, size.height * 2f))
                            }
                        }
                    }
                    TeethRow(10 until TEETH, down = false, pressed = pressed, traps = traps, onPress = onPress, enabled = !bitten)
                }
                // 턱
                Canvas(Modifier.fillMaxWidth().height(40.dp)) {
                    drawOval(
                        Cream,
                        topLeft = Offset(size.width * 0.2f, -size.height * 0.4f),
                        size = Size(size.width * 0.6f, size.height * 1.1f),
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))
        when {
            bitten && !allFound -> BigButton(s.continueN(k - found), Modifier.fillMaxWidth()) { bitten = false }
            bitten -> BigButton(s.playAgain, Modifier.fillMaxWidth()) { round++ }
            else -> BigButton(s.shuffle, Modifier.fillMaxWidth(), color = WG.Surface2, contentColor = WG.Text) { round++ }
        }
        VTextButton(onClick = onSetup) { Text(s.changePrizes, color = WG.SubText) }
    }
}

/** 뾰족한 귀 두 개 */
private fun DrawScope.drawEars() {
    val w = size.width
    val h = size.height
    for (cx in listOf(w * 0.24f, w * 0.76f)) {
        val half = w * 0.15f
        val outer = Path().apply {
            moveTo(cx - half, h)
            lineTo(cx + (if (cx < w / 2) -half * 0.15f else half * 0.15f), 0f)
            lineTo(cx + half, h)
            close()
        }
        drawPath(outer, FurDark)
        val ih = half * 0.55f
        val inner = Path().apply {
            moveTo(cx - ih, h)
            lineTo(cx + (if (cx < w / 2) -ih * 0.15f else ih * 0.15f), h * 0.3f)
            lineTo(cx + ih, h)
            close()
        }
        drawPath(inner, EarPink)
    }
}

/** 눈, 볼터치, 주둥이, 코 */
private fun DrawScope.drawUpperFace(bitten: Boolean) {
    val w = size.width
    val h = size.height
    // 이마 무늬
    val mark = Path().apply {
        moveTo(w * 0.42f, 0f)
        lineTo(w * 0.5f, h * 0.32f)
        lineTo(w * 0.58f, 0f)
        close()
    }
    drawPath(mark, Cream.copy(alpha = 0.7f))
    // 주둥이
    drawOval(Cream, topLeft = Offset(w * 0.27f, h * 0.48f), size = Size(w * 0.46f, h * 0.7f))
    // 눈
    val eyeY = h * 0.38f
    val eyeR = h * 0.13f
    for (ex in listOf(w * 0.3f, w * 0.7f)) {
        if (bitten) {
            // 꽉 감은 눈 ( > < )
            val d = eyeR * 0.9f
            val dir = if (ex < w / 2) 1f else -1f
            drawLine(Color(0xFF2B2B33), Offset(ex - d * dir, eyeY - d), Offset(ex + d * dir, eyeY), 7f, StrokeCap.Round)
            drawLine(Color(0xFF2B2B33), Offset(ex + d * dir, eyeY), Offset(ex - d * dir, eyeY + d), 7f, StrokeCap.Round)
        } else {
            drawCircle(Color(0xFF2B2B33), radius = eyeR, center = Offset(ex, eyeY))
            drawCircle(Color.White, radius = eyeR * 0.38f, center = Offset(ex - eyeR * 0.3f, eyeY - eyeR * 0.35f))
            drawCircle(Color.White, radius = eyeR * 0.16f, center = Offset(ex + eyeR * 0.35f, eyeY + eyeR * 0.3f))
        }
        // 볼터치
        drawOval(Blush, topLeft = Offset(ex - eyeR * 1.4f + (if (ex < w / 2) -eyeR * 0.6f else eyeR * 0.6f), eyeY + eyeR * 1.3f), size = Size(eyeR * 2.8f, eyeR * 1.4f))
    }
    // 코
    val noseW = w * 0.13f
    val noseH = h * 0.14f
    drawOval(Color(0xFF2B2B33), topLeft = Offset(w / 2 - noseW / 2, h * 0.62f), size = Size(noseW, noseH))
    drawOval(Color.White.copy(alpha = 0.5f), topLeft = Offset(w / 2 - noseW * 0.25f, h * 0.645f), size = Size(noseW * 0.3f, noseH * 0.3f))
    // 코 아래 선
    drawLine(Color(0xFF2B2B33), Offset(w / 2, h * 0.62f + noseH), Offset(w / 2, h), 5f, StrokeCap.Round)
}

@Composable
private fun TeethRow(
    range: IntRange,
    down: Boolean,
    pressed: List<Boolean>,
    traps: Set<Int>,
    enabled: Boolean,
    onPress: (Int) -> Unit,
) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp)) {
        for (i in range) {
            Tooth(
                down = down,
                isPressed = pressed[i],
                isTrap = pressed[i] && i in traps,
                enabled = enabled && !pressed[i],
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
        isPressed -> Color(0xFF5E5E66)
        else -> Color(0xFFFFFDF5)
    }
    Canvas(modifier.height(46.dp).clickable(enabled = enabled, onClick = onClick)) {
        val w = size.width
        val h = size.height
        val inset = w * 0.06f
        val tip = if (isPressed) 0.5f else 1f
        // 끝이 둥근 송곳니 모양
        val path = Path().apply {
            if (down) {
                moveTo(inset, 0f)
                lineTo(w - inset, 0f)
                quadraticTo(w * 0.62f, h * tip * 0.75f, w / 2f, h * tip)
                quadraticTo(w * 0.38f, h * tip * 0.75f, inset, 0f)
            } else {
                moveTo(inset, h)
                lineTo(w - inset, h)
                quadraticTo(w * 0.62f, h - h * tip * 0.75f, w / 2f, h - h * tip)
                quadraticTo(w * 0.38f, h - h * tip * 0.75f, inset, h)
            }
            close()
        }
        drawPath(path, color)
    }
}
