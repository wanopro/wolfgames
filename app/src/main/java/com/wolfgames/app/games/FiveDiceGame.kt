package com.wolfgames.app.games

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.wolfgames.app.ui.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import com.wolfgames.app.ui.LocalVibe
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.DieView
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.SetupPanel
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.VTextButton
import com.wolfgames.app.ui.WG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/** Playing: 각자 굴리는 중 / Applying: 모두 굴림 → 결과 반영 중 / Win: 패자 결정(게임 끝) */
private enum class FdPhase { Playing, Applying, Win }

/** 한 사람 영역의 상태 */
private enum class SlotState { Waiting, Rolling, Done, Safe }

/**
 * 1-3 5개 주사위
 * 각자 5개로 시작, 자기 영역을 탭하면 자기 주사위만 굴림.
 * 모든 사람이 다 굴리면 한꺼번에 결과 반영:
 * 1 → 아웃, 6 → 시계방향 다음(아직 남은) 사람에게. 주사위가 0개가 되면 탈출.
 * 마지막 한 명이 남을 때까지 계속 → 남은 사람이 패배. (마지막에 동시에 0개면 그 사람들이 공동 패배)
 *
 * 자리 배치(폰을 테이블 가운데에 놓는 기준, 플레이어 번호 = 시계방향 순서)
 *  2명: 아래 P1, 위 P2(뒤집힘)
 *  3명: 아래 P1, 위왼쪽 P2, 위오른쪽 P3 (위쪽 둘은 뒤집힘)
 *  4명: 아래왼쪽 P1, 위왼쪽 P2, 위오른쪽 P3, 아래오른쪽 P4 (위쪽 둘은 뒤집힘)
 */
@Composable
fun FiveDiceGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    var players by rememberSaveable { mutableIntStateOf(2) }
    var started by rememberSaveable { mutableStateOf(false) }

    if (!started) {
        GameScaffold(s.fiveTitle, onBack) {
            SetupPanel(
                description = s.fiveDesc,
                onStart = { started = true },
            ) {
                Stepper(s.players, players, 2..4) { players = it }
            }
        }
    } else {
        BackHandler { started = false }
        FiveDicePlay(players, onExit = { started = false })
    }
}

@Composable
private fun FiveDicePlay(n: Int, onExit: () -> Unit) {
    val counts = remember { mutableStateListOf(*Array(n) { 5 }) }
    val faces = remember { mutableStateListOf(*Array(n) { emptyList<Int>() }) }
    val slots = remember { mutableStateListOf(*Array(n) { SlotState.Waiting }) }
    var phase by remember { mutableStateOf(FdPhase.Playing) }
    var winners by remember { mutableStateOf(emptyList<Int>()) }
    val haptic = LocalVibe.current
    val scope = rememberCoroutineScope()

    /** 모두 굴렸을 때 결과 반영 */
    suspend fun applyResults() {
        if (phase != FdPhase.Playing) return
        phase = FdPhase.Applying
        delay(1800)
        val act = (0 until n).filter { slots[it] != SlotState.Safe }
        fun nextActive(i: Int): Int {
            for (k in 1..n) { val j = (i + k) % n; if (j in act) return j }
            return i
        }
        val ones = faces.map { f -> f.count { it == 1 } }
        val sixes = faces.map { f -> f.count { it == 6 } }
        val add = IntArray(n)
        for (i in act) add[nextActive(i)] += sixes[i]
        for (i in act) counts[i] = counts[i] - ones[i] - sixes[i] + add[i]
        val justSafe = act.filter { counts[it] == 0 }
        for (i in 0 until n) {
            faces[i] = emptyList()
            if (slots[i] != SlotState.Safe) slots[i] = if (i in justSafe) SlotState.Safe else SlotState.Waiting
        }
        val left = (0 until n).filter { slots[it] != SlotState.Safe }
        if (left.size <= 1) {
            winners = if (left.size == 1) left else justSafe
            phase = FdPhase.Win
            haptic.strong()
        } else {
            phase = FdPhase.Playing
            if (justSafe.isNotEmpty()) haptic.tap()
        }
    }

    fun roll(i: Int) {
        if (phase != FdPhase.Playing || slots[i] != SlotState.Waiting) return
        slots[i] = SlotState.Rolling
        haptic.tap()
        scope.launch {
            repeat(12) {
                faces[i] = List(counts[i]) { Random.nextInt(1, 7) }
                delay(60)
            }
            faces[i] = List(counts[i]) { Random.nextInt(1, 7) }
            slots[i] = SlotState.Done
            haptic.tap()
            if (slots.all { it == SlotState.Done || it == SlotState.Safe }) applyResults()
        }
    }

    fun restart() {
        for (i in 0 until n) {
            counts[i] = 5
            faces[i] = emptyList()
            slots[i] = SlotState.Waiting
        }
        winners = emptyList()
        phase = FdPhase.Playing
    }

    val waitingCount = slots.count { it == SlotState.Waiting || it == SlotState.Rolling }
    val activeList = (0 until n).filter { slots[it] != SlotState.Safe }

    Column(Modifier.fillMaxSize().padding(4.dp)) {
        when (n) {
            2 -> {
                PlayerArea(1, n, true, counts[1], faces[1], slots[1], phase, 1 in winners, activeList, Modifier.weight(1f).fillMaxWidth()) { roll(1) }
                CenterBar(phase, winners, waitingCount, onRestart = { restart() }, onExit = onExit)
                PlayerArea(0, n, false, counts[0], faces[0], slots[0], phase, 0 in winners, activeList, Modifier.weight(1f).fillMaxWidth()) { roll(0) }
            }

            3 -> {
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    PlayerArea(1, n, true, counts[1], faces[1], slots[1], phase, 1 in winners, activeList, Modifier.weight(1f).fillMaxHeight()) { roll(1) }
                    PlayerArea(2, n, true, counts[2], faces[2], slots[2], phase, 2 in winners, activeList, Modifier.weight(1f).fillMaxHeight()) { roll(2) }
                }
                CenterBar(phase, winners, waitingCount, onRestart = { restart() }, onExit = onExit)
                PlayerArea(0, n, false, counts[0], faces[0], slots[0], phase, 0 in winners, activeList, Modifier.weight(1f).fillMaxWidth()) { roll(0) }
            }

            else -> {
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    PlayerArea(1, n, true, counts[1], faces[1], slots[1], phase, 1 in winners, activeList, Modifier.weight(1f).fillMaxHeight()) { roll(1) }
                    PlayerArea(2, n, true, counts[2], faces[2], slots[2], phase, 2 in winners, activeList, Modifier.weight(1f).fillMaxHeight()) { roll(2) }
                }
                CenterBar(phase, winners, waitingCount, onRestart = { restart() }, onExit = onExit)
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    PlayerArea(0, n, false, counts[0], faces[0], slots[0], phase, 0 in winners, activeList, Modifier.weight(1f).fillMaxHeight()) { roll(0) }
                    PlayerArea(3, n, false, counts[3], faces[3], slots[3], phase, 3 in winners, activeList, Modifier.weight(1f).fillMaxHeight()) { roll(3) }
                }
            }
        }
    }
}

