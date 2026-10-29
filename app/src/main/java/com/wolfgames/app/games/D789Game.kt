package com.wolfgames.app.games

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.ToggleRow
import com.wolfgames.app.ui.WG
import com.wolfgames.app.ui.drawDie
import com.wolfgames.app.ui.runFor
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/** 789 결과 종류 */
enum class R789 { Top, Half, Shot, Rev, Pass }

/** 두 눈의 합 뒷자리로 판정: 7 첨잔 / 8 반 / 9 원샷 / 6 순서 반대(옵션) / 나머지 통과 */
fun judge789(sum: Int, rev: Boolean): R789 = when (sum % 10) {
    7 -> R789.Top
    8 -> R789.Half
    9 -> R789.Shot
    6 -> if (rev) R789.Rev else R789.Pass
    else -> R789.Pass
}

/**
 * 1-4 789
 * 차례대로 주사위 2개를 굴려 합의 뒷자리로 벌칙을 정함. 굴린 뒤 다음 사람 차례.
 * 6이 나오면(옵션) 순서 방향이 반대로 바뀜.
 */
@Composable
fun D789Game(onBack: () -> Unit) {
    val s = LocalStrings.current
    var players by rememberSaveable { mutableIntStateOf(4) }
    var rev by rememberSaveable { mutableStateOf(true) }
    var started by rememberSaveable { mutableStateOf(false) }
    if (!started) {
        GameScaffold(s.d789Title, onBack) {
            SetupPanel(s.d789Desc, onStart = { started = true }) {
                Stepper(s.players, players, 2..8) { players = it }
                ToggleRow(s.d789RevOpt, rev) { rev = it }
            }
        }
    } else {
        BackHandler { started = false }
        GameScaffold(s.d789Title, onBack = { started = false }) { D789Play(players, rev) }
    }
}

@Composable
private fun D789Play(n: Int, rev: Boolean) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    val scope = rememberCoroutineScope()
    var turn by remember { mutableIntStateOf(0) }
    var dir by remember { mutableIntStateOf(1) }
    var lastRoller by remember { mutableIntStateOf(-1) }
    var d1 by remember { mutableIntStateOf(Random.nextInt(1, 7)) }
    var d2 by remember { mutableIntStateOf(Random.nextInt(1, 7)) }
    var p by remember { mutableFloatStateOf(1f) }
    var rolling by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<R789?>(null) }
    val pop = remember { Animatable(1f) }

    fun roll() {
        if (rolling) return
        rolling = true
        result = null
        vibe.tap()
        val roller = turn
        lastRoller = roller
        val f1 = Random.nextInt(1, 7)
        val f2 = Random.nextInt(1, 7)
        scope.launch {
            var lastSwap = 0f
            runFor(1100) { u ->
                p = u
                if (u < 0.92f && u - lastSwap > 0.065f) { lastSwap = u; d1 = Random.nextInt(1, 7); d2 = Random.nextInt(1, 7) }
                if (u >= 0.92f) { d1 = f1; d2 = f2 }
            }
            d1 = f1; d2 = f2
            val r = judge789(f1 + f2, rev)
            if (r == R789.Pass) vibe.tap() else vibe.strong()
            if (r == R789.Rev) dir = -dir
            result = r
            turn = ((turn + dir) % n + n) % n
            rolling = false
            pop.snapTo(0.4f)
            pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    val tap = remember { MutableInteractionSource() }
    Column(
        Modifier.fillMaxSize().clickable(interactionSource = tap, indication = null) { roll() }.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val c = WG.PlayerColors[turn % WG.PlayerColors.size]
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = c)) { append(s.playerN(turn + 1)) }
                withStyle(SpanStyle(color = WG.SubText)) { append(if (dir == 1) "  ↻" else "  ↺") }
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            if (lastRoller >= 0) "${s.playerN(lastRoller + 1)} → ${s.playerN(turn + 1)}" else " ",
            color = WG.SubText,
            fontSize = 14.sp,
        )
        val border = if (!rolling && lastRoller >= 0) WG.PlayerColors[lastRoller % WG.PlayerColors.size] else null
        Canvas(Modifier.weight(1f).fillMaxWidth()) {
            val sz = min(110.dp.toPx(), size.width * 0.3f)
            val cy = size.height * 0.55f
            listOf(-1f to d1, 1f to d2).forEachIndexed { i, (side, v) ->
                val x = size.width / 2 + side * sz * 0.75f
                val bounce = if (rolling) abs(sin(p * PI.toFloat() * (3f + i * 0.5f))) * (1 - p) * size.height * 0.35f else 0f
                val ang = if (rolling) (1 - p) * (1 - p) * 180f * (5 + i) * side else 0f
                val sh = 1f - bounce / size.height
                drawOval(Color(0x59000000), Offset(x - sz * 0.42f * sh, cy + sz * 0.54f), Size(sz * 0.84f * sh, sz * 0.16f))
                rotate(ang, pivot = Offset(x, cy - bounce)) {
                    drawDie(Offset(x, cy - bounce), sz, v, border = border)
                }
            }
        }
        Box(Modifier.fillMaxWidth().heightIn(min = 100.dp), contentAlignment = Alignment.Center) {
            result?.let { r ->
                val (emo, text, col) = when (r) {
                    R789.Top -> Triple("🍶", s.d789Top, WG.Accent)
                    R789.Half -> Triple("🍺", s.d789Half, Color(0xFFFF9800))
                    R789.Shot -> Triple("🔥", s.d789Shot, WG.Win)
                    R789.Rev -> Triple("🔄", s.d789Rev, Color(0xFF448AFF))
                    R789.Pass -> Triple("👉", s.d789Pass, WG.SubText)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$emo $text", color = col, fontSize = 38.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, lineHeight = 44.sp, modifier = Modifier.scale(pop.value))
                    Text(s.d789Sum(d1 + d2), color = WG.SubText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Text(if (rolling) " " else s.d789Tap, color = WG.SubText, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
    }
}
