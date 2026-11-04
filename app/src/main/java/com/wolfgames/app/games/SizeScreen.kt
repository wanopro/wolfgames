package com.wolfgames.app.games

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.Text
import com.wolfgames.app.ui.WG

// 📏 사이즈 변환표 (계산기 안) — 웹 calc.js sizesScreen 과 같음. 내 사이즈 줄을 누르면 ⭐ 저장
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SizeScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var cat by remember { mutableIntStateOf(prefs.getInt("sz_cat", 0).coerceIn(0, SIZES.size - 1)) }
    var ver by remember { mutableIntStateOf(0) }
    val my = remember(cat, ver) { prefs.getInt("sz_$cat", -1) }
    GameScaffold("📏 ${s.szTitle}", onBack) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(s.szDesc, color = WG.SubText, fontSize = 15.sp, lineHeight = 22.sp)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                s.szCats.split('|').forEachIndexed { k, c ->
                    val on = k == cat
                    Text(c, color = if (on) Color.Black else WG.Text, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (on) WG.Accent else WG.Surface).clickable { vibe.tap(); cat = k; prefs.edit().putInt("sz_cat", k).apply() }.padding(horizontal = 12.dp, vertical = 6.dp))
                }
            }
            val t = SIZES[cat]
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(WG.Bg), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    t.cols.forEach { c -> Box(Modifier.weight(1f).background(WG.Surface2).padding(vertical = 10.dp), contentAlignment = Alignment.Center) { Text(c, color = WG.SubText, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center) } }
                }
                t.rows.forEachIndexed { i, r ->
                    val on = i == my
                    Row(Modifier.fillMaxWidth().clickable { vibe.tap(); prefs.edit().putInt("sz_$cat", if (on) -1 else i).apply(); ver++ }, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        r.forEachIndexed { j, v ->
                            Box(Modifier.weight(1f).background(if (on) WG.Accent else WG.Surface).padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
                                Text(if (j == 0 && on) "⭐ $v" else v, color = if (on) Color.Black else WG.Text, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}
