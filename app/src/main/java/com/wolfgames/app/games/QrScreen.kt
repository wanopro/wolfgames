package com.wolfgames.app.games

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.WG
import org.json.JSONObject

// 📇 연락처 QR: 내 SNS 링크를 풀화면 QR로 (정보는 이 휴대폰에만 저장, QR은 휴대폰 안에서 만듦) — 웹 qr.js 와 같음

class Sns(val id: String, val name: String, val colors: List<Long>, val icon: String, val kind: String, val link: (String) -> String)

val SNS_LIST = listOf(
    Sns("instagram", "Instagram", listOf(0xFFF58529, 0xFFDD2A7B, 0xFF8134AF), "◎", "id") { "https://instagram.com/${it.removePrefix("@")}" },
    Sns("whatsapp", "WhatsApp", listOf(0xFF25D366), "✆", "phone") { "https://wa.me/${it.filter(Char::isDigit)}" },
    Sns("tiktok", "TikTok", listOf(0xFF000000), "♪", "id") { "https://www.tiktok.com/@${it.removePrefix("@")}" },
    Sns("line", "LINE", listOf(0xFF06C755), "L", "id") { "https://line.me/ti/p/~${it.removePrefix("@")}" },
    Sns("kakao", "KakaoTalk", listOf(0xFFFEE500), "K", "link") { it },
    Sns("telegram", "Telegram", listOf(0xFF229ED9), "✈", "id") { "https://t.me/${it.removePrefix("@")}" },
    Sns("facebook", "Facebook", listOf(0xFF1877F2), "f", "id") { "https://facebook.com/${it.removePrefix("@")}" },
    Sns("zalo", "Zalo", listOf(0xFF0068FF), "Z", "phone") { "https://zalo.me/${it.filter(Char::isDigit)}" },
)
fun snsLink(sns: Sns, v: String): String { val t = v.trim(); return if (t.startsWith("http://", true) || t.startsWith("https://", true)) t else sns.link(t) }
private fun accentOf(sns: Sns) = Color(if (sns.id == "tiktok") 0xFFFE2C55 else if (sns.id == "instagram") 0xFFDD2A7B else sns.colors[0])
private fun brushOf(sns: Sns) = if (sns.colors.size > 1) Brush.linearGradient(sns.colors.map { Color(it) }) else SolidColor(Color(sns.colors[0]))

private const val KEY = "qr_json"
data class QrEntry(val v: String, val on: Boolean)
fun loadQr(ctx: Context): Map<String, QrEntry> {
    val raw = ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE).getString(KEY, null) ?: return emptyMap()
    return try { val j = JSONObject(raw); j.keys().asSequence().associateWith { val o = j.getJSONObject(it); QrEntry(o.optString("v"), o.optBoolean("on", true)) } } catch (_: Exception) { emptyMap() }
}
private fun saveQr(ctx: Context, m: Map<String, QrEntry>) {
    val j = JSONObject(); for ((k, e) in m) j.put(k, JSONObject().put("v", e.v).put("on", e.on))
    ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE).edit().putString(KEY, j.toString()).apply()
}

