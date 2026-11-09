package com.wolfgames.app.games

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.i18n.one
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.Text
import com.wolfgames.app.ui.WG

// 📤 앱 공유하기 — 친구 폰(안드로이드 / 아이폰·PC) 고르고 → 앱으로 공유 · 링크만 복사 · QR (웹 share.js 와 같음)
const val SHARE_APK = "https://github.com/wanopro/wolfgames/releases/latest/download/WolfGames.apk"
const val SHARE_WEB = "https://wanopro.github.io/wolfgames/"

@Composable
fun ShareScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var ios by remember { mutableStateOf(prefs.getBoolean("share_ios", false)) }
    val link = if (ios) SHARE_WEB else SHARE_APK
    val bmp = remember(link) { qrBitmap(link).asImageBitmap() }
    GameScaffold(s.shTitle, onBack) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(s.shWhich, color = WG.Text, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for ((v, label) in listOf(false to s.shAndroid, true to s.shIphone)) {
                    BigButton(label, Modifier.weight(1f), color = if (ios == v) WG.Accent else WG.Surface2, contentColor = if (ios == v) Color.Black else WG.Text) {
                        ios = v; prefs.edit().putBoolean("share_ios", v).apply()
                    }
                }
            }
            Text(if (ios) s.shIphoneTip else s.shAndroidTip, color = WG.SubText, fontSize = 15.sp, lineHeight = 21.sp)
            Box(Modifier.align(Alignment.CenterHorizontally).widthIn(max = 260.dp).fillMaxWidth(0.66f).aspectRatio(1f).clip(RoundedCornerShape(18.dp)).background(Color.White).padding(14.dp)) {
                Image(bmp, contentDescription = link, modifier = Modifier.fillMaxSize(), filterQuality = FilterQuality.None)
            }
            Text(s.shQr, color = WG.SubText, fontSize = 15.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Text(link, color = WG.SubText, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            BigButton(s.shSend, Modifier.fillMaxWidth()) {
                val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_SUBJECT, one(s.shMsg)).putExtra(Intent.EXTRA_TEXT, "${one(s.shMsg)}\n$link")
                ctx.startActivity(Intent.createChooser(send, one(s.shSend)))
            }
            BigButton(s.shCopy, Modifier.fillMaxWidth(), color = WG.Surface2, contentColor = WG.Text) {
                (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("Wolf Games", link))
                Toast.makeText(ctx, one(s.shCopied), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
