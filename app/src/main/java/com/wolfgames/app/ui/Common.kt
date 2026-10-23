package com.wolfgames.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings

/**
 * 주사위 한 개를 그린다. value 가 1~6 이 아니면 눈 없는 빈 주사위.
 */
fun DrawScope.drawDie(
    at: Offset,
    dieSize: Float,
    value: Int,
    body: Color = Color.White,
    border: Color? = null,
) {
    val half = dieSize / 2f
    val topLeft = Offset(at.x - half, at.y - half)
    val corner = CornerRadius(dieSize * 0.18f, dieSize * 0.18f)
    drawRoundRect(color = body, topLeft = topLeft, size = Size(dieSize, dieSize), cornerRadius = corner)
    if (border != null) {
        drawRoundRect(
            color = border,
            topLeft = topLeft,
            size = Size(dieSize, dieSize),
            cornerRadius = corner,
            style = Stroke(width = dieSize * 0.08f),
        )
    }
    if (value !in 1..6) return
    val r = dieSize * 0.09f
    val o = dieSize * 0.26f
    val pts: List<Pair<Float, Float>> = when (value) {
        1 -> listOf(0f to 0f)
        2 -> listOf(-1f to -1f, 1f to 1f)
        3 -> listOf(-1f to -1f, 0f to 0f, 1f to 1f)
        4 -> listOf(-1f to -1f, 1f to -1f, -1f to 1f, 1f to 1f)
        5 -> listOf(-1f to -1f, 1f to -1f, 0f to 0f, -1f to 1f, 1f to 1f)
        else -> listOf(-1f to -1f, 1f to -1f, -1f to 0f, 1f to 0f, -1f to 1f, 1f to 1f)
    }
    val pipColor = if (value == 1) Color(0xFFE53935) else Color(0xFF1A1A1A)
    val pipR = if (value == 1) r * 1.5f else r
    for ((dx, dy) in pts) {
        drawCircle(color = pipColor, radius = pipR, center = Offset(at.x + dx * o, at.y + dy * o))
    }
}

@Composable
fun DieView(
    value: Int,
    dieSize: Dp,
    modifier: Modifier = Modifier,
    border: Color? = null,
    alpha: Float = 1f,
) {
    Canvas(modifier.size(dieSize).alpha(alpha)) {
        drawDie(at = center, dieSize = this.size.minDimension * 0.94f, value = value, border = border)
    }
}

/** 상단 뒤로가기 + 제목이 있는 기본 화면 틀 */
@Composable
fun GameScaffold(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val vibe = LocalVibe.current
    Column(Modifier.fillMaxSize().background(WG.Bg)) {
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { vibe.tap(); onBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = LocalStrings.current.back, tint = WG.Text)
            }
            Text(
                title,
                color = WG.Text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            actions()
        }
        Box(Modifier.fillMaxWidth().weight(1f), content = content)
    }
}

@Composable
fun BigButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = WG.Accent,
    contentColor: Color = Color.Black,
    onClick: () -> Unit,
) {
    val vibe = LocalVibe.current
    Button(
        onClick = { vibe.tap(); onClick() },
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = contentColor),
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

/** - 숫자 + 형태의 설정 줄 */
@Composable
fun Stepper(label: String, value: Int, range: IntRange, onChange: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WG.Surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = WG.Text, fontSize = 18.sp, modifier = Modifier.weight(1f))
        StepButton("−", enabled = value > range.first) { onChange(value - 1) }
        Text(
            "$value",
            color = WG.Accent,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(56.dp),
        )
        StepButton("+", enabled = value < range.last) { onChange(value + 1) }
    }
}

@Composable
private fun StepButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    val vibe = LocalVibe.current
    Box(
        Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (enabled) WG.Surface2 else WG.Surface)
            .clickable(enabled = enabled) { vibe.tap(); onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = if (enabled) WG.Text else WG.SubText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MenuCard(emoji: String, title: String, subtitle: String, onClick: () -> Unit) {
    val vibe = LocalVibe.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(WG.Surface)
            .clickable { vibe.tap(); onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, fontSize = 40.sp)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = WG.Text, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = WG.SubText, fontSize = 14.sp)
        }
        Text("›", color = WG.SubText, fontSize = 32.sp)
    }
}

/** 게임 시작 전 설정 화면 */
@Composable
fun SetupPanel(
    description: String,
    onStart: () -> Unit,
    startText: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(description, color = WG.SubText, fontSize = 15.sp, lineHeight = 22.sp)
        content()
        Spacer(Modifier.height(8.dp))
        BigButton(startText ?: LocalStrings.current.start, Modifier.fillMaxWidth(), onClick = onStart)
    }
}

/** 결과 팝업 (화면 위에 덮임) */
@Composable
fun BoxScope.ResultOverlay(
    title: String,
    subtitle: String?,
    primary: String,
    onPrimary: () -> Unit,
    secondary: String? = null,
    onSecondary: () -> Unit = {},
) {
    val blocker = remember { MutableInteractionSource() }
    Box(
        Modifier
            .matchParentSize()
            .background(Color(0xCC000000))
            .clickable(interactionSource = blocker, indication = null) {},
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(WG.Surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                title,
                color = WG.Accent,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            if (subtitle != null) {
                Text(subtitle, color = WG.SubText, fontSize = 16.sp, textAlign = TextAlign.Center)
            }
            BigButton(primary, Modifier.fillMaxWidth(), onClick = onPrimary)
            if (secondary != null) {
                BigButton(
                    secondary,
                    Modifier.fillMaxWidth(),
                    color = WG.Surface2,
                    contentColor = WG.Text,
                    onClick = onSecondary,
                )
            }
        }
    }
}

/** 켜기/끄기 스위치 설정 줄 */
@Composable
fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    val vibe = LocalVibe.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WG.Surface)
            .clickable { vibe.tap(); onChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = WG.Text, fontSize = 18.sp, modifier = Modifier.weight(1f))
        Box(
            Modifier
                .size(width = 54.dp, height = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (checked) WG.Accent else WG.Surface2)
                .padding(3.dp),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(Modifier.size(26.dp).clip(CircleShape).background(Color.White))
        }
    }
}

/** 여러 개 중 하나 고르기 (폭탄돌리기 방향, 주사위 대결 계산 방법) */
@Composable
fun ChoiceRow(label: String, options: List<String>, selected: Int, onChange: (Int) -> Unit) {
    val vibe = LocalVibe.current
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(WG.Surface).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(label, color = WG.Text, fontSize = 18.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEachIndexed { i, o ->
                val on = i == selected
                Box(
                    Modifier.weight(1f).height(44.dp).clip(RoundedCornerShape(12.dp)).background(if (on) WG.Accent else WG.Surface2)
                        .clickable { vibe.tap(); onChange(i) },
                    contentAlignment = Alignment.Center,
                ) { Text(o, color = if (on) Color.Black else WG.Text, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1) }
            }
        }
    }
}
