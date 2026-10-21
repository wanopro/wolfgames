package com.wolfgames.app.games

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.PCard
import com.wolfgames.app.ui.PlayingCard
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.ToggleRow
import com.wolfgames.app.ui.VTextButton
import com.wolfgames.app.ui.WG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ===================== Up/Down =====================

enum class UdGuess { Up, Down, Red, Black }
enum class UdResult { Ok, Fail, Same }

/** 판정. 기준 카드가 A·K면 색깔로, 아니면 높낮이로. 같은 숫자는 원샷 */
fun judgeUpDown(base: PCard, next: PCard, g: UdGuess): UdResult {
    if (base.r == 1 || base.r == 13) return if ((g == UdGuess.Red) == next.red) UdResult.Ok else UdResult.Fail
    if (next.r == base.r) return UdResult.Same
    val ok = if (g == UdGuess.Up) next.r > base.r else next.r < base.r
    return if (ok) UdResult.Ok else UdResult.Fail
}

@Composable
fun UpDownGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    var players by rememberSaveable { mutableIntStateOf(4) }
    var stack by rememberSaveable { mutableStateOf(false) }
    var started by rememberSaveable { mutableStateOf(false) }
    if (!started) {
        GameScaffold(s.udTitle, onBack) {
            SetupPanel(s.udDesc, onStart = { started = true }) {
                Stepper(s.players, players, 2..8) { players = it }
                ToggleRow(s.udStackOpt, stack) { stack = it }
            }
        }
    } else {
        BackHandler { started = false }
        UpDownPlay(players, stack, onExit = { started = false })
    }
}

