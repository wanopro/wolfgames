package com.wolfgames.app.games

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.VTextButton
import com.wolfgames.app.ui.WG
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

private const val WAIT_MS = 500L

private class Spot(var id: PointerId?, var pos: Offset, val color: Color, val t0: Long) {
    val down get() = id != null
    var lose = false
}

/**
 * 손가락게임 · 손가락선택
 * 걸릴 인원을 정하고, 다 같이 손가락을 올렸다가 뗌.
 * 모든 손가락이 떨어지고 0.5초 동안 새 손가락이 없으면, 걸린 자리에만 늑대가 나타나 메롱~
 */
@Composable
fun FingerGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    var losers by rememberSaveable { mutableIntStateOf(1) }
    var started by rememberSaveable { mutableStateOf(false) }
    if (!started) {
        GameScaffold(s.fingerTitle, onBack) {
            SetupPanel(s.fingerDesc, onStart = { started = true }) {
                Stepper(s.fingerLosers, losers, 1..9) { losers = it }
            }
        }
    } else {
        BackHandler { started = false }
        FingerPlay(losers, onExit = { started = false })
    }
}

@Composable
private fun FingerPlay(losers: Int, onExit: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    val scope = rememberCoroutineScope()
    val spots = remember { mutableStateListOf<Spot>() }
    var result by remember { mutableStateOf(false) }
    var resultAt by remember { mutableLongStateOf(0L) }
    var msgUntil by remember { mutableLongStateOf(0L) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var timer by remember { mutableStateOf<Job?>(null) }
    val density = LocalDensity.current

    LaunchedEffect(Unit) { while (true) { withFrameMillis { }; now = System.currentTimeMillis() } }

    fun decide() {
        if (result || spots.isEmpty() || spots.any { it.down }) return
        if (spots.size <= losers) {
            msgUntil = System.currentTimeMillis() + 1800
            vibe.tap()
            spots.clear()
            return
        }
        spots.indices.shuffled().take(losers).forEach { spots[it].lose = true }
        result = true
        resultAt = System.currentTimeMillis()
        vibe.strong()
    }

    fun pickColor(): Color {
        val used = spots.map { it.color }
        return WG.PlayerColors.firstOrNull { it !in used } ?: WG.PlayerColors[spots.size % WG.PlayerColors.size]
    }

    GameScaffold(s.fingerTitle, onBack = onExit, actions = {
        VTextButton(onClick = onExit) { Text("${s.fingerLosers} $losers", color = WG.Accent, fontWeight = FontWeight.Bold) }
    }) {
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(losers) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val t = System.currentTimeMillis()
                            for (change in event.changes) {
                                when {
                                    change.changedToDown() -> {
                                        if (result) {
                                            if (t - resultAt > 600) { spots.clear(); result = false; vibe.tap() }
                                        } else {
                                            timer?.cancel()
                                            msgUntil = 0L
                                            spots.add(Spot(change.id, change.position, pickColor(), t))
                                            vibe.tap()
                                        }
                                    }

                                    change.changedToUp() -> {
                                        val sp = spots.firstOrNull { it.id == change.id }
                                        if (sp != null) {
                                            sp.id = null
                                            if (spots.none { it.down }) {
                                                timer?.cancel()
                                                timer = scope.launch { delay(WAIT_MS); decide() }
                                            }
                                        }
                                    }

                                    change.pressed -> spots.firstOrNull { it.id == change.id }?.pos = change.position
                                }
                                change.consume()
                            }
                        }
                    }
                },
        ) {
            val word = s.fingerMerong
            Canvas(Modifier.fillMaxSize()) {
                val r = min(52.dp.toPx(), size.width * 0.13f)
                val tick = now // 매 프레임 다시 그리기
                for (p in spots) {
                    if (result) {
                        if (p.lose) continue
                        val a = max(0f, 1f - (tick - resultAt) / 300f)
                        drawCircle(p.color.copy(alpha = 0.25f + 0.75f * a), radius = r * (0.7f + 0.3f * a), center = p.pos)
                        drawIntoCanvas { c ->
                            val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                                color = android.graphics.Color.WHITE; textSize = 15.sp.toPx(); textAlign = android.graphics.Paint.Align.CENTER
                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                            }
                            c.nativeCanvas.drawText(s.fingerPass, p.pos.x, p.pos.y + paint.textSize * 0.35f, paint)
                        }
                        continue
                    }
                    val pulse = if (p.down) 1f + sin((tick - p.t0) / 120f) * 0.05f else 1f
                    drawCircle(p.color, radius = r * pulse, center = p.pos)
                    drawCircle(if (p.down) p.color.copy(alpha = 0.5f) else Color.White, radius = r * 1.18f, center = p.pos, style = Stroke(6.dp.toPx()))
                }
                if (result) {
                    val t = (tick - resultAt) / 1000f
                    for (p in spots) if (p.lose) drawTauntWolf(p.pos.x, p.pos.y, r, t, p.color, word)
                }
            }
            when {
                result -> {
                    Text(s.fingerResult(losers), color = WG.Accent, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp))
                    if (now - resultAt > 1000) {
                        Text(s.fingerAgain, color = WG.SubText, fontSize = 14.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp))
                    }
                }
                now < msgUntil -> Text(s.fingerNeed(losers + 1), color = WG.SubText, fontSize = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center).padding(24.dp))
                spots.isEmpty() -> Text(s.fingerHint, color = WG.SubText, fontSize = 18.sp, lineHeight = 28.sp, textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center).padding(24.dp))
            }
        }
    }
}

