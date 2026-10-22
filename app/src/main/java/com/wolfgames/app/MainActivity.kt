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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.wolfgames.app.games.ArrowGame
import com.wolfgames.app.games.CalculatorScreen
import com.wolfgames.app.games.D789Game
import com.wolfgames.app.games.HoChiMinhGame
import com.wolfgames.app.games.UpDownGame
import com.wolfgames.app.games.ExchangeScreen
import com.wolfgames.app.games.FiveDiceGame
import com.wolfgames.app.games.FingerGame
import com.wolfgames.app.games.FourGame
import com.wolfgames.app.games.LadderGame
import com.wolfgames.app.games.PirateGame
import com.wolfgames.app.games.RaceGame
import com.wolfgames.app.games.RollDiceGame
import com.wolfgames.app.games.RouletteGame
import com.wolfgames.app.games.ShutBoxGame
import com.wolfgames.app.games.WolfTeethGame
import com.wolfgames.app.i18n.CHANGELOG
import com.wolfgames.app.i18n.ChangeItem
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
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

enum class Screen { Home, DiceMenu, RollDice, ShutBox, FiveDice, D789, CardMenu, UpDown, HoChiMinh, FingerMenu, Finger, WolfMenu, LuckMenu, BattleMenu, Croc, Ladder, Roulette, Race, Pirate, Arrow, Four, Calc, Fx, Language, Changelog }

@Composable
fun App(langPref: String, currentLang: Lang, onLangChange: (String) -> Unit) {
    var screen by rememberSaveable { mutableStateOf(Screen.Home) }
    val s = LocalStrings.current
    val back: () -> Unit = {
        screen = when (screen) {
            Screen.RollDice, Screen.ShutBox, Screen.FiveDice, Screen.D789 -> Screen.DiceMenu
            Screen.UpDown, Screen.HoChiMinh -> Screen.CardMenu
            Screen.Finger -> Screen.FingerMenu
            Screen.Croc, Screen.Race, Screen.Pirate, Screen.Arrow -> Screen.WolfMenu
            Screen.Ladder, Screen.Roulette -> Screen.LuckMenu
            Screen.Four -> Screen.BattleMenu
            Screen.Fx -> Screen.Calc
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
            Screen.D789 -> D789Game(onBack = back)
            Screen.CardMenu -> CardMenuScreen(onBack = back) { screen = it }
            Screen.UpDown -> UpDownGame(onBack = back)
            Screen.HoChiMinh -> HoChiMinhGame(onBack = back)
            Screen.Finger -> FingerGame(onBack = back)
            Screen.FingerMenu -> CategoryScreen("👆 ${s.menuFinger}", back, listOf(MenuItem("☝️", s.fingerTitle, s.fingerSub, Screen.Finger))) { screen = it }
            Screen.WolfMenu -> CategoryScreen("🐺 ${s.menuWolf}", back, listOf(
                MenuItem("🐺", s.menuCroc, s.menuCrocSub, Screen.Croc),
                MenuItem("🏁", s.menuRace, s.menuRaceSub, Screen.Race),
                MenuItem("🗡️", s.menuPirate, s.menuPirateSub, Screen.Pirate),
                MenuItem("🔄", s.menuArrow, s.menuArrowSub, Screen.Arrow),
            )) { screen = it }
            Screen.LuckMenu -> CategoryScreen("🍀 ${s.menuLuck}", back, listOf(
                MenuItem("🪜", s.menuLadder, s.menuLadderSub, Screen.Ladder),
                MenuItem("🎡", s.menuRoulette, s.menuRouletteSub, Screen.Roulette),
            )) { screen = it }
            Screen.BattleMenu -> CategoryScreen("⚔️ ${s.menuBattle}", back, listOf(MenuItem("🔴", s.menuFour, s.menuFourSub, Screen.Four))) { screen = it }
            Screen.Croc -> WolfTeethGame(onBack = back)
            Screen.Ladder -> LadderGame(onBack = back)
            Screen.Roulette -> RouletteGame(onBack = back)
            Screen.Race -> RaceGame(onBack = back)
            Screen.Pirate -> PirateGame(onBack = back)
            Screen.Arrow -> ArrowGame(onBack = back)
            Screen.Four -> FourGame(onBack = back)
            Screen.Calc -> CalculatorScreen(onBack = back, onExchange = { screen = Screen.Fx })
            Screen.Fx -> ExchangeScreen(onBack = back, langCode = currentLang.code)
            Screen.Language -> LanguageScreen(langPref, onBack = back, onSelect = onLangChange)
            Screen.Changelog -> ChangelogScreen(currentLang.code, onBack = back)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(currentLang: Lang, onSelect: (Screen) -> Unit) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var askOpen by remember { mutableStateOf(false) }
    val vibe = LocalVibe.current
    // 버전 표시 (build.gradle 의 versionName)
    val version = remember {
        try { context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "" } catch (_: Exception) { "" }
    }
    // 새 버전을 처음 열었을 때 한 번만 바뀐 점 보여주기
    val prefs = remember { context.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var showNew by remember { mutableStateOf(version.isNotEmpty() && prefs.getString("seen_version", null) != version) }
    LaunchedEffect(Unit) { if (showNew) prefs.edit().putString("seen_version", version).apply() }
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.wolf_logo),
                    contentDescription = s.appName,
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                        .clickable { vibe.tap(); askOpen = true },
                )
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(s.appName, color = WG.Accent, fontSize = if (s.appName.length > 6) 27.sp else 32.sp, fontWeight = FontWeight.Black, lineHeight = 36.sp)
                    Text(s.appSubtitle, color = WG.SubText, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // 언어 버튼 (현재 국기 표시)
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(WG.Surface)
                                .clickable { vibe.tap(); onSelect(Screen.Language) }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(currentLang.flag, fontSize = 18.sp)
                            Spacer(Modifier.width(6.dp))
                            Text("🌐 ${s.language}", color = WG.Text, fontSize = 14.sp)
                        }
                        // 계산기 버튼
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(WG.Surface)
                                .clickable { vibe.tap(); onSelect(Screen.Calc) }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("🧮 ${s.calcTitle}", color = WG.Text, fontSize = 14.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            MenuCard("🎲", s.menuDice, s.menuDiceSub) { onSelect(Screen.DiceMenu) }
            MenuCard("🃏", s.menuCards, s.menuCardsSub) { onSelect(Screen.CardMenu) }
            MenuCard("👆", s.menuFinger, s.menuFingerSub) { onSelect(Screen.FingerMenu) }
            MenuCard("🐺", s.menuWolf, s.menuWolfSub) { onSelect(Screen.WolfMenu) }
            MenuCard("🍀", s.menuLuck, s.menuLuckSub) { onSelect(Screen.LuckMenu) }
            MenuCard("⚔️", s.menuBattle, s.menuBattleSub) { onSelect(Screen.BattleMenu) }
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
            // 설치 안내(웹) 링크 + 버전
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "📖 ${s.installGuide}",
                    color = WG.SubText,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            vibe.tap()
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wanopro.github.io/wolfgames/manual.html?lang=${currentLang.code}")))
                            } catch (_: ActivityNotFoundException) {
                            }
                        }
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                )
                Text("  ·  ", color = WG.SubText.copy(alpha = 0.6f), fontSize = 12.sp)
                Text(
                    "📜 v$version ${s.logTitle}",
                    color = WG.SubText,
                    fontSize = 12.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { vibe.tap(); onSelect(Screen.Changelog) }
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                )
            }
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

    if (showNew) {
        val rel = CHANGELOG.firstOrNull { it.version == version } ?: CHANGELOG.first()
        AlertDialog(
            onDismissRequest = { showNew = false },
            containerColor = WG.Surface,
            title = { Text("🐺 ${s.logNew(rel.version)}", color = WG.Accent, fontWeight = FontWeight.Black, fontSize = 20.sp) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    rel.items.forEach { ChangeRow(it, currentLang.code) }
                }
            },
            confirmButton = {
                VTextButton(onClick = { showNew = false }) { Text(s.logOk, color = WG.Accent, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                VTextButton(onClick = { showNew = false; onSelect(Screen.Changelog) }) { Text(s.logAll, color = WG.SubText) }
            },
        )
    }
}

