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
import com.wolfgames.app.ui.Text
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
import kotlin.math.sin
import kotlin.math.cos
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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
 * 늑대 돌리기 그림: 엎드린 옆모습 늑대 (늑대달리기 늑대와 같은 그림체). 코끝이 위(-y)를 가리킴.
 * open 0~1 = 입 벌림, bite = 꽉 문 뒤 표정. (웹 spinWolf 와 같은 좌표)
 */
private fun DrawScope.drawSpinWolf(r: Float, open: Float, bite: Boolean) {
    val fur = 0xFF9AA5B4.toInt(); val dark = 0xFF7D8899.toInt(); val bel = 0xFFFFF6E8.toInt()
    val ol = 0x66000000; val ink = 0xFF2B2B33.toInt(); val pink = 0xFFF6AFC0.toInt()
    val s = r
    val sw = max(1.5f, s * 0.02f)
    drawIntoCanvas { cc ->
        val c = cc.nativeCanvas
        val fill = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { style = android.graphics.Paint.Style.FILL }
        val line = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            style = android.graphics.Paint.Style.STROKE; strokeJoin = android.graphics.Paint.Join.ROUND; strokeCap = android.graphics.Paint.Cap.ROUND
        }
        fun outline(p: android.graphics.Path) { line.color = ol; line.strokeWidth = sw; c.drawPath(p, line) }
        fun oval(x: Float, y: Float, rx: Float, ry: Float, color: Int, stroke: Boolean = false, rot: Float = 0f) {
            c.save(); c.translate(x * s, y * s); c.rotate(Math.toDegrees(rot.toDouble()).toFloat())
            val rect = android.graphics.RectF(-rx * s, -ry * s, rx * s, ry * s)
            fill.color = color; c.drawOval(rect, fill)
            if (stroke) { line.color = ol; line.strokeWidth = sw; c.drawOval(rect, line) }
            c.restore()
        }
        fun poly(vararg pts: Float, color: Int, stroke: Boolean = false) {
            val p = android.graphics.Path()
            p.moveTo(pts[0] * s, pts[1] * s)
            var i = 2
            while (i < pts.size) { p.lineTo(pts[i] * s, pts[i + 1] * s); i += 2 }
            p.close()
            fill.color = color; c.drawPath(p, fill)
            if (stroke) outline(p)
        }
        fun circle(x: Float, y: Float, rr: Float, color: Int, stroke: Boolean = false) {
            fill.color = color; c.drawCircle(x * s, y * s, rr * s, fill)
            if (stroke) { line.color = ol; line.strokeWidth = sw; c.drawCircle(x * s, y * s, rr * s, line) }
        }

        c.save()
        c.rotate(-90f) // u(→)=앞(코), v(↓)=배 쪽
        // 꼬리: 등 쪽으로 동그랗게 말린 복슬 꼬리 (가리키는 코끝이랑 헷갈리지 않게)
        val tail = android.graphics.Path().apply {
            moveTo(-0.5f * s, 0f)
            cubicTo(-0.86f * s, 0.04f * s, -0.9f * s, -0.34f * s, -0.64f * s, -0.33f * s)
            quadTo(-0.5f * s, -0.31f * s, -0.55f * s, -0.19f * s)
        }
        line.color = ol; line.strokeWidth = s * 0.15f + sw * 2; c.drawPath(tail, line)
        line.color = fur; line.strokeWidth = s * 0.15f; c.drawPath(tail, line)
        fill.color = bel; c.drawCircle(-0.55f * s, -0.19f * s, s * 0.075f, fill)
        // 다리 (엎드려 앞으로 뻗은 자세)
        for (lg in listOf(floatArrayOf(-0.34f, 0.12f, 0.2f, 1f), floatArrayOf(0.12f, 0.12f, 0.24f, 1f), floatArrayOf(-0.26f, 0.15f, 0.2f, 0f), floatArrayOf(0.2f, 0.15f, 0.26f, 0f))) {
            val (u, v, len, dk) = lg.toList()
            line.color = fur; line.alpha = if (dk > 0f) 191 else 255; line.strokeWidth = s * 0.1f
            c.drawLine(u * s, v * s, (u + len) * s, (v + 0.08f) * s, line); line.alpha = 255
            oval(u + len + 0.02f, v + 0.085f, 0.06f, 0.045f, bel, true)
        }
        // 몸통, 배, 등 무늬
        oval(-0.15f, 0f, 0.42f, 0.19f, fur, true); oval(-0.12f, 0.1f, 0.28f, 0.07f, bel); oval(-0.2f, -0.1f, 0.25f, 0.06f, dark)
        // 목 갈기
        poly(0.05f, -0.17f, 0.1f, -0.24f, 0.15f, -0.16f, 0.2f, -0.22f, 0.2f, 0.12f, 0.08f, 0.16f, 0.12f, 0.08f, 0.02f, 0.1f, color = fur, stroke = true)
        // 머리, 귀
        val hx = 0.36f; val hy = -0.07f; val hr = 0.27f
        for (ex in listOf(hx - hr * 0.6f, hx + hr * 0.15f)) {
            poly(ex - hr * 0.35f, hy - hr * 0.55f, ex - hr * 0.05f, hy - hr * 1.5f, ex + hr * 0.38f, hy - hr * 0.6f, color = fur, stroke = true)
            poly(ex - hr * 0.17f, hy - hr * 0.68f, ex - hr * 0.04f, hy - hr * 1.18f, ex + hr * 0.2f, hy - hr * 0.7f, color = pink)
        }
        circle(hx, hy, hr, fur, true)
        poly(hx - hr * 0.5f, hy + hr * 0.75f, hx - hr * 0.2f, hy + hr * 1.25f, hx + hr * 0.05f, hy + hr * 0.85f, hx + hr * 0.3f, hy + hr * 1.15f, hx + hr * 0.45f, hy + hr * 0.7f, color = fur, stroke = true)

        // 주둥이 (경첩 = 주둥이 뒤쪽)
        val jx = hx + hr * 0.5f; val jy = hy + hr * 0.36f; val a = open * 0.75f
        fun hinge(ang: Float, body: () -> Unit) {
            c.save(); c.translate(jx * s, jy * s); c.rotate(Math.toDegrees(ang.toDouble()).toFloat()); c.translate(-jx * s, -jy * s); body(); c.restore()
        }
        if (open > 0.04f) { // 입 안
            poly(jx, jy, jx + cos(-a * 0.45f) * 0.27f, jy + sin(-a * 0.45f) * 0.27f, jx + cos(a * 0.55f) * 0.24f, jy + sin(a * 0.55f) * 0.24f, color = 0xFF8E2440.toInt())
        }
        hinge(a * 0.55f) { // 아래턱
            oval(jx + 0.13f, jy + 0.04f, 0.15f, 0.05f, bel, true)
            if (open > 0.04f) {
                oval(jx + 0.09f, jy + 0.005f, 0.09f, 0.022f, 0xFFE86A86.toInt())
                for (u in listOf(0.1f, 0.16f)) poly(jx + u, jy, jx + u + 0.012f, jy - 0.035f, jx + u + 0.024f, jy, color = 0xFFFFFFFF.toInt())
                poly(jx + 0.22f, jy, jx + 0.238f, jy - 0.07f, jx + 0.256f, jy, color = 0xFFFFFFFF.toInt(), stroke = true)
            }
        }
        hinge(-a * 0.45f) { // 위턱 (코)
            val p = android.graphics.Path()
            p.moveTo((jx - 0.03f) * s, (jy - 0.1f) * s)
            p.quadTo((jx + 0.15f) * s, (jy - 0.15f) * s, (jx + 0.29f) * s, (jy - 0.08f) * s)
            p.quadTo((jx + 0.34f) * s, (jy - 0.02f) * s, (jx + 0.28f) * s, (jy + 0.005f) * s)
            p.lineTo((jx - 0.02f) * s, (jy + 0.005f) * s); p.close()
            fill.color = bel; c.drawPath(p, fill); outline(p)
            poly(jx + 0.19f, jy, jx + 0.208f, jy + 0.075f, jx + 0.226f, jy, color = 0xFFFFFFFF.toInt(), stroke = true) // 송곳니
            if (open > 0.04f) for (u in listOf(0.08f, 0.13f)) poly(jx + u, jy, jx + u + 0.012f, jy + 0.035f, jx + u + 0.024f, jy, color = 0xFFFFFFFF.toInt())
            oval(jx + 0.3f, jy - 0.065f, 0.055f, 0.042f, ink, false, 0.3f) // 코
            oval(jx + 0.288f, jy - 0.08f, 0.014f, 0.01f, 0x99FFFFFF.toInt())
            val nose = android.graphics.Path()
            nose.moveTo((jx + 0.02f) * s, (jy - 0.13f) * s)
            nose.quadTo((jx + 0.1f) * s, (jy - 0.15f) * s, (jx + 0.2f) * s, (jy - 0.115f) * s)
            line.color = fur; line.strokeWidth = s * 0.03f; c.drawPath(nose, line) // 콧등 회색
        }
        // 눈
        val ex = hx + hr * 0.25f; val ey = hy - hr * 0.15f
        if (bite) {
            val p = android.graphics.Path()
            p.moveTo((ex - 0.045f) * s, (ey - 0.045f) * s); p.lineTo((ex + 0.035f) * s, ey * s); p.lineTo((ex - 0.045f) * s, (ey + 0.045f) * s)
            line.color = ink; line.strokeWidth = s * 0.028f; c.drawPath(p, line)
        } else {
            circle(ex, ey, hr * 0.27f, 0xFFFFFFFF.toInt())
            circle(ex + hr * 0.06f, ey, hr * 0.19f, ink)
            circle(ex + hr * 0.01f, ey - hr * 0.07f, hr * 0.07f, 0xFFFFFFFF.toInt())
        }
        oval(hx, hy + hr * 0.4f, hr * 0.2f, hr * 0.12f, 0x8CFF8FA3.toInt()) // 볼
        c.restore()
    }
}

