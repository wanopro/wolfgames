package com.wolfgames.app.games

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale
import androidx.compose.runtime.DisposableEffect
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.Lang
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.i18n.part
import com.wolfgames.app.i18n.stringsFor
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.Text
import com.wolfgames.app.ui.WG

// 🗣️ 회화 카드: 자주 쓰는 말을 현지어 큰 글씨로 (인터넷 없이) — 웹 phrase.js 와 같음
/** 🔊 읽어주기용 나라 말 */
fun ttsLocale(l: Lang): Locale = when (l) { Lang.KO -> Locale.KOREA; Lang.EN -> Locale.US; Lang.JA -> Locale.JAPAN; Lang.ZH -> Locale.SIMPLIFIED_CHINESE; Lang.TH -> Locale("th", "TH"); Lang.VI -> Locale("vi", "VN") }

val PH_ICONS = listOf("🚻", "👉", "🧾", "💰", "🌶️", "🌿", "🧊", "💧", "🍺", "🥡", "💳", "🏷️", "📄", "📸", "📶", "💊", "🆘", "🥜", "🍻", "🌙", "😍", "🥴", "🎁", "🤝", "🤬", "😱", "😤", "🤪", "🤣", "👋", "🙏", "🙇", "🐢", "📍", "✈️", "🛑", "⏱️", "🛎️", "🧳", "🧺", "❄️", "🙆", "🙅", "👌", "🤔", "🗣️", "✍️", "😊", "👍", "👋", "🙇", "😓", "🙅", "✋", "👀", "🙉", "📅", "🛑", "🚉", "🚌", "🎫", "🧮", "🐢", "⚡", "➡️", "⬅️", "⬆️", "😋", "📖", "⭐", "🚪", "🕐", "🍳", "🔑", "🚿", "🧹", "⏰", "🥃", "🍢", "🎤", "💸", "🤢", "🏠", "👕", "📏", "💵", "🏧", "🔌", "🚓", "🏥", "🙄", "😒", "🤡", "🐔", "💨")
/** 카테고리별 문장 번호 (phCats 순서: 인사 · 교통 · 식당 · 숙소 · 술집 · 쇼핑·도움 · 장난 욕) — 웹 PH_CATS 와 같음 */
val PH_CATS = listOf(listOf(29, 30, 31, 32, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57), listOf(33, 34, 35, 36, 58, 59, 60, 61, 62, 63, 64, 65, 66), listOf(1, 2, 4, 5, 6, 7, 9, 10, 12, 17, 67, 68, 69), listOf(37, 38, 39, 40, 14, 70, 71, 72, 73, 74, 75, 76), listOf(8, 18, 19, 20, 21, 22, 23, 77, 78, 79, 80, 81, 82), listOf(3, 11, 13, 0, 15, 16, 83, 84, 85, 86, 87, 88, 89), listOf(24, 25, 26, 27, 28, 90, 91, 92, 93, 94))

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhraseScreen(onBack: () -> Unit, mine: Lang, second: Lang?) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var to by remember { mutableStateOf(Lang.entries.firstOrNull { it.code == prefs.getString("ph_to", null) } ?: second ?: if (mine == Lang.EN) Lang.KO else Lang.EN) }
    var open by remember { mutableIntStateOf(-1) }
    var cat by remember { mutableIntStateOf(prefs.getInt("ph_cat", 0).coerceIn(0, PH_CATS.size - 1)) }
    val ids = PH_CATS[cat]
    var note by remember { mutableStateOf("") }
    // 휴대폰에 들어 있는 음성(TTS) — 그 언어 음성이 깔려 있으면 인터넷 없이도 됨
    var ttsReady by remember { mutableStateOf(false) }
    val tts = remember { arrayOfNulls<TextToSpeech>(1) }
    DisposableEffect(Unit) {
        tts[0] = TextToSpeech(ctx) { st -> ttsReady = st == TextToSpeech.SUCCESS }
        onDispose { tts[0]?.stop(); tts[0]?.shutdown() }
    }
    fun speak(text: String) {
        val t = tts[0]
        if (t == null || !ttsReady) { note = s.phNoVoice; return }
        val r = t.setLanguage(ttsLocale(to))
        if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED) { note = s.phNoVoice; return }
        note = ""; t.setSpeechRate(0.9f); t.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ph")
    }
    val my = remember(mine) { stringsFor(mine).phrases.split('|') }
    val theirs = remember(to) { stringsFor(to).phrases.split('|') }
    if (open >= 0) {
        BackHandler { open = -1; tts[0]?.stop(); note = "" }
        Column(Modifier.fillMaxSize().background(Color.White).systemBarsPadding().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.weight(1f))
            Text(PH_ICONS[open], fontSize = 64.sp)
            Text(theirs[open], color = Color(0xFF111111), fontSize = 40.sp, lineHeight = 50.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 14.dp).widthIn(max = 600.dp))
            if (to != mine) Text(my[open], color = Color(0xFF666666), fontSize = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 12.dp))
            Box(Modifier.padding(top = 18.dp).clip(RoundedCornerShape(26.dp)).background(Color(0xFF1565C0)).clickable { vibe.tap(); speak(theirs[open]) }.padding(horizontal = 26.dp, vertical = 12.dp)) {
                Text(s.phSpeak, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            }
            if (note.isNotEmpty()) Text(note, color = Color(0xFFC62828), fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp).widthIn(max = 420.dp))
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BigButton("‹", Modifier.weight(1f), color = Color(0xFFEEEEEE), contentColor = Color(0xFF222222)) { open = ids[(ids.indexOf(open) + ids.size - 1) % ids.size]; note = "" }
                BigButton(part(s.qrClose, 0), Modifier.weight(1f), color = Color(0xFF111111), contentColor = Color.White) { open = -1; tts[0]?.stop(); note = "" }
                BigButton("›", Modifier.weight(1f), color = Color(0xFFEEEEEE), contentColor = Color(0xFF222222)) { open = ids[(ids.indexOf(open) + 1) % ids.size]; note = "" }
            }
        }
        return
    }
    GameScaffold("🗣️ ${s.phTitle}", onBack) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(s.phDesc, color = WG.SubText, fontSize = 15.sp, lineHeight = 22.sp)
            Text(s.phTo, color = WG.SubText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Lang.entries.forEach { L ->
                    val on = L == to
                    Row(Modifier.clip(RoundedCornerShape(20.dp)).background(if (on) WG.Accent else WG.Surface).clickable { vibe.tap(); to = L; prefs.edit().putString("ph_to", L.code).apply() }.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(L.flag, fontSize = 16.sp); Spacer(Modifier.width(6.dp)); Text(L.label, color = if (on) Color.Black else WG.Text, fontSize = 14.sp)
                    }
                }
            }
            FlowRow(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                s.phCats.split('|').forEachIndexed { k, c ->
                    val on = k == cat
                    Text(c, color = if (on) Color.Black else WG.Text, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (on) WG.Accent else WG.Surface).clickable { vibe.tap(); cat = k; prefs.edit().putInt("ph_cat", k).apply() }.padding(horizontal = 12.dp, vertical = 6.dp))
                }
            }
            ids.forEach { i -> val m = my[i]
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(WG.Surface).clickable { vibe.tap(); open = i }.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(PH_ICONS.getOrElse(i) { "💬" }, fontSize = 26.sp); Spacer(Modifier.width(12.dp))
                    Column {
                        Text(m, color = WG.Text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        if (to != mine) Text(theirs.getOrElse(i) { "" }, color = WG.SubText, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
