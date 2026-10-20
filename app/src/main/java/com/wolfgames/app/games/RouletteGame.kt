package com.wolfgames.app.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.WG
import kotlinx.coroutines.launch
import kotlin.random.Random

private val SlicePalette = listOf(
    Color(0xFF3949AB),
    Color(0xFF00897B),
    Color(0xFF7B1FA2),
    Color(0xFF1E88E5),
    Color(0xFF43A047),
    Color(0xFF5E35B1),
)
private val WinA = Color(0xFFE53950)
private val WinB = Color(0xFFFF6B7F)

/** 4. 돌림판 */
@Composable
fun RouletteGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    var m by rememberSaveable { mutableIntStateOf(8) }
    var k by rememberSaveable { mutableIntStateOf(1) }
    var started by rememberSaveable { mutableStateOf(false) }

    GameScaffold(s.menuRoulette, onBack) {
        if (!started) {
            SetupPanel(
                description = s.rouletteDesc,
                onStart = { started = true },
            ) {
                Stepper(s.slices, m, 2..20) {
                    m = it
                    if (k > m - 1) k = m - 1
                }
                Stepper(s.prizes, k, 1..(m - 1)) { k = it }
            }
        } else {
            RoulettePlay(m, k, onSetup = { started = false })
        }
    }
}

@Composable
private fun RoulettePlay(m: Int, k: Int, onSetup: () -> Unit) {
    val s = LocalStrings.current
    var seed by remember { mutableIntStateOf(0) }
    val wins = remember(seed) { (List(k) { true } + List(m - k) { false }).shuffled() }
    val rotation = remember { Animatable(0f) }
    var spinning by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val tm = rememberTextMeasurer()

    val fontSize = when {
        m <= 8 -> 20.sp
        m <= 14 -> 15.sp
        else -> 11.sp
    }
    val layouts = remember(wins, m, s) {
        val style = TextStyle(color = Color.White, fontSize = fontSize, fontWeight = FontWeight.Bold)
        List(m) { i -> tm.measure(if (wins[i]) s.win else "${i + 1}", style) }
    }
    val hubLayout = remember(s) {
        tm.measure(s.spinHub, TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black))
    }

    fun spin() {
        if (spinning) return
        spinning = true
        result = null
        scope.launch {
            val target = rotation.value + 360f * Random.nextInt(5, 9) + Random.nextFloat() * 360f
            rotation.animateTo(target, tween(4500, easing = CubicBezierEasing(0.12f, 0.8f, 0.2f, 1f)))
            rotation.snapTo(rotation.value % 360f)
            val sweep = 360f / m
            val a = ((-rotation.value) % 360f + 360f) % 360f
            result = (a / sweep).toInt().coerceIn(0, m - 1)
            spinning = false
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val tapSource = remember { MutableInteractionSource() }
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val r = result
        Text(
            when {
                r != null && wins[r] -> s.sliceWin(r + 1)
                r != null -> s.slicePass(r + 1)
                spinning -> s.drumroll
                else -> s.tapWheel
            },
            color = if (r != null && wins[r]) WG.Win else WG.Text,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
        )
        Text(s.wheelInfo(m, k), color = WG.SubText, fontSize = 14.sp)

        Spacer(Modifier.weight(1f))

        Canvas(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable(interactionSource = tapSource, indication = null) { spin() },
        ) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val rad = size.minDimension / 2f - 10.dp.toPx()
            val sweep = 360f / m
            val rot = rotation.value
            val tl = Offset(c.x - rad, c.y - rad)
            val sz = Size(rad * 2f, rad * 2f)

            for (i in 0 until m) {
                val start = -90f + rot + i * sweep
                val col = if (wins[i]) {
                    if (i % 2 == 0) WinA else WinB
                } else {
                    SlicePalette[i % SlicePalette.size]
                }
                drawArc(color = col, startAngle = start, sweepAngle = sweep, useCenter = true, topLeft = tl, size = sz)
                drawArc(
                    color = WG.Bg,
                    startAngle = start,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = tl,
                    size = sz,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
            // 결과 칸 강조
            val res = result
            if (res != null) {
                drawArc(
                    color = WG.Accent,
                    startAngle = -90f + rot + res * sweep,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = tl,
                    size = sz,
                    style = Stroke(width = 6.dp.toPx()),
                )
            }
            // 칸 글자
            for (i in 0 until m) {
                val mid = -90f + rot + i * sweep + sweep / 2f
                val lay = layouts[i]
                rotate(degrees = mid + 90f, pivot = c) {
                    drawText(
                        textLayoutResult = lay,
                        topLeft = Offset(c.x - lay.size.width / 2f, c.y - rad * 0.72f - lay.size.height / 2f),
                    )
                }
            }
            // 가운데 버튼
            drawCircle(WG.Bg, radius = rad * 0.2f, center = c)
            drawCircle(WG.Accent, radius = rad * 0.17f, center = c)
            drawText(
                textLayoutResult = hubLayout,
                topLeft = Offset(c.x - hubLayout.size.width / 2f, c.y - hubLayout.size.height / 2f),
            )
            // 위쪽 화살표
            val pw = 16.dp.toPx()
            val pointer = Path().apply {
                moveTo(c.x - pw, c.y - rad - 6.dp.toPx())
                lineTo(c.x + pw, c.y - rad - 6.dp.toPx())
                lineTo(c.x, c.y - rad + 24.dp.toPx())
                close()
            }
            drawPath(pointer, Color.White)
            drawPath(pointer, Color.Black, style = Stroke(width = 2.dp.toPx()))
        }

        Spacer(Modifier.weight(1f))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BigButton(s.spin, Modifier.weight(1f), enabled = !spinning) { spin() }
            BigButton(
                s.shuffleWins,
                Modifier.weight(1f),
                enabled = !spinning,
                color = WG.Surface2,
                contentColor = WG.Text,
            ) {
                seed++
                result = null
            }
        }
        TextButton(onClick = onSetup, enabled = !spinning) { Text(s.changeWheel, color = WG.SubText) }
    }
}
