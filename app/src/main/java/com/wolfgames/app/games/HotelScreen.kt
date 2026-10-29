package com.wolfgames.app.games

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.WindowManager
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
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
import com.wolfgames.app.i18n.EnStrings
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.i18n.one
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.Text
import com.wolfgames.app.ui.WG
import org.json.JSONArray
import org.json.JSONObject

// 🏨 숙소 주소 카드: 택시 기사님께 보여주는 큰 글씨 화면 + 구글 지도 QR (정보는 이 휴대폰에만 저장) — 웹 hotel.js 와 같음

data class Hotel(val name: String, val addr: String, val phone: String, val note: String)
private const val KEY = "hotels_json"
private val ORANGE = Color(0xFFE65100)

fun loadHotels(ctx: Context): List<Hotel> {
    val raw = ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE).getString(KEY, null) ?: return emptyList()
    return try {
        val a = JSONArray(raw)
        (0 until a.length()).map { val o = a.getJSONObject(it); Hotel(o.optString("name"), o.optString("addr"), o.optString("phone"), o.optString("note")) }.filter { it.name.isNotBlank() || it.addr.isNotBlank() }
    } catch (_: Exception) { emptyList() }
}
private fun saveHotels(ctx: Context, list: List<Hotel>) {
    val a = JSONArray(); for (x in list) a.put(JSONObject().put("name", x.name).put("addr", x.addr).put("phone", x.phone).put("note", x.note))
    ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE).edit().putString(KEY, a.toString()).apply()
}
fun mapLink(addr: String): String = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(addr)