/** 메롱하는 늑대 얼굴 (웹 tauntWolf 와 같은 그림). (x,y) 중심, r 반지름, t 나타난 뒤 지난 초 */
private fun DrawScope.drawTauntWolf(x: Float, y: Float, r: Float, t: Float, ring: Color, word: String) {
    val fur = 0xFF9AA5B4.toInt(); val dark = 0xFF6E7A8A.toInt(); val cream = 0xFFF4ECE1.toInt()
    val ink = 0xFF2B2B33.toInt(); val ol = 0x59000000
    val u = min(1f, t / 0.35f)
    val pop = if (u < 1f) 1f - (1f - u).pow(3) * cos(u * 9f) else 1f
    val tilt = sin(t * 7f) * 0.2f * u
    val wag = sin(t * 16f) * 0.32f
    val tl = 0.85f + sin(t * 8f) * 0.15f
    val deg = { rad: Float -> (rad * 180f / PI.toFloat()) }
    drawIntoCanvas { cc ->
        val c = cc.nativeCanvas
        val fill = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { style = android.graphics.Paint.Style.FILL }
        val line = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            style = android.graphics.Paint.Style.STROKE; strokeCap = android.graphics.Paint.Cap.ROUND; strokeJoin = android.graphics.Paint.Join.ROUND
        }
        val lw = max(1.5f, r * 0.04f)
        fun fillPath(p: android.graphics.Path, color: Int, stroke: Int? = null, w: Float = lw) {
            fill.color = color; c.drawPath(p, fill)
            if (stroke != null) { line.color = stroke; line.strokeWidth = w; c.drawPath(p, line) }
        }
        fun oval(cx: Float, cy: Float, rx: Float, ry: Float, color: Int) {
            fill.color = color; c.drawOval(android.graphics.RectF(cx - rx, cy - ry, cx + rx, cy + ry), fill)
        }

        c.save(); c.translate(x, y); c.scale(pop, pop)
        // 플레이어 색 링
        fill.color = ring.copy(alpha = 0.25f).toArgb(); c.drawCircle(0f, 0f, r * 1.18f, fill)
        line.color = ring.toArgb(); line.strokeWidth = r * 0.1f; c.drawCircle(0f, 0f, r * 1.18f, line)
        c.rotate(deg(tilt))
        // 귀 (번갈아 쫑긋)
        for (sx in listOf(-1f, 1f)) {
            val e = sin(t * 7f + if (sx > 0) 0f else PI.toFloat()) * 0.08f
            c.save(); c.translate(sx * r * 0.55f, -r * 0.6f); c.rotate(deg(sx * (0.25f + e)))
            fillPath(android.graphics.Path().apply { moveTo(-r * 0.3f, r * 0.15f); lineTo(0f, -r * 0.62f); lineTo(r * 0.3f, r * 0.15f); close() }, dark, ol)
            fillPath(android.graphics.Path().apply { moveTo(-r * 0.15f, r * 0.08f); lineTo(0f, -r * 0.4f); lineTo(r * 0.15f, r * 0.08f); close() }, 0xFFF6AFC0.toInt())
            c.restore()
        }
        // 얼굴 + 볼털
        val face = android.graphics.Path().apply {
            moveTo(-r * 0.95f, -r * 0.1f)
            quadTo(-r * 0.95f, -r * 0.95f, 0f, -r * 0.95f)
            quadTo(r * 0.95f, -r * 0.95f, r * 0.95f, -r * 0.1f)
            lineTo(r * 1.08f, r * 0.25f); lineTo(r * 0.82f, r * 0.3f); lineTo(r * 0.92f, r * 0.55f); lineTo(r * 0.55f, r * 0.6f)
            quadTo(0f, r * 0.98f, -r * 0.55f, r * 0.6f)
            lineTo(-r * 0.92f, r * 0.55f); lineTo(-r * 0.82f, r * 0.3f); lineTo(-r * 1.08f, r * 0.25f); close()
        }
        fillPath(face, fur, ol)
        fill.alpha = 255
        fillPath(android.graphics.Path().apply { moveTo(-r * 0.16f, -r * 0.93f); lineTo(0f, -r * 0.45f); lineTo(r * 0.16f, -r * 0.93f); close() }, 0xCCF4ECE1.toInt())
        // 주둥이
        oval(0f, r * 0.38f, r * 0.48f, r * 0.36f, cream)
        // 눈: 왼쪽 찡긋(>), 오른쪽 동글 + 눈썹
        val ey = -r * 0.12f
        line.color = ink; line.strokeWidth = r * 0.09f
        c.drawPath(android.graphics.Path().apply { moveTo(-r * 0.55f, ey - r * 0.12f); lineTo(-r * 0.32f, ey); lineTo(-r * 0.55f, ey + r * 0.12f) }, line)
        fill.color = ink; c.drawCircle(r * 0.42f, ey, r * 0.15f, fill)
        fill.color = android.graphics.Color.WHITE; c.drawCircle(r * 0.37f, ey - r * 0.05f, r * 0.055f, fill)
        line.strokeWidth = r * 0.06f
        c.drawPath(android.graphics.Path().apply { moveTo(r * 0.25f, ey - r * 0.3f); quadTo(r * 0.42f, ey - r * 0.42f, r * 0.6f, ey - r * 0.28f) }, line)
        // 볼 홍조
        for (sx in listOf(-1f, 1f)) oval(sx * r * 0.62f, r * 0.2f, r * 0.17f, r * 0.09f, 0x80FF7896.toInt())
        // 코
        oval(0f, r * 0.2f, r * 0.14f, r * 0.095f, ink)
        fill.color = 0x99FFFFFF.toInt(); c.drawCircle(-r * 0.04f, r * 0.17f, r * 0.03f, fill)
        // 입 + 혀 메롱
        line.color = ink; line.strokeWidth = r * 0.05f
        c.drawPath(android.graphics.Path().apply { moveTo(-r * 0.24f, r * 0.42f); quadTo(0f, r * 0.6f, r * 0.24f, r * 0.42f) }, line)
        c.save(); c.translate(0f, r * 0.5f); c.rotate(deg(wag))
        val l = r * 0.55f * tl; val w = r * 0.2f
        val tongue = android.graphics.Path().apply {
            moveTo(-w, 0f); lineTo(-w, l * 0.6f); quadTo(-w, l + w * 0.4f, 0f, l + w * 0.4f); quadTo(w, l + w * 0.4f, w, l * 0.6f); lineTo(w, 0f); close()
        }
        fillPath(tongue, 0xFFFF6F91.toInt(), 0xFFC2405F.toInt())
        line.color = 0xFFD9506F.toInt(); line.strokeWidth = r * 0.035f
        c.drawLine(0f, l * 0.15f, 0f, l * 0.7f, line)
        c.restore()
        c.restore()
        // 말풍선 "메롱~"
        if (t > 0.25f) {
            val b = min(1f, (t - 0.25f) / 0.25f)
            val bob = sin(t * 7f) * r * 0.06f
            c.save(); c.translate(x + r * 0.95f, y - r * 1.25f + bob); c.scale(b, b); c.rotate(deg(-0.12f))
            val tp = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                textSize = r * 0.42f; typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                textAlign = android.graphics.Paint.Align.CENTER; color = 0xFFE53950.toInt()
            }
            val bw = tp.measureText(word) + r * 0.4f; val bh = r * 0.6f
            fill.color = android.graphics.Color.WHITE
            c.drawRoundRect(android.graphics.RectF(-bw / 2, -bh / 2, bw / 2, bh / 2), bh / 2, bh / 2, fill)
            c.drawPath(android.graphics.Path().apply { moveTo(-bw * 0.25f, bh / 2 - 2); lineTo(-bw * 0.4f, bh / 2 + r * 0.22f); lineTo(-bw * 0.05f, bh / 2 - 2); close() }, fill)
            c.drawText(word, 0f, tp.textSize * 0.36f, tp)
            c.restore()
        }
    }
}
