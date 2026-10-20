package com.wolfgames.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.games.CrocodileGame
import com.wolfgames.app.games.FiveDiceGame
import com.wolfgames.app.games.LadderGame
import com.wolfgames.app.games.RollDiceGame
import com.wolfgames.app.games.RouletteGame
import com.wolfgames.app.games.ShutBoxGame
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.MenuCard
import com.wolfgames.app.ui.WG
import com.wolfgames.app.ui.WolfTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 술자리에서 화면이 꺼지지 않도록
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            WolfTheme { App() }
        }
    }
}

enum class Screen { Home, DiceMenu, RollDice, ShutBox, FiveDice, Croc, Ladder, Roulette }

@Composable
fun App() {
    var screen by rememberSaveable { mutableStateOf(Screen.Home) }
    val back: () -> Unit = {
        screen = when (screen) {
            Screen.RollDice, Screen.ShutBox, Screen.FiveDice -> Screen.DiceMenu
            else -> Screen.Home
        }
    }
    BackHandler(enabled = screen != Screen.Home, onBack = back)

    Box(Modifier.fillMaxSize().background(WG.Bg).systemBarsPadding()) {
        when (screen) {
            Screen.Home -> HomeScreen { screen = it }
            Screen.DiceMenu -> DiceMenuScreen(onBack = back) { screen = it }
            Screen.RollDice -> RollDiceGame(onBack = back)
            Screen.ShutBox -> ShutBoxGame(onBack = back)
            Screen.FiveDice -> FiveDiceGame(onBack = back)
            Screen.Croc -> CrocodileGame(onBack = back)
            Screen.Ladder -> LadderGame(onBack = back)
            Screen.Roulette -> RouletteGame(onBack = back)
        }
    }
}

@Composable
fun HomeScreen(onSelect: (Screen) -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(12.dp))
        Text("🐺", fontSize = 56.sp)
        Text("울프 게임즈", color = WG.Accent, fontSize = 36.sp, fontWeight = FontWeight.Black)
        Text("술자리 게임 모음", color = WG.SubText, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        MenuCard("🎲", "주사위", "주사위 굴리기 · 숫자 뒤집기 · 5개 주사위") { onSelect(Screen.DiceMenu) }
        MenuCard("🐊", "악어이빨", "20개 중 단 하나의 당첨 이빨") { onSelect(Screen.Croc) }
        MenuCard("🪜", "사다리게임", "인원과 당첨 개수를 정해서") { onSelect(Screen.Ladder) }
        MenuCard("🎡", "돌림판", "칸 수와 당첨 개수를 정해서") { onSelect(Screen.Roulette) }
    }
}

@Composable
fun DiceMenuScreen(onBack: () -> Unit, onSelect: (Screen) -> Unit) {
    GameScaffold("주사위", onBack) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            MenuCard("👆", "주사위 굴리기", "여러 명이 동시에 손가락으로") { onSelect(Screen.RollDice) }
            MenuCard("🔢", "숫자 뒤집기 잭팟", "마지막 숫자를 뒤집으면 승리") { onSelect(Screen.ShutBox) }
            MenuCard("🎲", "5개 주사위", "1은 아웃, 6은 옆 사람에게") { onSelect(Screen.FiveDice) }
        }
    }
}
