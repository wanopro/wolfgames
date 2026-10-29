package com.wolfgames.app.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.wolfgames.app.ui.Text
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import com.wolfgames.app.ui.Ctx
import com.wolfgames.app.ui.TAU
import com.wolfgames.app.ui.pirateHead2
import com.wolfgames.app.ui.text
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min

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
    // 통과할 때마다 늑대가 올라와 메롱 (teaseAt: 시작 시각, now: 매 프레임 시간)
    var now by remember { mutableFloatStateOf(0f) }
    var teaseAt by remember(round) { mutableFloatStateOf(-9f) }
    val density = LocalDensity.current.density
    LaunchedEffect(Unit) { val st = withFrameNanos { it }; while (true) { val f = withFrameNanos { it }; now = (f - st) / 1e9f } }

    fun insert(i: Int) {
        if (popped || i in swords) return
        swords.add(i)
        if (i == trap) {
            popped = true
            vibe.strong()
            scope.launch { spinFor(900L) { launch = it } }
        } else {
            vibe.tap()
            teaseAt = now
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
                if (popped) s.popped else if (now - teaseAt < 1.3f) s.pirateTease else s.pirateHint,
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
                // 통
                drawBarrel(w, h, shakeX)
                drawIntoCanvas { cc ->
                    val x = Ctx(cc.nativeCanvas, density)
                    val W = w / density; val H = h / density; val sx = shakeX / density
                    val l = W * 0.14f + sx; val rr = W * 0.86f + sx; val rimY = H * 0.36f; val rx = (rr - l - W * 0.08f) / 2; val ry = H * 0.03f; val cxr = (l + rr) / 2
                    val hr = W * 0.17f; val hx = W * 0.5f; val tt = now - teaseAt
                    val mode = if (popped) "sur" else if (tt < 1.3f) "tease" else "idle"
                    val hy = when (mode) {
                        "sur" -> rimY - hr * 0.3f - launch * (rimY - hr * 0.3f - hr * 0.2f)
                        "tease" -> { val u = min(1f, tt / 0.25f); val bk = if (tt > 1.05f) min(1f, (tt - 1.05f) / 0.2f) else 0f; val up = (if (u < 1) 1 - (1 - u) * (1 - u) * (1 - u) * cos(u * 8) else 1f) * (1 - bk); rimY - hr * 0.3f - up * hr * 0.75f + sin(tt * 9) * hr * 0.04f * (1 - bk) }
                        else -> rimY - hr * 0.3f + sin(now * 2.2f) * hr * 0.05f
                    }
                    // 늑대: 통 입구 앞쪽 테두리보다 위만 보이게 (통 안에 들어가 있음)
                    x.save()
                    x.beginPath(); x.moveTo(0f, 0f); x.lineTo(W, 0f); x.lineTo(W, rimY); x.lineTo(cxr + rx, rimY)
                    x.ellipse(cxr, rimY, rx, ry, 0f, 0f, Math.PI.toFloat()); x.lineTo(0f, rimY); x.closePath(); x.clip()
                    x.translate(hx, hy); if (mode == "sur") x.rotate(launch * Math.PI.toFloat() * 4); x.translate(-hx, -hy)
                    x.pirateHead2(hx, hy, hr, mode, tt)
                    x.restore()
                    // 통 입구 앞쪽 테두리 + 통을 붙잡은 앞발
                    x.beginPath(); x.ellipse(cxr, rimY, (rr - l + 8) / 2, H * 0.045f, 0f, 0f, Math.PI.toFloat()); x.ellipse(cxr, rimY, rx, ry, 0f, Math.PI.toFloat(), 0f, true); x.closePath(); x.fillStyle = "#6E4220"; x.fill()
                    if (mode != "sur" || launch < 0.15f) {
                        val lift = if (mode == "tease") min(1f, tt / 0.2f) * (if (tt > 1.05f) max(0f, 1 - (tt - 1.05f) / 0.2f) else 1f) else 0f
                        for (k in listOf(-1f, 1f)) {
                            val px = hx + k * hr * 0.75f; val py = rimY + H * 0.012f - lift * hr * 0.1f
                            x.beginPath(); x.ellipse(px, py, hr * 0.28f, hr * 0.17f, 0f, 0f, TAU); x.fillStyle = "#9AA5B4"; x.fill(); x.lineWidth = 1.5f; x.strokeStyle = "rgba(0,0,0,.3)"; x.stroke()
                            for (q in listOf(-1f, 0f, 1f)) { x.beginPath(); x.moveTo(px + q * hr * 0.09f, py - hr * 0.02f); x.lineTo(px + q * hr * 0.09f, py + hr * 0.12f); x.stroke() }
                        }
                    }
                    x.finish()
                }
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
                // 메롱~ 말풍선
                val tt2 = now - teaseAt
                if (!popped && tt2 > 0.15f && tt2 < 1.15f) drawIntoCanvas { cc ->
                    val x = Ctx(cc.nativeCanvas, density)
                    val W = w / density; val H = h / density; val hr = W * 0.17f; val hx = W * 0.5f
                    val b = min(1f, (tt2 - 0.15f) / 0.2f); val hy = H * 0.36f - hr * 1.05f
                    x.save(); x.translate(hx + hr * 1.35f, hy - hr * 1.15f + sin(tt2 * 8) * 3); x.scale(b, b); x.rotate(-0.1f)
                    x.font(hr * 0.42f); val tw = x.measureText(s.fingerMerong) + hr * 0.4f; val th = hr * 0.62f
                    x.beginPath(); x.roundRect(-tw / 2, -th / 2, tw, th, th / 2); x.fillStyle = "#fff"; x.fill()
                    x.beginPath(); x.moveTo(-tw * 0.3f, th / 2 - 2); x.lineTo(-tw * 0.45f, th / 2 + hr * 0.2f); x.lineTo(-tw * 0.1f, th / 2 - 2); x.fill()
                    x.text(s.fingerMerong, 0f, 2f, hr * 0.42f, "#E53950"); x.restore(); x.finish()
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