@Composable
private fun UpDownPlay(n: Int, stack: Boolean, onExit: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    val scope = rememberCoroutineScope()
    val deck = remember { PCard.deck() }
    var base by remember { mutableStateOf(deck.removeAt(deck.lastIndex)) }
    var left by remember { mutableIntStateOf(deck.size) }
    var next by remember { mutableStateOf<PCard?>(null) }
    var turn by remember { mutableIntStateOf(0) }
    var pot by remember { mutableIntStateOf(1) }
    var busy by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<UdResult?>(null) }
    var drinker by remember { mutableIntStateOf(0) }
    var drinkCount by remember { mutableIntStateOf(1) }
    val pop = remember { Animatable(1f) }

    fun draw(): PCard {
        if (deck.isEmpty()) deck.addAll(PCard.deck().filter { it != base })
        val c = deck.removeAt(deck.lastIndex)
        left = deck.size
        return c
    }

    fun guess(g: UdGuess) {
        if (busy || result != null) return
        busy = true
        val c = draw()
        next = c
        scope.launch {
            delay(520)
            val r = judgeUpDown(base, c, g)
            drinker = turn
            when (r) {
                UdResult.Ok -> { vibe.tap(); if (stack) pot++ }
                UdResult.Fail -> { vibe.strong(); drinkCount = if (stack) pot else 1 }
                UdResult.Same -> vibe.strong()
            }
            result = r
            busy = false
            pop.snapTo(0.4f)
            pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    fun proceed() {
        val r = result ?: return
        val c = next
        if (r == UdResult.Ok && c != null) base = c else { pot = 1; base = draw() }
        next = null
        result = null
        turn = (turn + 1) % n
    }

    fun reshuffle() {
        if (busy) return
        deck.clear(); deck.addAll(PCard.deck())
        base = draw(); next = null; result = null; pot = 1; turn = 0
    }

    GameScaffold(s.udTitle, onBack = onExit, actions = {
        VTextButton(onClick = { reshuffle() }) { Text(s.shuffle, color = WG.Accent, fontWeight = FontWeight.Bold) }
    }) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(s.playerN(turn + 1), color = WG.PlayerColors[turn % WG.PlayerColors.size], fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text(s.udGuess, color = WG.SubText, fontSize = 14.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill(s.udLeft(left), false)
                if (stack) Pill(s.udPot(pot), true)
            }
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp), verticalAlignment = Alignment.CenterVertically) {
                PlayingCard(base, 128.dp)
                PlayingCard(next, 128.dp)
            }
            Spacer(Modifier.weight(1f))
            Box(Modifier.fillMaxWidth().heightIn(min = 84.dp), contentAlignment = Alignment.Center) {
                result?.let { r ->
                    val (t, col) = when (r) {
                        UdResult.Ok -> "⭕ ${s.udSuccess}" to Color(0xFF69F0AE)
                        UdResult.Fail -> "❌ ${s.udFail}" to WG.Win
                        UdResult.Same -> "🔥 ${s.udSame}" to WG.Win
                    }
                    val sub = when (r) {
                        UdResult.Ok -> if (stack) s.udPot(pot) else ""
                        UdResult.Fail -> "${s.playerN(drinker + 1)} · ${s.udDrink(drinkCount)}"
                        UdResult.Same -> s.playerN(drinker + 1)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(t, color = col, fontSize = 36.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.scale(pop.value))
                        if (sub.isNotEmpty()) Text(sub, color = WG.SubText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            if (result != null) {
                BigButton(s.udNext, Modifier.fillMaxWidth()) { proceed() }
            } else {
                val ak = base.r == 1 || base.r == 13
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (ak) {
                        BigButton("♥ ${s.udRed}", Modifier.weight(1f), enabled = !busy, color = Color(0xFFE53935), contentColor = Color.White) { guess(UdGuess.Red) }
                        BigButton("♠ ${s.udBlack}", Modifier.weight(1f), enabled = !busy, color = Color(0xFF222222), contentColor = Color.White) { guess(UdGuess.Black) }
                    } else {
                        BigButton("▲ ${s.udUp}", Modifier.weight(1f), enabled = !busy, color = Color(0xFF43A047), contentColor = Color.White) { guess(UdGuess.Up) }
                        BigButton("▼ ${s.udDown}", Modifier.weight(1f), enabled = !busy, color = Color(0xFFE53935), contentColor = Color.White) { guess(UdGuess.Down) }
                    }
                }
            }
        }
    }
}

@Composable
private fun Pill(text: String, hot: Boolean) {
    Text(
        text,
        color = if (hot) Color.Black else WG.SubText,
        fontSize = 14.sp,
        fontWeight = if (hot) FontWeight.ExtraBold else FontWeight.Normal,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (hot) WG.Accent else WG.Surface)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

// ===================== 호치민 =====================

/** 카드 점수: A=1, 2~9, 10·J·Q·K=10 */
fun hcmVal(c: PCard) = minOf(c.r, 10)

/** 내 숫자(뒷자리): 10·J·Q·K → 0 */
fun hcmDigit(c: PCard) = hcmVal(c) % 10

/**
 * 호치민 (2~4명)
 * 각자 카드 1장 = 내 숫자. 바닥 카드 2장 중 하나를 골라 새 카드로 바꾸고,
 * 두 장 합의 뒷자리가 내 숫자와 같은 사람은 벌주. 덱이 떨어지면 남은 카드를 다시 섞음.
 */
@Composable
fun HoChiMinhGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    var players by rememberSaveable { mutableIntStateOf(3) }
    var started by rememberSaveable { mutableStateOf(false) }
    if (!started) {
        GameScaffold(s.hcmTitle, onBack) {
            SetupPanel(s.hcmDesc, onStart = { started = true }) {
                Stepper(s.players, players, 2..4) { players = it }
            }
        }
    } else {
        BackHandler { started = false }
        HcmPlay(players, onExit = { started = false })
    }
}

@Composable
private fun HcmPlay(n: Int, onExit: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    val scope = rememberCoroutineScope()
    val deck = remember { mutableListOf<PCard>() }
    val mine = remember { mutableStateListOf<PCard>() }
    val field = remember { mutableStateListOf<PCard>() }
    val flips = remember { mutableStateListOf(0, 0) }
    var turn by remember { mutableIntStateOf(0) }
    var busy by remember { mutableStateOf(false) }
    var hit by remember { mutableStateOf(emptyList<Int>()) }
    var sum by remember { mutableIntStateOf(-1) }
    var round by remember { mutableIntStateOf(0) }

    fun deal() {
        val d = PCard.deck()
        mine.clear(); repeat(n) { mine.add(d.removeAt(d.lastIndex)) }
        field.clear(); repeat(2) { field.add(d.removeAt(d.lastIndex)) }
        deck.clear(); deck.addAll(d)
        turn = 0; busy = false; hit = emptyList(); sum = -1; round++
    }
    LaunchedEffect(Unit) { if (mine.isEmpty()) deal() }

    fun pick(k: Int) {
        if (busy) return
        busy = true
        vibe.tap()
        if (deck.isEmpty()) deck.addAll(PCard.deck().filter { it !in mine && it !in field })
        field[k] = deck.removeAt(deck.lastIndex)
        flips[k] = flips[k] + 1
        hit = emptyList(); sum = -1
        scope.launch {
            delay(480)
            val total = hcmVal(field[0]) + hcmVal(field[1])
            val d = total % 10
            hit = (0 until n).filter { hcmDigit(mine[it]) == d }
            sum = total
            if (hit.isNotEmpty()) vibe.strong() else vibe.tap()
            turn = (turn + 1) % n
            busy = false
        }
    }

    @Composable
    fun Area(i: Int, rotated: Boolean, modifier: Modifier) {
        val color = WG.PlayerColors[i]
        val bad = i in hit
        val my = turn == i && !busy
        val shape = RoundedCornerShape(20.dp)
        Column(
            modifier
                .padding(4.dp)
                .rotate(if (rotated) 180f else 0f)
                .clip(shape)
                .background(color.copy(alpha = if (bad) 0.4f else if (my) 0.19f else 0.08f))
                .border(if (my) 3.dp else 2.dp, color.copy(alpha = if (my || bad) 1f else 0.45f), shape)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(s.playerN(i + 1) + if (my) " 👈" else "", color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (i < mine.size) PlayingCard(mine[i], if (n == 4) 58.dp else 66.dp)
            Text(if (bad) "💀 🍺" else " ", fontSize = 18.sp)
        }
    }

    @Composable
    fun Mid() {
        Column(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                VTextButton(onClick = onExit) { Text(s.exit, color = WG.SubText) }
                Text(s.playerN(turn + 1), color = WG.PlayerColors[turn], fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                VTextButton(onClick = { deal() }) { Text(s.newGame, color = WG.Accent, fontWeight = FontWeight.Bold) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                if (field.size == 2) {
                    PlayingCard(field[0], 74.dp, Modifier.clickable { pick(0) }, highlight = WG.Accent, flipKey = flips[0] + round * 1000)
                    Text("+", color = WG.SubText, fontSize = 22.sp)
                    PlayingCard(field[1], 74.dp, Modifier.clickable { pick(1) }, highlight = WG.Accent, flipKey = flips[1] + round * 1000)
                }
            }
            val msg = if (sum < 0) s.hcmPick else s.hcmSum(sum, sum % 10) + "\n" +
                if (hit.isNotEmpty()) s.hcmPenalty(hit.joinToString(", ") { "P${it + 1}" }) else s.hcmNone
            Text(
                msg,
                color = if (hit.isNotEmpty()) WG.Win else WG.Text,
                fontSize = if (hit.isNotEmpty()) 18.sp else 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }

    Column(Modifier.fillMaxSize().padding(4.dp)) {
        when (n) {
            2 -> {
                Area(1, true, Modifier.weight(1f).fillMaxWidth())
                Mid()
                Area(0, false, Modifier.weight(1f).fillMaxWidth())
            }
            3 -> {
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    Area(1, true, Modifier.weight(1f).fillMaxHeight())
                    Area(2, true, Modifier.weight(1f).fillMaxHeight())
                }
                Mid()
                Area(0, false, Modifier.weight(1f).fillMaxWidth())
            }
            else -> {
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    Area(1, true, Modifier.weight(1f).fillMaxHeight())
                    Area(2, true, Modifier.weight(1f).fillMaxHeight())
                }
                Mid()
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    Area(0, false, Modifier.weight(1f).fillMaxHeight())
                    Area(3, false, Modifier.weight(1f).fillMaxHeight())
                }
            }
        }
    }
}