private fun qrBitmap(text: String): Bitmap {
    val m = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 0, 0, mapOf(EncodeHintType.MARGIN to 0, EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M, EncodeHintType.CHARACTER_SET to "UTF-8"))
    val bmp = Bitmap.createBitmap(m.width, m.height, Bitmap.Config.ARGB_8888)
    for (y in 0 until m.height) for (x in 0 until m.width) bmp.setPixel(x, y, if (m[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
    return bmp
}

/** 풀화면 QR (흰 배경, 화면 밝기 최대). 입력한 게 없으면 바로 입력 화면 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QrScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    var editing by remember { mutableStateOf(false) }
    var data by remember { mutableStateOf(loadQr(ctx)) }
    val list = SNS_LIST.filter { data[it.id]?.let { e -> e.v.isNotBlank() && e.on } == true }
    if (editing || list.isEmpty()) {
        if (editing) BackHandler { editing = false }
        QrEditScreen(first = list.isEmpty(), onBack = { if (editing) editing = false else onBack() }) { data = loadQr(ctx); editing = false }
        return
    }
    // QR 보여주는 동안 화면 밝기 최대
    DisposableEffect(Unit) {
        val w = (ctx as? Activity)?.window
        val old = w?.attributes?.screenBrightness
        w?.let { val a = it.attributes; a.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL; it.attributes = a }
        onDispose { w?.let { val a = it.attributes; a.screenBrightness = old ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE; it.attributes = a } }
    }
    var k by remember { mutableIntStateOf(0) }
    if (k >= list.size) k = 0
    val sns = list[k]; val v = data[sns.id]!!.v
    val bmp = remember(sns.id, v) { qrBitmap(snsLink(sns, v)).asImageBitmap() }
    var drag = 0f
    Column(
        Modifier.fillMaxSize().background(Color.White).systemBarsPadding().padding(16.dp)
            .pointerInput(list.size) {
                detectHorizontalDragGestures(onDragStart = { drag = 0f }, onDragEnd = {
                    if (kotlin.math.abs(drag) > 60 && list.size > 1) { k = (k + (if (drag < 0) 1 else -1) + list.size) % list.size; vibe.tap() }
                }) { _, dx -> drag += dx }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // SNS 가 많으면 버튼이 다음 줄로 넘어감 (한 줄에 억지로 넣으면 마지막 버튼이 세로로 찌그러짐)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally), verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            list.forEachIndexed { i, x ->
                Box(
                    Modifier.clip(RoundedCornerShape(20.dp)).background(if (i == k) brushOf(x) else SolidColor(Color(0xFFEEEEEE)))
                        .clickable { k = i; vibe.tap() }.padding(horizontal = 12.dp, vertical = 8.dp),
                ) { Text(x.name, color = if (i == k) Color.White else Color(0xFF222222), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, maxLines = 1, softWrap = false) }
            }
        }
        Spacer(Modifier.height(18.dp))
        Box(
            Modifier.widthIn(max = 340.dp).fillMaxWidth(0.82f).aspectRatio(1f).border(7.dp, accentOf(sns), RoundedCornerShape(24.dp)).padding(16.dp),
        ) { Image(bmp, contentDescription = sns.name, modifier = Modifier.fillMaxSize(), filterQuality = FilterQuality.None) }
        Text(if (sns.kind == "link") sns.name else v, color = Color(0xFF111111), fontSize = 26.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 16.dp))
        Text(s.qrScan, color = Color(0xFF666666), fontSize = 14.sp, modifier = Modifier.padding(top = 6.dp))
        if (list.size > 1) {
            Row(Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                list.indices.forEach { i -> Box(Modifier.height(8.dp).width(if (i == k) 20.dp else 8.dp).clip(RoundedCornerShape(4.dp)).background(if (i == k) Color(0xFF333333) else Color(0xFFCCCCCC))) }
            }
            Text(s.qrSwipe, color = Color(0xFF666666), fontSize = 14.sp, modifier = Modifier.padding(top = 10.dp))
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BigButton(s.qrEdit, Modifier.weight(1f), color = Color(0xFFEEEEEE), contentColor = Color(0xFF222222)) { editing = true }
            BigButton(s.qrClose, Modifier.weight(1f), color = Color(0xFF111111), contentColor = Color.White) { onBack() }
        }
    }
}

/** 내 SNS 입력 */
@Composable
fun QrEditScreen(first: Boolean, onBack: () -> Unit, onSaved: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val init = remember { loadQr(ctx) }
    val values = remember { mutableStateListOf(*SNS_LIST.map { init[it.id]?.v ?: "" }.toTypedArray()) }
    val ons = remember { mutableStateListOf(*SNS_LIST.map { init[it.id]?.let { e -> e.on && e.v.isNotBlank() } ?: false }.toTypedArray()) }
    GameScaffold("📇 ${s.qrTitle}", onBack) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (first) Text(s.qrEmpty, color = WG.SubText, fontSize = 15.sp)
            SNS_LIST.forEachIndexed { i, sns ->
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(WG.Surface).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(brushOf(sns)), contentAlignment = Alignment.Center) {
                        Text(sns.icon, color = if (sns.id == "kakao") Color(0xFF3C1E1E) else Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    val hint = "${sns.name} · ${when (sns.kind) { "phone" -> s.qrPhone; "link" -> s.qrLink; else -> s.qrId }}"
                    Box(Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(WG.Bg).padding(horizontal = 10.dp, vertical = 10.dp)) {
                        if (values[i].isEmpty()) Text(hint, color = WG.SubText, fontSize = 14.sp, maxLines = 1)
                        BasicTextField(
                            values[i], { values[i] = it; if (it.isNotBlank()) ons[i] = true },
                            singleLine = true, textStyle = TextStyle(color = WG.Text, fontSize = 15.sp), cursorBrush = SolidColor(WG.Accent),
                            keyboardOptions = KeyboardOptions(keyboardType = if (sns.kind == "phone") KeyboardType.Phone else KeyboardType.Uri),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Box(
                        Modifier.width(42.dp).height(24.dp).clip(CircleShape).background(if (ons[i]) WG.Accent else Color(0xFF444444)).clickable { vibe.tap(); ons[i] = !ons[i] }.padding(3.dp),
                        contentAlignment = if (ons[i]) Alignment.CenterEnd else Alignment.CenterStart,
                    ) { Box(Modifier.size(18.dp).clip(CircleShape).background(if (ons[i]) Color.Black else Color(0xFF999999))) }
                }
            }
            Text(s.qrPrivacy, color = WG.SubText, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.padding(4.dp))
            BigButton(s.qrSave, Modifier.fillMaxWidth()) {
                val m = LinkedHashMap<String, QrEntry>()
                SNS_LIST.forEachIndexed { i, sns -> if (values[i].isNotBlank()) m[sns.id] = QrEntry(values[i].trim(), ons[i]) }
                saveQr(ctx, m); vibe.tap(); onSaved()
            }
        }
    }
}