private fun qrBmp(text: String): Bitmap {
    val m = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 0, 0, mapOf(EncodeHintType.MARGIN to 0, EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M, EncodeHintType.CHARACTER_SET to "UTF-8"))
    val bmp = Bitmap.createBitmap(m.width, m.height, Bitmap.Config.ARGB_8888)
    for (y in 0 until m.height) for (x in 0 until m.width) bmp.setPixel(x, y, if (m[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
    return bmp
}

/** 풀화면 주소 카드 (흰 배경, 화면 밝기 최대). 저장된 숙소가 없으면 바로 입력 화면 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HotelScreen(onBack: () -> Unit, langCode: String, secondCode: String?) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    var editing by remember { mutableStateOf(false) }
    var list by remember { mutableStateOf(loadHotels(ctx)) }
    if (editing || list.isEmpty()) {
        if (editing) BackHandler { editing = false }
        HotelEditScreen(first = list.isEmpty(), onBack = { if (editing) editing = false else onBack() }) { list = loadHotels(ctx); editing = false }
        return
    }
    DisposableEffect(Unit) {
        val w = (ctx as? Activity)?.window
        val old = w?.attributes?.screenBrightness
        w?.let { val a = it.attributes; a.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL; it.attributes = a }
        onDispose { w?.let { val a = it.attributes; a.screenBrightness = old ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE; it.attributes = a } }
    }
    var k by remember { mutableIntStateOf(0) }
    if (k >= list.size) k = 0
    val x = list[k]
    val bmp = remember(x.addr) { if (x.addr.isNotBlank()) qrBmp(mapLink(x.addr)).asImageBitmap() else null }
    // 기사님이 영어만 읽을 수도 있으니, 영어가 안 보이면 영어 한 줄 더
    val showEn = langCode != "en" && secondCode != "en"
    Column(
        Modifier.fillMaxSize().background(Color.White).systemBarsPadding().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (list.size > 1) FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally), verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            list.forEachIndexed { i, y ->
                Box(Modifier.clip(RoundedCornerShape(20.dp)).background(if (i == k) ORANGE else Color(0xFFEEEEEE)).clickable { k = i; vibe.tap() }.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(y.name.ifBlank { "#${i + 1}" }, color = if (i == k) Color.White else Color(0xFF222222), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, maxLines = 1, softWrap = false)
                }
            }
        }
        Text(s.hotelTakeMe, color = ORANGE, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 14.dp))
        if (showEn) Text(EnStrings.hotelTakeMe, color = Color(0xFFBF360C), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        if (x.name.isNotBlank()) Text(x.name, color = Color(0xFF111111), fontSize = 34.sp, lineHeight = 40.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 14.dp))
        if (x.addr.isNotBlank()) Text(x.addr, color = Color(0xFF111111), fontSize = 26.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 10.dp).widthIn(max = 560.dp))
        if (x.note.isNotBlank()) Text(x.note, color = Color(0xFF444444), fontSize = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
        if (bmp != null) {
            Box(Modifier.padding(top = 16.dp).widthIn(max = 230.dp).fillMaxWidth(0.55f).aspectRatio(1f).border(7.dp, ORANGE, RoundedCornerShape(24.dp)).padding(14.dp)) {
                Image(bmp, contentDescription = x.addr, modifier = Modifier.fillMaxSize(), filterQuality = FilterQuality.None)
            }
            Text(s.hotelMap, color = Color(0xFF666666), fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp))
        }
        if (x.phone.isNotBlank()) {
            Box(
                Modifier.padding(top = 14.dp).clip(RoundedCornerShape(26.dp)).background(Color(0xFF2E7D32)).clickable {
                    vibe.tap()
                    try { ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + x.phone.filter { it.isDigit() || it == '+' }))) } catch (_: ActivityNotFoundException) { }
                }.padding(horizontal = 22.dp, vertical = 12.dp),
            ) { Text("${s.hotelCall}  ${x.phone}", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, textAlign = TextAlign.Center) }
        }
        Spacer(Modifier.heightIn(min = 24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BigButton(s.hotelEditBtn, Modifier.weight(1f), color = Color(0xFFEEEEEE), contentColor = Color(0xFF222222)) { editing = true }
            BigButton(s.qrClose, Modifier.weight(1f), color = Color(0xFF111111), contentColor = Color.White) { onBack() }
        }
    }
}

/** 숙소 입력 (여러 개) */
@Composable
fun HotelEditScreen(first: Boolean, onBack: () -> Unit, onSaved: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val rows = remember { mutableStateListOf(*loadHotels(ctx).ifEmpty { listOf(Hotel("", "", "", "")) }.toTypedArray()) }
    GameScaffold("🏨 ${s.hotelTitle}", onBack) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (first) Text(s.hotelEmpty, color = WG.SubText, fontSize = 15.sp)
            rows.forEachIndexed { i, x ->
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(WG.Surface).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Field(x.name, one(s.hotelName), KeyboardType.Text, 1) { rows[i] = rows[i].copy(name = it) }
                    Field(x.addr, one(s.hotelAddr), KeyboardType.Text, 3) { rows[i] = rows[i].copy(addr = it) }
                    Field(x.phone, one(s.hotelPhone), KeyboardType.Phone, 1) { rows[i] = rows[i].copy(phone = it) }
                    Field(x.note, one(s.hotelNote), KeyboardType.Text, 1) { rows[i] = rows[i].copy(note = it) }
                    if (rows.size > 1 || x.name.isNotBlank() || x.addr.isNotBlank()) {
                        Text(s.hotelDelete, color = Color(0xFFFF8A80), fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.End).clip(RoundedCornerShape(8.dp)).clickable { vibe.tap(); rows.removeAt(i); if (rows.isEmpty()) rows.add(Hotel("", "", "", "")) }.padding(6.dp))
                    }
                }
            }
            BigButton(s.hotelAdd, Modifier.fillMaxWidth(), color = WG.Surface2, contentColor = WG.Text) { rows.add(Hotel("", "", "", "")) }
            Text(s.qrPrivacy, color = WG.SubText, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.padding(4.dp))
            BigButton(s.qrSave, Modifier.fillMaxWidth()) {
                saveHotels(ctx, rows.map { Hotel(it.name.trim(), it.addr.trim(), it.phone.trim(), it.note.trim()) }.filter { it.name.isNotBlank() || it.addr.isNotBlank() })
                vibe.tap(); onSaved()
            }
        }
    }
}

@Composable
private fun Field(value: String, hint: String, kb: KeyboardType, lines: Int, onChange: (String) -> Unit) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(WG.Bg).padding(horizontal = 10.dp, vertical = 10.dp)) {
        if (value.isEmpty()) Text(hint, color = WG.SubText, fontSize = 14.sp)
        BasicTextField(
            value, onChange, singleLine = lines == 1, minLines = lines,
            textStyle = TextStyle(color = WG.Text, fontSize = 15.sp), cursorBrush = SolidColor(WG.Accent),
            keyboardOptions = KeyboardOptions(keyboardType = kb), modifier = Modifier.fillMaxWidth(),
        )
    }
}