@Composable
private fun ChangeRow(item: ChangeItem, langCode: String) {
    Row {
        Text(item.emoji, fontSize = 15.sp, modifier = Modifier.width(28.dp))
        Text(item.text[langCode] ?: item.text["en"] ?: "", color = WG.Text, fontSize = 15.sp, lineHeight = 21.sp)
    }
}

/** 📜 업데이트 내역 (버전별 수정 사항) */
@Composable
fun ChangelogScreen(langCode: String, onBack: () -> Unit) {
    val s = LocalStrings.current
    GameScaffold("📜 ${s.logTitle}", onBack) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            CHANGELOG.forEachIndexed { i, rel ->
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(WG.Surface).padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("v${rel.version}", color = WG.Accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(10.dp))
                        Text(rel.date.replace("-", "."), color = WG.SubText, fontSize = 13.sp)
                        if (i == 0) {
                            Spacer(Modifier.width(10.dp))
                            Text(
                                s.logLatest,
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(WG.Accent).padding(horizontal = 8.dp, vertical = 2.dp),
                            )
                        }
                    }
                    rel.items.forEach { ChangeRow(it, langCode) }
                }
            }
        }
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
            MenuCard("🍶", s.d789Title, s.d789Sub) { onSelect(Screen.D789) }
        }
    }
}

/** 카테고리 메뉴 한 줄 */
data class MenuItem(val emoji: String, val title: String, val sub: String, val screen: Screen)

/** 카테고리 안의 게임 목록 */
@Composable
fun CategoryScreen(title: String, onBack: () -> Unit, items: List<MenuItem>, onSelect: (Screen) -> Unit) {
    GameScaffold(title, onBack) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items.forEach { MenuCard(it.emoji, it.title, it.sub) { onSelect(it.screen) } }
        }
    }
}

/** 카드 게임 메뉴 */
@Composable
fun CardMenuScreen(onBack: () -> Unit, onSelect: (Screen) -> Unit) {
    val s = LocalStrings.current
    GameScaffold("🃏 ${s.menuCards}", onBack) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            MenuCard("↕️", s.udTitle, s.udSub) { onSelect(Screen.UpDown) }
            MenuCard("🇻🇳", s.hcmTitle, s.hcmSub) { onSelect(Screen.HoChiMinh) }
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
