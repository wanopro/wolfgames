package com.wolfgames.app.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.WG
import com.wolfgames.app.ui.spinFor
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

/** 8. 화살표 돌리기 — 정한 시간 동안 돌다가 천천히 멈춤 */
@Composable
fun ArrowGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    var secs by rememberSaveable { mutableIntStateOf(5) }
    var angle by remember { mutableFloatStateOf(Random.nextFloat() * 360f) }
    var spinning by remember { mutableStateOf(false) }
    var stopped by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun spin() {
        if (spinning) return
        spinning = true
        stopped = false
        vibe.tap()
        scope.launch {
            // 처음 속도가 초당 약 2.5바퀴가 되도록 회전 수를 시간에 맞춤
            val turns = max(1.5f, secs * 0.85f)
            val from = angle
            val total = 360f * turns + Random.nextFloat() * 360f
            spinFor(secs * 1000L) { e -> angle = from + total * e }
            angle %= 360f
            spinning = false
            stopped = true
            vibe.strong()
        }
    }

    GameScaffold(s.menuArrow, onBack) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                when {
                    spinning -> s.arrowSpinning
                    stopped -> s.arrowStopped
                    else -> s.arrowTap
                },
                color = if (stopped) WG.Accent else WG.Text,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.weight(1f))
            val tap = remember { MutableInteractionSource() }
            Canvas(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable(interactionSource = tap, indication = null) { spin() },
            ) {
                val c = center
                val r = size.minDimension / 2f - 8.dp.toPx()
                // 판
                drawCircle(WG.Surface, radius = r, center = c)
                drawCircle(WG.Surface2, radius = r, center = c, style = Stroke(width = 6.dp.toPx()))
                // 화살표
                rotate(degrees = angle, pivot = c) {
                    val len = r * 0.72f
                    val shaftW = r * 0.07f
                    val headW = r * 0.2f
                    val headL = r * 0.24f
                    val arrow = Path().apply {
                        moveTo(c.x, c.y - len) // 끝
                        lineTo(c.x + headW, c.y - len + headL)
                        lineTo(c.x + shaftW, c.y - len + headL)
                        lineTo(c.x + shaftW, c.y + len * 0.55f)
                        lineTo(c.x - shaftW, c.y + len * 0.55f)
                        lineTo(c.x - shaftW, c.y - len + headL)
                        lineTo(c.x - headW, c.y - len + headL)
                        close()
                    }
                    drawPath(arrow, WG.Accent)
                    drawPath(arrow, Color(0xFF7A5200), style = Stroke(width = 2.dp.toPx()))
                    // 꼬리 깃털
                    for (k in 0..2) {
                        val y = c.y + len * (0.3f + k * 0.12f)
                        val feather = Path().apply {
                            moveTo(c.x - shaftW, y)
                            lineTo(c.x - shaftW - r * 0.12f, y + r * 0.1f)
                            lineTo(c.x - shaftW - r * 0.12f, y + r * 0.02f)
                            close()
                            moveTo(c.x + shaftW, y)
                            lineTo(c.x + shaftW + r * 0.12f, y + r * 0.1f)
                            lineTo(c.x + shaftW + r * 0.12f, y + r * 0.02f)
                            close()
                        }
                        drawPath(feather, WG.Win)
                    }
                }
                // 가운데 축
                drawCircle(WG.Bg, radius = r * 0.11f, center = c)
                drawCircle(WG.Accent, radius = r * 0.07f, center = c)
            }
            Spacer(Modifier.weight(1f))
            Stepper(s.arrowSeconds, secs, 1..15) { if (!spinning) secs = it }
            BigButton(s.spin, Modifier.fillMaxWidth(), enabled = !spinning) { spin() }
        }
    }
}
