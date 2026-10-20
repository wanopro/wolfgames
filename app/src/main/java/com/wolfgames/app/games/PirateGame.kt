package com.wolfgames.app.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.WG
import com.wolfgames.app.ui.runFor
import com.wolfgames.app.ui.spinFor
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

private const val SLOTS = 20
private const val COLS = 5
private const val ROWS = 4

private val Wood = Color(0xFF9A6332)
private val WoodDark = Color(0xFF6E4220)
private val Band = Color(0xFF5D6670)
private val BandLight = Color(0xFF8A95A1)
private val HeadFur = Color(0xFF9AA5B4)
private val HeadDark = Color(0xFF6E7A8A)
private val Cream = Color(0xFFF4ECE1)
private val Ink = Color(0xFF2B2B33)
private val Bandana = Color(0xFFE53935)
private val Gold = Color(0xFFFFC107)

/** 칼 자리 위치 (캔버스 크기 비율) */
private fun slotPos(i: Int, w: Float, h: Float): Offset {
    val r = i / COLS
    val c = i % COLS
    val y = h * (0.47f + r * 0.125f)
    // 통이 볼록하므로 가운데 줄이 조금 더 넓게
    val bulge = 1f + 0.06f * sin(Math.PI.toFloat() * (r + 0.5f) / ROWS)
    val x = w * 0.5f + (c - 2) * w * 0.135f * bulge
    return Offset(x, y)
}

/** 7. 해적 룰렛 — 20개 칼 자리 중 하나에 꽂으면 늑대가 튀어나옴 */
@Composable
fun PirateGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    var round by remember { mutableIntStateOf(0) }
    val trap = remember(round) { Random.nextInt(SLOTS) }
    val swords = remember(round) { mutableStateListOf<Int>() }
    var popped by remember(round) { mutableStateOf(false) }
    var launch by remember(round) { mutableFloatStateOf(0f) }
    var shake by remember(round) { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    fun insert(i: Int) {
        if (popped || i in swords) return
        swords.add(i)
        if (i == trap) {
            popped = true
            vibe.strong()
            scope.launch { spinFor(900L) { launch = it } }
        } else {
            vibe.tap()
            scope.launch {
                runFor(350L) { shake = 1f - it }
            }
        }
    }

    GameScaffold(s.menuPirate, onBack) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                if (popped) s.popped else s.pirateHint,
                color = if (popped) WG.Win else WG.Text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            Text(
                if (popped) s.poppedAt(swords.size) else s.slotsLeft(SLOTS - swords.size),
                color = WG.SubText,
                fontSize = 15.sp,
            )
            Spacer(Modifier.weight(1f))
            Canvas(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.9f)
                    .pointerInput(round) {
                        detectTapGestures { p ->
                            val w = size.width.toFloat()
                            val h = size.height.toFloat()
                            val hit = (0 until SLOTS).minByOrNull { (slotPos(it, w, h) - p).getDistance() }
                            if (hit != null && (slotPos(hit, w, h) - p).getDistance() < w * 0.08f) insert(hit)
                        }
                    },
            ) {
                val w = size.width
                val h = size.height
                val shakeX = sin(shake * 40f) * shake * 6.dp.toPx()
                // 늑대 (통 뒤에서 튀어나옴)
                val l = launch
                val headR = w * 0.17f
                val headC = Offset(w * 0.5f, h * 0.27f - l * h * 0.24f)
                rotate(degrees = l * 720f, pivot = headC) {
                    drawWolfHead(headC, headR, surprised = popped)
                }
                // 통
                drawBarrel(w, h, shakeX)
                // 칼 자리 & 꽂힌 칼
                for (i in 0 until SLOTS) {
                    val p = slotPos(i, w, h) + Offset(shakeX, 0f)
                    drawOval(
                        Color(0xFF2A170A),
                        topLeft = Offset(p.x - w * 0.04f, p.y - w * 0.025f),
                        size = Size(w * 0.08f, w * 0.05f),
                    )
                    val idx = swords.indexOf(i)
                    if (idx >= 0) drawSword(p, w * 0.11f, WG.PlayerColors[idx % WG.PlayerColors.size], isTrap = popped && i == trap)
                }
            }
            Spacer(Modifier.weight(1f))
            if (popped) {
                BigButton(s.playAgain, Modifier.fillMaxWidth()) { round++ }
            } else {
                BigButton(s.shuffle, Modifier.fillMaxWidth(), color = WG.Surface2, contentColor = WG.Text) { round++ }
            }
        }
    }
}

