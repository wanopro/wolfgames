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
import com.wolfgames.app.ui.runFor
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.drawscope.DrawScope
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

/** 8. 늑대 돌리기 — 정한 시간 동안 입을 벌리고 돌다가, 멈추면 꽉 묾 */
@Composable
fun ArrowGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    var secs by rememberSaveable { mutableIntStateOf(5) }
    var angle by remember { mutableFloatStateOf(Random.nextFloat() * 360f) }
    var spinning by remember { mutableStateOf(false) }
    var stopped by remember { mutableStateOf(false) }
    var open by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    fun spin() {
        if (spinning) return
        spinning = true
        stopped = false
        vibe.tap()
        scope.launch { spinFor(180L) { open = it } }
        scope.launch {
            // 처음 속도가 초당 약 2.5바퀴가 되도록 회전 수를 시간에 맞춤
            val turns = max(1.5f, secs * 0.85f)
            val from = angle
            val total = 360f * turns + Random.nextFloat() * 360f
            spinFor(secs * 1000L) { e -> angle = from + total * e }
            angle %= 360f
            runFor(130L) { open = 1f - it } // 꽉!
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
                color = if (stopped) WG.Win else WG.Text,
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
                // 늑대 (앞 = 바깥쪽을 향해 회전)
                withTransform({
                    translate(c.x, c.y)
                    rotate(degrees = angle, pivot = Offset.Zero)
                }) {
                    drawSpinWolf(r, open, bite = stopped && !spinning)
                }
            }
            Spacer(Modifier.weight(1f))
            Stepper(s.arrowSeconds, secs, 1..15) { if (!spinning) secs = it }
            BigButton(s.spin, Modifier.fillMaxWidth(), enabled = !spinning) { spin() }
        }
    }
}

private val SwFur = Color(0xFF9AA5B4)
private val SwDark = Color(0xFF6E7A8A)
private val SwCream = Color(0xFFF4ECE1)
private val SwLine = Color(0x59000000)
private val SwInk = Color(0xFF2B2B33)

private fun DrawScope.oval(cx: Float, cy: Float, rx: Float, ry: Float, color: Color, outline: Boolean = false) {
    drawOval(color, topLeft = Offset(cx - rx, cy - ry), size = Size(rx * 2, ry * 2))
    if (outline) drawOval(SwLine, topLeft = Offset(cx - rx, cy - ry), size = Size(rx * 2, ry * 2), style = Stroke(width = 2f))
}

/**
 * 위에서 본 늑대 (원점 = 판 가운데, 앞 = -y). open: 입 벌린 정도 0~1, bite: 꽉 문 표정
 * (웹 버전 web/games-fun.js 의 spinWolf 와 같은 그림)
 */