@Composable
private fun CenterBar(
    phase: FdPhase,
    winners: List<Int>,
    waitingCount: Int,
    onRestart: () -> Unit,
    onExit: () -> Unit,
) {
    val s = LocalStrings.current
    Row(
        Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VTextButton(onClick = onExit) { Text(s.exit, color = WG.SubText) }
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            val text = when (phase) {
                FdPhase.Playing -> s.tapYourArea(waitingCount)
                FdPhase.Applying -> s.allRolled
                FdPhase.Win -> s.fiveLoser(winners.joinToString(", ") { "P${it + 1}" })
            }
            Text(
                text,
                color = WG.Accent,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        if (phase == FdPhase.Win) {
            VTextButton(onClick = onRestart) { Text(s.again, color = WG.Accent, fontWeight = FontWeight.Bold) }
        } else {
            Spacer(Modifier.width(64.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlayerArea(
    i: Int,
    n: Int,
    rotated: Boolean,
    count: Int,
    faces: List<Int>,
    slot: SlotState,
    phase: FdPhase,
    winner: Boolean,
    active: List<Int>,
    modifier: Modifier,
    onTap: () -> Unit,
) {
    val s = LocalStrings.current
    val color = WG.PlayerColors[i]
    val shape = RoundedCornerShape(20.dp)
    val canTap = phase == FdPhase.Playing && slot == SlotState.Waiting
    val safe = slot == SlotState.Safe && !winner
    val bgAlpha = when {
        winner -> 0.4f
        safe -> 0.04f
        canTap -> 0.22f
        else -> 0.08f
    }
    Box(
        modifier
            .padding(4.dp)
            .rotate(if (rotated) 180f else 0f)
            .clip(shape)
            .background(color.copy(alpha = bgAlpha))
            .border(if (canTap) 3.dp else 2.dp, color.copy(alpha = if (canTap) 1f else if (safe) 0.25f else 0.5f), shape)
            .clickable(enabled = canTap, onClick = onTap)
            .padding(10.dp),
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(s.playerN(i + 1), color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                if (winner) "💀" else if (safe) s.fiveSafe else s.diceLeft(count),
                color = if (winner) WG.Win else WG.SubText,
                fontSize = if (winner || safe) 22.sp else 14.sp,
                fontWeight = if (winner || safe) FontWeight.Black else FontWeight.Normal,
            )
            Spacer(Modifier.weight(1f))
            val showing = slot != SlotState.Waiting
            val marked = slot == SlotState.Done
            val values = if (slot == SlotState.Safe) emptyList() else if (showing) faces else List(count) { 0 }
            val dieSize = when {
                values.size <= 6 -> 44.dp
                values.size <= 10 -> 34.dp
                else -> 26.dp
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                values.forEach { v ->
                    val out = marked && v == 1
                    val pass = marked && v == 6
                    DieView(
                        value = v,
                        dieSize = dieSize,
                        alpha = if (out) 0.25f else 1f,
                        border = when {
                            pass -> WG.Accent
                            out -> WG.Win
                            else -> null
                        },
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            val status = when {
                winner -> s.fiveLoserSub
                safe || phase == FdPhase.Win -> ""
                slot == SlotState.Waiting -> s.tapToRoll
                slot == SlotState.Rolling -> s.rolling
                else -> {
                    val ones = faces.count { it == 1 }
                    val sixes = faces.count { it == 6 }
                    var next = i + 1
                    for (k in 1..n) { val j = (i + k) % n; if (j in active) { next = j + 1; break } }
                    s.outPass(ones, next, sixes) +
                        if (phase == FdPhase.Playing) "\n" + s.waitingOthers else ""
                }
            }
            Text(
                status,
                color = if (slot == SlotState.Waiting) color else WG.Text,
                fontSize = if (slot == SlotState.Waiting) 16.sp else 13.sp,
                fontWeight = if (slot == SlotState.Waiting) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
        }
    }
}
