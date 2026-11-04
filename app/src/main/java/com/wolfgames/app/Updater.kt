package com.wolfgames.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.ResultOverlay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * 🔄 앱 안 업데이트: 릴리스에 같이 올라가는 version.json 을 보고 새 버전이면 APK 를 받아 설치 화면을 연다.
 * 베타(…​.beta)는 베타 릴리스, 정식은 최신 정식 릴리스를 본다.
 * 설치 확인 버튼은 안드로이드 보안상 사람이 한 번 눌러야 함 (플레이스토어 앱이 아니라서).
 */
object Updater {
    data class Info(val code: Long, val name: String, val apk: String)

    private fun base(ctx: Context) = if (ctx.packageName.endsWith(".beta")) "https://github.com/wanopro/wolfgames_beta/releases/download/beta/"
        else "https://github.com/wanopro/wolfgames/releases/latest/download/"

    fun current(ctx: Context): Pair<Long, String> = try {
        val pi = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
        (if (Build.VERSION.SDK_INT >= 28) pi.longVersionCode else @Suppress("DEPRECATION") pi.versionCode.toLong()) to (pi.versionName ?: "")
    } catch (_: Exception) { 0L to "" }

    private fun open(url: String): HttpURLConnection {
        var u = url
        repeat(6) { // github.com → 다운로드 서버로 넘겨주는 주소 따라가기
            val c = URL(u).openConnection() as HttpURLConnection
            c.instanceFollowRedirects = false; c.connectTimeout = 10000; c.readTimeout = 20000
            c.setRequestProperty("User-Agent", "WolfGames-Android")
            val code = c.responseCode
            if (code in 300..399) { u = URL(URL(u), c.getHeaderField("Location")).toString(); c.disconnect() } else return c
        }
        throw java.io.IOException("too many redirects")
    }

    /** 새 버전이 있으면 Info, 없거나 확인 못 하면 null */
    suspend fun check(ctx: Context): Info? = withContext(Dispatchers.IO) {
        try {
            val c = open(base(ctx) + "version.json")
            if (c.responseCode != 200) return@withContext null
            val o = JSONObject(c.inputStream.bufferedReader().use { it.readText() })
            val info = Info(o.getLong("versionCode"), o.optString("versionName"), o.optString("apk").ifEmpty { base(ctx) + "WolfGames.apk" })
            if (info.code > current(ctx).first) info else null
        } catch (_: Exception) { null }
    }

    /** APK 받기 (progress 0~1). 실패하면 null */
    suspend fun download(ctx: Context, info: Info, progress: (Float) -> Unit): File? = withContext(Dispatchers.IO) {
        try {
            val dir = File(ctx.cacheDir, "updates").apply { mkdirs(); listFiles()?.forEach { it.delete() } }
            val f = File(dir, "update-${info.code}.apk")
            val c = open(info.apk)
            if (c.responseCode != 200) return@withContext null
            val total = c.contentLengthLong
            c.inputStream.use { inp -> f.outputStream().use { out ->
                val buf = ByteArray(64 * 1024); var got = 0L
                while (true) { val n = inp.read(buf); if (n < 0) break; out.write(buf, 0, n); got += n; if (total > 0) withContext(Dispatchers.Main) { progress(got.toFloat() / total) } }
            } }
            if (f.length() < 100_000) null else f
        } catch (_: Exception) { null }
    }

    fun canInstall(ctx: Context) = Build.VERSION.SDK_INT < 26 || ctx.packageManager.canRequestPackageInstalls()
    fun openAllowSettings(ctx: Context) {
        try { ctx.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${ctx.packageName}"))) } catch (_: Exception) { }
    }
    fun install(ctx: Context, f: File) {
        val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.updates", f)
        ctx.startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/vnd.android.package-archive").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

/**
 * 홈 화면 위에 뜨는 업데이트 안내. auto = 앱 켤 때 자동 확인 (하루에 한 번, "나중에" 누른 버전은 하루 동안 안 물어봄).
 * manual 이 바뀌면 바로 확인 (업데이트 내역 화면의 🔄 버튼).
 */
@Composable
fun BoxScope.UpdatePrompt(manual: Int = 0, onLatest: () -> Unit = {}) {
    val s = LocalStrings.current; val ctx = LocalContext.current; val scope = rememberCoroutineScope()
    val prefs = remember { ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var info by remember { mutableStateOf<Updater.Info?>(null) }
    var stage by remember { mutableStateOf(0) } // 0 물어보기 · 1 허용 안내 · 2 받는 중 · 3 실패
    var prog by remember { mutableStateOf(0f) }
    LaunchedEffect(manual) {
        if (com.wolfgames.app.DebugOpen.autostart) return@LaunchedEffect
        val now = System.currentTimeMillis()
        if (manual == 0) {
            if (now - prefs.getLong("up_checked", 0) < 6 * 3600_000L) return@LaunchedEffect
            prefs.edit().putLong("up_checked", now).apply()
        }
        val i = Updater.check(ctx)
        if (i == null) { if (manual > 0) onLatest(); return@LaunchedEffect }
        if (manual == 0 && prefs.getLong("up_later_code", 0) == i.code && now - prefs.getLong("up_later_at", 0) < 24 * 3600_000L) return@LaunchedEffect
        stage = 0; info = i
    }
    val i = info ?: return
    fun go() {
        if (!Updater.canInstall(ctx)) { stage = 1; return }
        stage = 2; prog = 0f
        scope.launch {
            val f = Updater.download(ctx, i) { prog = it }
            if (f == null) stage = 3 else { info = null; Updater.install(ctx, f) }
        }
    }
    val cur = Updater.current(ctx).second
    when (stage) {
        0 -> ResultOverlay(s.upTitle, s.upSub(cur, i.name), s.upGo, onPrimary = { go() }, secondary = s.upLater, onSecondary = {
            prefs.edit().putLong("up_later_code", i.code).putLong("up_later_at", System.currentTimeMillis()).apply(); info = null
        })
        1 -> ResultOverlay(s.upTitle, s.upAllow, s.upGo, onPrimary = { Updater.openAllowSettings(ctx); stage = 0 }, secondary = s.upLater, onSecondary = { info = null })
        2 -> ResultOverlay(s.upDownloading, "${(prog * 100).toInt()}%", s.upLater, onPrimary = { info = null })
        else -> ResultOverlay(s.upFail, null, s.upGo, onPrimary = { go() }, secondary = s.upLater, onSecondary = { info = null })
    }
}