private fun DrawScope.drawSpinWolf(r: Float, open: Float, bite: Boolean) {
    val l = r * 0.86f
    val hr = r * 0.25f
    val hy = -l * 0.36f
    // 꼬리
    oval(0f, l * 0.62f, r * 0.1f, r * 0.2f, SwFur, true)
    oval(0f, l * 0.62f + r * 0.13f, r * 0.06f, r * 0.07f, Color.White)
    // 발 + 몸통
    for ((x, y) in listOf(-1f to -0.12f, 1f to -0.12f, -1f to 0.32f, 1f to 0.32f)) oval(x * r * 0.13f, y * l, r * 0.055f, r * 0.045f, SwCream, true)
    oval(0f, l * 0.1f, r * 0.12f, r * 0.3f, SwFur, true)
    oval(0f, l * 0.05f, r * 0.05f, r * 0.16f, SwDark.copy(alpha = 0.5f))
    // 귀
    for (sx in listOf(-1f, 1f)) {
        val ear = Path().apply {
            moveTo(sx * hr * 0.2f, hy + hr * 0.2f); lineTo(sx * hr * 1.15f, hy + hr * 0.95f); lineTo(sx * hr * 0.95f, hy - hr * 0.15f); close()
        }
        drawPath(ear, SwDark); drawPath(ear, SwLine, style = Stroke(width = 2f))
        val inner = Path().apply {
            moveTo(sx * hr * 0.45f, hy + hr * 0.2f); lineTo(sx * hr * 1.0f, hy + hr * 0.7f); lineTo(sx * hr * 0.85f, hy + hr * 0.05f); close()
        }
        drawPath(inner, Color(0xFFF6AFC0))
    }
    // 턱
    val hingeY = hy - hr * 0.55f
    val tipY = -l
    val len = hingeY - tipY
    val a = open * 38f
    if (open > 0.05f) {
        val rad = Math.toRadians(a.toDouble())
        val sx = (kotlin.math.sin(rad) * len).toFloat()
        val sy = (kotlin.math.cos(rad) * len).toFloat()
        val mouth = Path().apply { moveTo(0f, hingeY + hr * 0.1f); lineTo(-sx, hingeY - sy); lineTo(sx, hingeY - sy); close() }
        drawPath(mouth, Color(0xFF8E2440))
        oval(0f, hingeY - len * 0.3f, hr * 0.22f, len * 0.28f, Color(0xFFE86A86))
    }
    for (sx in listOf(-1f, 1f)) {
        rotate(degrees = sx * a, pivot = Offset(0f, hingeY)) {
            val jaw = Path().apply {
                moveTo(0f, hingeY); lineTo(sx * hr * 0.66f, hingeY + hr * 0.1f)
                quadraticTo(sx * hr * 0.6f, hingeY - len * 0.75f, sx * hr * 0.16f, tipY); lineTo(0f, tipY); close()
            }
            drawPath(jaw, SwFur); drawPath(jaw, SwLine, style = Stroke(width = 2f))
            val cream = Path().apply {
                moveTo(0f, hingeY - len * 0.1f); lineTo(sx * hr * 0.16f, hingeY - len * 0.12f)
                quadraticTo(sx * hr * 0.16f, hingeY - len * 0.7f, sx * hr * 0.05f, tipY + len * 0.14f); lineTo(0f, tipY + len * 0.14f); close()
            }
            drawPath(cream, SwCream)
            if (open > 0.05f) {
                for (k in 0 until 4) {
                    val y0 = tipY + len * (0.12f + k * 0.2f)
                    val tooth = Path().apply { moveTo(0f, y0); lineTo(0f, y0 + len * 0.12f); lineTo(-sx * hr * 0.16f, y0 + len * 0.06f); close() }
                    drawPath(tooth, Color.White)
                }
            }
            oval(sx * hr * 0.04f, tipY + hr * 0.12f, hr * 0.17f, hr * 0.12f, SwInk)
        }
    }
    // 머리
    drawCircle(SwFur, radius = hr, center = Offset(0f, hy))
    drawCircle(SwLine, radius = hr, center = Offset(0f, hy), style = Stroke(width = 2f))
    val mark = Path().apply { moveTo(-hr * 0.18f, hy - hr); lineTo(0f, hy - hr * 0.35f); lineTo(hr * 0.18f, hy - hr); close() }
    drawPath(mark, SwCream.copy(alpha = 0.8f))
    // 눈
    for (sx in listOf(-1f, 1f)) {
        val ex = sx * hr * 0.45f
        val ey = hy - hr * 0.28f
        if (bite) {
            val d = hr * 0.17f
            val eye = Path().apply { moveTo(ex - d * sx, ey - d); lineTo(ex + d * sx, ey); lineTo(ex - d * sx, ey + d) }
            drawPath(eye, SwInk, style = Stroke(width = hr * 0.1f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        } else {
            drawCircle(SwInk, radius = hr * 0.21f, center = Offset(ex, ey))
            drawCircle(Color.White, radius = hr * 0.08f, center = Offset(ex - hr * 0.06f, ey - hr * 0.07f))
        }
        oval(ex + sx * hr * 0.15f, ey + hr * 0.32f, hr * 0.17f, hr * 0.09f, Color(0x8CFF8FA3))
    }
}

