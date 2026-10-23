package com.wolfgames.app.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.wolfgames.app.ui.LocalVibe
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.VTextButton
import com.wolfgames.app.ui.WG
import com.wolfgames.app.ui.drawDie
import com.wolfgames.app.ui.Ctx
import com.wolfgames.app.ui.TAU
import com.wolfgames.app.ui.die
import com.wolfgames.app.ui.text
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val HOLD_MS = 500L
private const val TAP_MS = 300L
private const val DOUBLE_TAP_GAP_MS = 400L

private data class ActiveTouch(
    val pos: Offset,
    val start: Long,
    val color: Color,
    val value: Int,
    val ready: Boolean = false,
)

private data class PlacedDie(val pos: Offset, val value: Int, val color: Color)

/**
 * 1-1 주사위 굴리기
 * - 여러 손가락 동시 터치, 손가락마다 다른 색 원
 * - 0.5초 이상 누르면 원 아래 주사위가 정해지고, 손을 떼면 원이 사라지고 주사위 공개
 * - 더블탭(또는 상단 초기화)으로 리셋
 */
@Composable
fun RollDiceGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    val active = remember { mutableStateMapOf<PointerId, ActiveTouch>() }
    val placed = remember { mutableStateListOf<PlacedDie>() }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var lastTapTime by remember { mutableLongStateOf(0L) }
    var lastTapPos by remember { mutableStateOf(Offset.Zero) }
    val haptic = LocalVibe.current
    val density = LocalDensity.current
    val dieSizePx = with(density) { 64.dp.toPx() }
    val circleR = with(density) { 50.dp.toPx() }
    val ringW = with(density) { 6.dp.toPx() }
    val doubleTapDist = with(density) { 60.dp.toPx() }

    fun pickColor(): Color {
        val used = active.values.map { it.color } + placed.map { it.color }
        return WG.PlayerColors.firstOrNull { it !in used } ?: WG.PlayerColors.random()
    }

    // 매 프레임 시간 갱신 + 2초 도달 시 진동
    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { }
            val t = System.currentTimeMillis()
            now = t
            var buzz = false
            for ((id, a) in active.toMap()) {
                if (!a.ready && t - a.start >= HOLD_MS) {
                    active[id] = a.copy(ready = true)
                    buzz = true
                }
            }
            if (buzz) haptic.strong()
        }
    }

    GameScaffold(
        title = s.rollTitle,
        onBack = onBack,
        actions = {
            VTextButton(onClick = { placed.clear() }) { Text(s.reset, color = WG.Accent) }
        },
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val t = System.currentTimeMillis()
                            for (change in event.changes) {
                                val id = change.id
                                when {
                                    change.changedToDown() -> {
                                        haptic.tap()
                                        active[id] = ActiveTouch(
                                            pos = change.position,
                                            start = t,
                                            color = pickColor(),
                                            value = Random.nextInt(1, 7),
                                        )
                                    }

                                    change.changedToUp() -> {
                                        val a = active.remove(id)
                                        if (a != null) {
                                            val held = t - a.start
                                            if (held >= HOLD_MS) {
                                                placed.add(PlacedDie(a.pos, a.value, a.color))
                                            } else if (held < TAP_MS) {
                                                val isDouble = t - lastTapTime < DOUBLE_TAP_GAP_MS &&
                                                    (a.pos - lastTapPos).getDistance() < doubleTapDist
                                                if (isDouble) {
                                                    placed.clear()
                                                    haptic.strong()
                                                    lastTapTime = 0L
                                                } else {
                                                    lastTapTime = t
                                                    lastTapPos = a.pos
                                                }
                                            }
                                        }
                                    }

                                    change.pressed -> {
                                        val a = active[id]
                                        if (a != null && a.pos != change.position) {
                                            active[id] = a.copy(pos = change.position)
                                        }
                                    }
                                }
                                change.consume()
                            }
                        }
                    }
                },
        ) {
            Canvas(Modifier.fillMaxSize()) {
                // 이미 나온 주사위 — 2개 이상이면 제일 높은 수·제일 낮은 수 강조 (전부 같으면 강조 안 함)
                val dn = density.density
                drawIntoCanvas { cc ->
                    val x = Ctx(cc.nativeCanvas, dn)
                    val vals = placed.map { it.value }
                    val hi = vals.maxOrNull() ?: 0; val lo = vals.minOrNull() ?: 0
                    val mark = placed.size >= 2 && hi != lo
                    val tt = (now % 1_000_000L) / 1000f; val pulse = 1 + sin(tt * 5) * 0.04f
                    for (d in placed) {
                        val px = d.pos.x / dn; val py = d.pos.y / dn
                        val isHi = mark && d.value == hi; val isLo = mark && d.value == lo
                        val hex = String.format("#%06X", 0xFFFFFF and d.color.toArgb())
                        x.save()
                        if (isHi) {
                            x.beginPath(); x.arc(px, py, 82f, 0f, TAU); x.radialFill(px, py, 20f, 82f, listOf(0f to "rgba(255,200,40,.75)", 1f to "rgba(255,200,40,0)")); x.fill()
                            for (k in 0 until 8) { val a = tt * 1.2f + k * PI.toFloat() / 4; val r2 = 70 + sin(tt * 6 + k) * 6; x.strokeStyle = "rgba(255,215,64,.9)"; x.lineWidth = 4f; x.beginPath(); x.moveTo(px + cos(a) * 58, py + sin(a) * 58); x.lineTo(px + cos(a) * r2, py + sin(a) * r2); x.stroke() }
                        } else if (isLo) {
                            x.beginPath(); x.arc(px, py, 80f, 0f, TAU); x.radialFill(px, py, 20f, 80f, listOf(0f to "rgba(255,61,90,.7)", 1f to "rgba(255,61,90,0)")); x.fill()
                        }
                        x.globalAlpha = if (mark && !isHi && !isLo) 0.4f else 1f
                        x.beginPath(); x.arc(px, py, 47.5f, 0f, TAU); x.fillStyle = hex + "38"; x.fill()
                        x.translate(px, py); val sc = if (isHi || isLo) 1.25f * pulse else if (mark) 0.9f else 1f; x.scale(sc, sc)
                        if (isLo) x.rotate(sin(tt * 9) * 0.08f)
                        x.die(0f, 0f, 64f, d.value, if (isHi) "#FFC107" else if (isLo) "#FF3D5A" else hex)
                        x.restore()
                        if (isHi || isLo) {
                            val lab = if (isHi) s.hiTag else s.loTag
                            x.font(16f); val w = x.measureText(lab) + 22
                            x.fillStyle = if (isHi) "#FFC107" else "#FF3D5A"; x.beginPath(); x.roundRect(px - w / 2, py + 50, w, 28f, 14f); x.fill()
                            x.text(lab, px, py + 64, 16f, if (isHi) "#000" else "#fff")
                        }
                    }
                    x.finish()
                }
                // 누르고 있는 손가락
                for (a in active.values) {
                    val p = ((now - a.start).toFloat() / HOLD_MS).coerceIn(0f, 1f)
                    drawCircle(a.color, radius = circleR, center = a.pos)
                    val ringR = circleR + ringW * 1.6f
                    drawArc(
                        color = if (a.ready) Color.White else a.color.copy(alpha = 0.9f),
                        startAngle = -90f,
                        sweepAngle = 360f * p,
                        useCenter = false,
                        topLeft = Offset(a.pos.x - ringR, a.pos.y - ringR),
                        size = Size(ringR * 2f, ringR * 2f),
                        style = Stroke(width = ringW, cap = StrokeCap.Round),
                    )
                    if (a.ready) {
                        drawCircle(Color.White.copy(alpha = 0.35f), radius = circleR * 0.55f, center = a.pos)
                    }
                }
            }
            if (active.isEmpty() && placed.isEmpty()) {
                Text(
                    s.rollHint,
                    color = WG.SubText,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
            }
        }
    }
}