private fun DrawScope.drawBarrel(w: Float, h: Float, dx: Float) {
    val left = w * 0.14f + dx
    val right = w * 0.86f + dx
    val top = h * 0.36f
    val bottom = h * 0.98f
    val bulge = w * 0.05f
    val body = Path().apply {
        moveTo(left, top)
        lineTo(right, top)
        quadraticTo(right + bulge * 2, (top + bottom) / 2, right, bottom)
        lineTo(left, bottom)
        quadraticTo(left - bulge * 2, (top + bottom) / 2, left, top)
        close()
    }
    drawPath(body, Wood)
    // 나무판 줄
    for (k in 1..5) {
        val x = left + (right - left) * k / 6f
        val bend = (x - (left + right) / 2) / ((right - left) / 2) * bulge
        val plank = Path().apply {
            moveTo(x, top)
            quadraticTo(x + bend * 2, (top + bottom) / 2, x, bottom)
        }
        drawPath(plank, WoodDark, style = Stroke(width = 2.dp.toPx()))
    }
    // 테두리 띠
    for (y in listOf(top + h * 0.02f, bottom - h * 0.03f, (top + bottom) / 2 - h * 0.005f)) {
        val ext = if (y == (top + bottom) / 2 - h * 0.005f) bulge else bulge * 0.35f
        drawRoundRect(
            Band,
            topLeft = Offset(left - ext, y - h * 0.018f),
            size = Size(right - left + ext * 2, h * 0.036f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(h * 0.018f),
        )
        drawLine(BandLight, Offset(left - ext + 6f, y - h * 0.008f), Offset(right + ext - 6f, y - h * 0.008f), strokeWidth = 2f)
    }
    // 통 윗면 (구멍)
    drawOval(WoodDark, topLeft = Offset(left - 4f, top - h * 0.045f), size = Size(right - left + 8f, h * 0.09f))
    drawOval(Color(0xFF2A170A), topLeft = Offset(left + w * 0.04f, top - h * 0.03f), size = Size(right - left - w * 0.08f, h * 0.06f))
}

private fun DrawScope.drawSword(at: Offset, len: Float, grip: Color, isTrap: Boolean) {
    // 칼날이 통에 박혀 있고 손잡이가 위로 비스듬히 튀어나온 모습
    val tip = at
    val guard = Offset(at.x - len * 0.35f, at.y - len * 0.55f)
    val end = Offset(at.x - len * 0.75f, at.y - len * 1.15f)
    drawLine(Color(0xFFCFD8DC), tip, guard, strokeWidth = len * 0.12f, cap = StrokeCap.Round)
    // 가드 (손잡이와 수직)
    val gx = len * 0.22f
    val gy = len * 0.13f
    drawLine(if (isTrap) WG.Win else Gold, Offset(guard.x - gx, guard.y + gy), Offset(guard.x + gx, guard.y - gy), strokeWidth = len * 0.12f, cap = StrokeCap.Round)
    drawLine(grip, guard, end, strokeWidth = len * 0.16f, cap = StrokeCap.Round)
    drawCircle(if (isTrap) WG.Win else Gold, radius = len * 0.11f, center = end)
}

/** 해적 두건을 쓴 귀여운 늑대 얼굴 (정면) */
private fun DrawScope.drawWolfHead(c: Offset, r: Float, surprised: Boolean) {
    // 귀
    for (sx in listOf(-1f, 1f)) {
        val ear = Path().apply {
            moveTo(c.x + sx * r * 0.25f, c.y - r * 0.7f)
            lineTo(c.x + sx * r * 0.85f, c.y - r * 1.35f)
            lineTo(c.x + sx * r * 0.95f, c.y - r * 0.35f)
            close()
        }
        drawPath(ear, HeadDark)
        val inner = Path().apply {
            moveTo(c.x + sx * r * 0.45f, c.y - r * 0.68f)
            lineTo(c.x + sx * r * 0.8f, c.y - r * 1.1f)
            lineTo(c.x + sx * r * 0.85f, c.y - r * 0.5f)
            close()
        }
        drawPath(inner, Color(0xFFF6AFC0))
    }
    drawCircle(HeadFur, radius = r, center = c)
    // 해적 두건
    val band = Path().apply {
        moveTo(c.x - r * 0.97f, c.y - r * 0.25f)
        quadraticTo(c.x, c.y - r * 1.25f, c.x + r * 0.97f, c.y - r * 0.25f)
        quadraticTo(c.x, c.y - r * 0.75f, c.x - r * 0.97f, c.y - r * 0.25f)
        close()
    }
    drawPath(band, Bandana)
    for (k in listOf(-0.45f, 0f, 0.45f)) {
        drawCircle(Color.White, radius = r * 0.06f, center = Offset(c.x + k * r, c.y - r * 0.68f + kotlin.math.abs(k) * r * 0.25f))
    }
    // 두건 매듭
    drawCircle(Bandana, radius = r * 0.13f, center = Offset(c.x + r * 0.95f, c.y - r * 0.3f))
    drawLine(Bandana, Offset(c.x + r * 0.95f, c.y - r * 0.3f), Offset(c.x + r * 1.25f, c.y - r * 0.05f), strokeWidth = r * 0.12f, cap = StrokeCap.Round)
    // 주둥이
    drawOval(Cream, topLeft = Offset(c.x - r * 0.48f, c.y + r * 0.08f), size = Size(r * 0.96f, r * 0.72f))
    // 눈
    val ey = c.y - r * 0.08f
    for (sx in listOf(-1f, 1f)) {
        val ex = c.x + sx * r * 0.42f
        if (surprised) {
            val d = r * 0.13f
            drawLine(Ink, Offset(ex - d * sx, ey - d), Offset(ex + d * sx, ey), strokeWidth = r * 0.07f, cap = StrokeCap.Round)
            drawLine(Ink, Offset(ex + d * sx, ey), Offset(ex - d * sx, ey + d), strokeWidth = r * 0.07f, cap = StrokeCap.Round)
        } else {
            drawCircle(Ink, radius = r * 0.15f, center = Offset(ex, ey))
            drawCircle(Color.White, radius = r * 0.06f, center = Offset(ex - r * 0.04f, ey - r * 0.05f))
        }
        drawOval(Color(0x66FF8FA3), topLeft = Offset(ex - r * 0.2f + sx * r * 0.08f, ey + r * 0.2f), size = Size(r * 0.4f, r * 0.2f))
    }
    // 코
    drawOval(Ink, topLeft = Offset(c.x - r * 0.13f, c.y + r * 0.16f), size = Size(r * 0.26f, r * 0.17f))
    // 입
    if (surprised) {
        drawOval(Color(0xFF8E2440), topLeft = Offset(c.x - r * 0.12f, c.y + r * 0.42f), size = Size(r * 0.24f, r * 0.24f))
    } else {
        drawArc(
            Ink, startAngle = 20f, sweepAngle = 140f, useCenter = false,
            topLeft = Offset(c.x - r * 0.16f, c.y + r * 0.32f), size = Size(r * 0.32f, r * 0.2f),
            style = Stroke(width = r * 0.05f, cap = StrokeCap.Round),
        )
    }
}
