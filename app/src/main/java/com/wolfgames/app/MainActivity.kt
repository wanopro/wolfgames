package com.wolfgames.app

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.games.FiveDiceGame
import com.wolfgames.app.games.LadderGame
import com.wolfgames.app.games.RaceGame
import com.wolfgames.app.games.RollDiceGame
import com.wolfgames.app.games.RouletteGame
import com.wolfgames.app.games.ShutBoxGame
import com.wolfgames.app.games.WolfTeethGame
import com.wolfgames.app.i18n.Lang
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.i18n.stringsFor
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.VTextButton
import com.wolfgames.app.ui.Vibe
import com.wolfgames.app.ui.MenuCard
import com.wolfgames.app.ui.WG
import com.wolfgames.app.ui.WolfTheme

private const val PREFS = "wolfgames"
private const val KEY_LANG = "lang"
private const val HOMEPAGE = "https://wolf.community"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 술자리에서 화면이 꺼지지 않도록
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val vibe = Vibe.from(this)
        setContent {
            var langPref by remember { mutableStateOf(prefs.getString(KEY_LANG, Lang.SYSTEM) ?: Lang.SYSTEM) }
            val lang = Lang.resolve(langPref)
            CompositionLocalProvider(LocalStrings provides stringsFor(lang), LocalVibe provides vibe) {
                WolfTheme {
                    App(
                        langPref = langPref,
                        currentLang = lang,
                        onLangChange = {
                            langPref = it
                            prefs.edit().putString(KEY_LANG, it).apply()
                        },
                    )
                }
            }
        }
    }
}

enum class Screen { Home, DiceMenu, RollDice, ShutBox, FiveDice, Croc, Ladder, Roulette, Race, Language }

@Composable
fun App(langPref: String, currentLang: Lang, onLangChange: (String) -> Unit) {
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
            Screen.Home -> HomeScreen(currentLang) { screen = it }
            Screen.DiceMenu -> DiceMenuScreen(onBack = back) { screen = it }
            Screen.RollDice -> RollDiceGame(onBack = back)
            Screen.ShutBox -> ShutBoxGame(onBack = back)
            Screen.FiveDice -> FiveDiceGame(onBack = back)
            Screen.Croc -> WolfTeethGame(onBack = back)
            Screen.Ladder -> LadderGame(onBack = back)
            Screen.Roulette -> RouletteGame(onBack = back)
            Screen.Race -> RaceGame(onBack = back)
            Screen.Language -> LanguageScreen(langPref, onBack = back, onSelect = onLangChange)
        }
    }
}

@Composable
fun HomeScreen(currentLang: Lang, onSelect: (Screen) -> Unit) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var askOpen by remember { mutableStateOf(false) }
    val vibe = LocalVibe.current
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Spacer(Modifier.height(12.dp))
            Image(
                painter = painterResource(R.drawable.wolf_logo),
                contentDescription = s.appName,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { vibe.tap(); askOpen = true },
            )
            Text(s.appName, color = WG.Accent, fontSize = 36.sp, fontWeight = FontWeight.Black)
            Text(s.appSubtitle, color = WG.SubText, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            MenuCard("🎲", s.menuDice, s.menuDiceSub) { onSelect(Screen.DiceMenu) }
            MenuCard("🐺", s.menuCroc, s.menuCrocSub) { onSelect(Screen.Croc) }
            MenuCard("🪜", s.menuLadder, s.menuLadderSub) { onSelect(Screen.Ladder) }
            MenuCard("🎡", s.menuRoulette, s.menuRouletteSub) { onSelect(Screen.Roulette) }
            MenuCard("🏁", s.menuRace, s.menuRaceSub) { onSelect(Screen.Race) }
            Spacer(Modifier.height(10.dp))
            // Powered by WOLF(feat. segna) — WOLF 를 누르면 홈페이지 이동 확인
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Powered by ", color = WG.SubText, fontSize = 13.sp)
                Text(
                    "WOLF",
                    color = WG.Accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { vibe.tap(); askOpen = true }
                        .padding(horizontal = 2.dp, vertical = 6.dp),
                )
                Text("(feat. segna)", color = WG.SubText, fontSize = 13.sp)
            }
        }
        // 오른쪽 위 언어 버튼 (현재 국기 표시)
        Row(
            Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(WG.Surface)
                .clickable { vibe.tap(); onSelect(Screen.Language) }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(currentLang.flag, fontSize = 20.sp)
            Spacer(Modifier.width(6.dp))
            Text("🌐 ${s.language}", color = WG.Text, fontSize = 14.sp)
        }
    }

    if (askOpen) {
        AlertDialog(
            onDismissRequest = { askOpen = false },
            containerColor = WG.Surface,
            title = { Text("🐺 WOLF", color = WG.Accent, fontWeight = FontWeight.Black) },
            text = { Text(s.goHomepage + "\n" + HOMEPAGE, color = WG.Text) },
            confirmButton = {
                VTextButton(onClick = {
                    askOpen = false
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(HOMEPAGE)))
                    } catch (_: ActivityNotFoundException) {
                    }
                }) { Text(s.ok, color = WG.Accent, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                VTextButton(onClick = { askOpen = false }) { Text(s.cancel, color = WG.SubText) }
            },
        )
    }
}

@Composable
fun DiceMenuScreen(onBack: () -> Unit, onSelect: (Screen) -> Unit) {
    val s = LocalStrings.current
    GameScaffold(s.menuDice, onBack) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            MenuCard("👆", s.rollTitle, s.rollSub) { onSelect(Screen.RollDice) }
            MenuCard("🔢", s.shutTitle, s.shutSub) { onSelect(Screen.ShutBox) }
            MenuCard("🎲", s.fiveTitle, s.fiveSub) { onSelect(Screen.FiveDice) }
        }
    }
}

/** 언어 선택 화면 */
@Composable
fun LanguageScreen(langPref: String, onBack: () -> Unit, onSelect: (String) -> Unit) {
    val s = LocalStrings.current
    GameScaffold("🌐 ${s.language}", onBack) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LanguageRow("📱", s.followSystem, selected = langPref == Lang.SYSTEM) { onSelect(Lang.SYSTEM) }
            for (lang in Lang.entries) {
                LanguageRow(lang.flag, lang.label, selected = langPref == lang.code) { onSelect(lang.code) }
            }
        }
    }
}

@Composable
private fun LanguageRow(flag: String, label: String, selected: Boolean, onClick: () -> Unit) {
    val vibe = LocalVibe.current
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) WG.Surface2 else WG.Surface)
            .border(2.dp, if (selected) WG.Accent else WG.Surface, shape)
            .clickable { vibe.tap(); onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(flag, fontSize = 30.sp)
        Spacer(Modifier.width(16.dp))
        Text(
            label,
            color = WG.Text,
            fontSize = 20.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )
        if (selected) Text("✓", color = WG.Accent, fontSize = 24.sp, fontWeight = FontWeight.Black)
    }
}
