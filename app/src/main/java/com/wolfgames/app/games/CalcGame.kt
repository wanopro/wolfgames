package com.wolfgames.app.games

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.Stepper
import com.wolfgames.app.ui.VTextButton
import com.wolfgames.app.ui.WG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToLong

// ---------------- 숫자 표시 ----------------
private fun fmt(v: Double, maxFrac: Int = 8): String {
    if (v.isNaN() || v.isInfinite()) return "—"
    val df = DecimalFormat("#,##0", DecimalFormatSymbols(Locale.US))
    df.maximumFractionDigits = maxFrac
    return df.format(v)
}

private fun fmtInput(s: String): String {
    if (s.isEmpty()) return s
    val parts = s.split(".")
    val i = parts[0].reversed().chunked(3).joinToString(",").reversed()
    return if (parts.size > 1) "$i.${parts[1]}" else i
}

private val OPS = setOf("+", "−", "×", "÷")

private fun op(o: String, a: Double, b: Double) = when (o) {
    "+" -> a + b
    "−" -> a - b
    "×" -> a * b
    else -> a / b
}

/** 곱셈·나눗셈 먼저 계산 (웹 버전 calc.js 와 같은 방식) */
private fun evaluate(tokens: List<String>): Double {
    val t = tokens.toMutableList()
    if (t.isNotEmpty() && t.last() in OPS) t.removeAt(t.size - 1)
    if (t.isEmpty()) return 0.0
    val vals = t.filter { it !in OPS }.map { it.toDoubleOrNull() ?: return Double.NaN }
    val ops = t.filter { it in OPS }
    val v2 = mutableListOf(vals[0])
    val o2 = mutableListOf<String>()
    ops.forEachIndexed { i, o ->
        if (o == "×" || o == "÷") v2[v2.size - 1] = op(o, v2.last(), vals[i + 1]) else { o2.add(o); v2.add(vals[i + 1]) }
    }
    var acc = v2[0]
    o2.forEachIndexed { i, o -> acc = op(o, acc, v2[i + 1]) }
    return acc
}

/** 화면을 오가도 계산 내용 유지 */
private object CalcState {
    val tokens = mutableStateListOf<String>()
    var fresh by mutableStateOf(false)
    var lastExpr by mutableStateOf("")
    fun value(): Double = evaluate(tokens).let { if (it.isNaN() || it.isInfinite()) 0.0 else it }
}

private fun show(t: List<String>) = t.joinToString("") { if (it in OPS) " $it " else fmtInput(it) }

private fun press(k: String) {
    val t = CalcState.tokens
    val last = t.lastOrNull()
    val isNum = last != null && last !in OPS
    when {
        k.all { it.isDigit() } || k == "." -> {
            if (CalcState.fresh) { t.clear(); CalcState.fresh = false }
            if (isNum && !CalcState.fresh) {
                var cur = t.last()
                if (k == "." && cur.contains(".")) return
                if (cur.replace(".", "").length >= 15) return
                cur = if (cur == "0" && k != ".") (if (k == "00") "0" else k) else cur + k
                t[t.size - 1] = cur
            } else {
                t.add(if (k == ".") "0." else if (k == "00") "0" else k)
            }
        }
        k in OPS -> {
            CalcState.fresh = false
            if (t.isEmpty()) t.add("0")
            if (t.last() in OPS) t[t.size - 1] = k else t.add(k)
        }
        k == "=" -> {
            if (t.isEmpty()) return
            val v = evaluate(t)
            CalcState.lastExpr = show(t) + " ="
            val r = if (v.isNaN() || v.isInfinite()) "0" else fmt(v, 10).replace(",", "")
            t.clear(); t.add(r)
            CalcState.fresh = true
        }
        k == "C" -> { t.clear(); CalcState.fresh = false }
        k == "⌫" -> {
            if (CalcState.fresh) { t.clear(); CalcState.fresh = false }
            else if (isNum) { val c = last!!.dropLast(1); if (c.isEmpty() || c == "-") t.removeAt(t.size - 1) else t[t.size - 1] = c }
            else if (t.isNotEmpty()) t.removeAt(t.size - 1)
        }
        k == "%" -> if (isNum) t[t.size - 1] = fmt(last!!.toDouble() / 100, 10).replace(",", "")
    }
}

// ---------------- 🧮 계산기 ----------------
@Composable
fun CalculatorScreen(onBack: () -> Unit, onExchange: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    var splitOpen by remember { mutableStateOf(false) }
    var tipOpen by remember { mutableStateOf(false) }
    var splitAmount by remember { mutableStateOf<Double?>(null) }
    val t = CalcState.tokens

    GameScaffold(s.calcTitle, onBack) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Column(
                Modifier.fillMaxWidth().weight(1f).padding(horizontal = 6.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End,
            ) {
                Text(if (CalcState.fresh) CalcState.lastExpr else show(t), color = WG.SubText, fontSize = 18.sp, textAlign = TextAlign.End)
                val main = when {
                    t.isEmpty() -> "0"
                    CalcState.fresh || t.size > 1 -> fmt(evaluate(t))
                    else -> fmtInput(t.last())
                }
                Text(main, color = WG.Text, fontSize = if (main.length > 12) 34.sp else 52.sp, fontWeight = FontWeight.Light, textAlign = TextAlign.End)
            }
            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BigButton(s.splitBtn, Modifier.weight(1f), color = WG.Surface2, contentColor = WG.Accent) { splitAmount = null; splitOpen = true }
                BigButton(s.tipBtn, Modifier.weight(1f), color = WG.Surface2, contentColor = WG.Accent) { tipOpen = true }
                BigButton(s.fxBtn, Modifier.weight(1f), color = WG.Surface2, contentColor = WG.Accent) { onExchange() }
            }
            val keys = listOf("C", "⌫", "%", "÷", "7", "8", "9", "×", "4", "5", "6", "−", "1", "2", "3", "+", "00", "0", ".", "=")
            for (row in keys.chunked(4)) {
                Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (k in row) {
                        val isOp = k in OPS || k == "="
                        val fn = k == "C" || k == "⌫" || k == "%"
                        Box(
                            Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (k == "=") WG.Accent else if (isOp) WG.Surface2 else if (fn) Color(0xFF3A3A50) else WG.Surface)
                                .clickable { vibe.tap(); press(k) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                k,
                                color = if (k == "=") Color.Black else if (isOp) WG.Accent else WG.Text,
                                fontSize = 26.sp,
                                fontWeight = if (isOp) FontWeight.ExtraBold else FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }
    }
    if (splitOpen) SplitDialog(splitAmount ?: CalcState.value()) { splitOpen = false }
    if (tipOpen) TipDialog(CalcState.value(), onSplit = { splitAmount = it; tipOpen = false; splitOpen = true }) { tipOpen = false }
}

// ---------------- 🍻 N빵 ----------------
@Composable
private fun SplitDialog(total: Double, onClose: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    val prefs = LocalContext.current.getSharedPreferences("wolfgames", Context.MODE_PRIVATE)
    var n by remember { mutableIntStateOf(prefs.getInt("split_n", 4)) }
    var unit by remember { mutableIntStateOf(prefs.getInt("split_unit", 100)) }
    Dialog(onDismissRequest = onClose) {
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(WG.Surface).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(s.splitTitle, color = WG.Text, fontSize = 22.sp, fontWeight = FontWeight.Black, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(WG.Bg).padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(s.splitTotal, color = WG.SubText, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Text(fmt(total, 2), color = WG.Text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Stepper(s.splitPeople, n, 2..30) { n = it; prefs.edit().putInt("split_n", it).apply() }
            Text(s.splitRound, color = WG.SubText, fontSize = 14.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (u in listOf(0, 10, 100, 1000)) {
                    Box(
                        Modifier.weight(1f).height(42.dp).clip(RoundedCornerShape(12.dp))
                            .background(if (unit == u) WG.Accent else WG.Surface2)
                            .clickable { vibe.tap(); unit = u; prefs.edit().putInt("split_unit", u).apply() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(if (u == 0) s.splitNone else fmt(u.toDouble()), color = if (unit == u) Color.Black else WG.Text, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (total <= 0) {
                Text(s.splitNeedTotal, color = WG.SubText, fontSize = 16.sp, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), textAlign = TextAlign.Center)
            } else {
                val raw = total / n
                val per = if (unit > 0) ceil(raw / unit - 1e-9) * unit else raw
                val collected = per * n
                val extra = collected - total
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(s.splitPer, color = WG.SubText, fontSize = 15.sp)
                    Text(fmt(per, if (unit > 0) 0 else 2), color = WG.Accent, fontSize = 44.sp, fontWeight = FontWeight.Black)
                    if (extra > 0.004) Text(s.splitCollect(fmt(collected, 2), fmt(extra, 2)), color = WG.SubText, fontSize = 14.sp)
                }
            }
            BigButton("OK", Modifier.fillMaxWidth()) { onClose() }
        }
    }
}

// ---------------- 💁 팁 ----------------
@Composable
private fun TipDialog(total: Double, onSplit: (Double) -> Unit, onClose: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    val prefs = LocalContext.current.getSharedPreferences("wolfgames", Context.MODE_PRIVATE)
    var pct by remember { mutableIntStateOf(prefs.getInt("tip_pct", 15)) }
    Dialog(onDismissRequest = onClose) {
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(WG.Surface).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(s.tipTitle, color = WG.Text, fontSize = 22.sp, fontWeight = FontWeight.Black, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(WG.Bg).padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(s.tipAmount, color = WG.SubText, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Text(fmt(total, 2), color = WG.Text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (p in listOf(5, 10, 15, 20, 25, 30)) {
                    Box(
                        Modifier.weight(1f).height(46.dp).clip(RoundedCornerShape(12.dp))
                            .background(if (pct == p) WG.Accent else WG.Surface2)
                            .clickable { vibe.tap(); pct = p; prefs.edit().putInt("tip_pct", p).apply() },
                        contentAlignment = Alignment.Center,
                    ) { Text("$p%", color = if (pct == p) Color.Black else WG.Text, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp) }
                }
            }
            if (total <= 0) {
                Text(s.splitNeedTotal, color = WG.SubText, fontSize = 16.sp, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), textAlign = TextAlign.Center)
            } else {
                val tip = total * pct / 100
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(s.tipLabel(pct), color = WG.SubText, fontSize = 15.sp)
                    Text(fmt(tip, 2), color = WG.Accent, fontSize = 44.sp, fontWeight = FontWeight.Black)
                }
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(WG.Bg).padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(s.tipTotal, color = WG.SubText, fontSize = 18.sp, modifier = Modifier.weight(1f))
                    Text(fmt(total + tip, 2), color = WG.Text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BigButton(s.tipSplit, Modifier.weight(1f), color = WG.Surface2, contentColor = WG.Accent) { onSplit(total * (1 + pct / 100.0)) }
                BigButton("OK", Modifier.weight(1f)) { onClose() }
            }
        }
    }
}

// ---------------- 💱 환전 ----------------
private val CURRENCIES = listOf(
    "KRW" to "🇰🇷", "USD" to "🇺🇸", "JPY" to "🇯🇵", "CNY" to "🇨🇳", "THB" to "🇹🇭", "VND" to "🇻🇳", "EUR" to "🇪🇺", "GBP" to "🇬🇧",
    "HKD" to "🇭🇰", "TWD" to "🇹🇼", "SGD" to "🇸🇬", "PHP" to "🇵🇭", "MYR" to "🇲🇾", "IDR" to "🇮🇩", "AUD" to "🇦🇺", "CAD" to "🇨🇦",
    "CHF" to "🇨🇭", "NZD" to "🇳🇿", "INR" to "🇮🇳", "MNT" to "🇲🇳", "LAK" to "🇱🇦", "KHR" to "🇰🇭", "RUB" to "🇷🇺", "AED" to "🇦🇪",
)
private val NO_DECIMALS = setOf("KRW", "JPY", "VND", "IDR", "MNT", "LAK", "KHR", "TWD")
private val HOME_CUR = mapOf("ko" to "KRW", "en" to "USD", "ja" to "JPY", "zh" to "CNY", "th" to "THB", "vi" to "VND")
private const val RATE_URL = "https://open.er-api.com/v6/latest/USD"

private class Rates(val rates: Map<String, Double>, val time: Long)

private fun parseRates(json: String): Rates {
    val j = JSONObject(json)
    val r = j.getJSONObject("rates")
    val m = mutableMapOf<String, Double>()
    for (k in r.keys()) m[k] = r.getDouble(k)
    return Rates(m, j.getLong("time_last_update_unix"))
}

private suspend fun download(): String = withContext(Dispatchers.IO) {
    val c = URL(RATE_URL).openConnection() as HttpURLConnection
    c.connectTimeout = 8000
    c.readTimeout = 8000
    try {
        c.inputStream.bufferedReader().use { it.readText() }
    } finally {
        c.disconnect()
    }
}

private fun flagOf(code: String) = CURRENCIES.firstOrNull { it.first == code }?.second ?: "🏳️"

private fun curName(code: String, lang: String): String =
    try { Currency.getInstance(code).getDisplayName(Locale(lang)) } catch (_: Exception) { code }

@Composable
fun ExchangeScreen(onBack: () -> Unit, langCode: String) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("wolfgames", Context.MODE_PRIVATE) }
    var from by remember { mutableStateOf(prefs.getString("fx_from", null) ?: HOME_CUR[langCode] ?: "USD") }
    var to by remember { mutableStateOf(prefs.getString("fx_to", null) ?: if (from == "USD") "KRW" else "USD") }
    if (to == from) to = if (from == "USD") "KRW" else "USD"
    var amount by remember { mutableStateOf(CalcState.value().let { if (it > 0) fmt(it, 2).replace(",", "") else "1" }) }
    var rates by remember { mutableStateOf<Rates?>(null) }
    var status by remember { mutableStateOf(s.fxLoading) }
    var statusColor by remember { mutableStateOf(WG.SubText) }
    var picking by remember { mutableStateOf<String?>(null) }
    var reload by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    fun save() = prefs.edit().putString("fx_from", from).putString("fx_to", to).apply()
    fun stamp(r: Rates) = SimpleDateFormat("M/d HH:mm", Locale(langCode)).format(Date(r.time * 1000))

    LaunchedEffect(reload) {
        status = s.fxLoading
        statusColor = WG.SubText
        val cached = prefs.getString("fx_json", null)
        val fetched = prefs.getLong("fx_fetched", 0L)
        val fresh = cached != null && System.currentTimeMillis() - fetched < 6 * 3600_000L
        if (fresh && reload == 0) {
            rates = parseRates(cached!!)
            status = s.fxUpdated(stamp(rates!!))
            return@LaunchedEffect
        }
        try {
            val json = download()
            val r = parseRates(json)
            prefs.edit().putString("fx_json", json).putLong("fx_fetched", System.currentTimeMillis()).apply()
            rates = r
            status = s.fxUpdated(stamp(r))
        } catch (_: Exception) {
            if (cached != null) {
                rates = parseRates(cached)
                status = s.fxOffline
                statusColor = WG.Accent
            } else {
                status = s.fxError
                statusColor = WG.Win
            }
        }
    }

    GameScaffold(s.fxTitle, onBack, actions = {
        VTextButton(onClick = { scope.launch { reload++ } }) { Text("↻ ${s.fxRefresh}", color = WG.Accent) }
    }) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val r = rates
            val rate = if (r != null) (r.rates[to] ?: Double.NaN) / (r.rates[from] ?: Double.NaN) else Double.NaN
            CurrencyBox(from, curName(from, langCode), onPick = { picking = "from" }) {
                BasicTextField(
                    value = amount,
                    onValueChange = { v -> amount = v.filter { it.isDigit() || it == '.' } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = TextStyle(color = WG.Text, fontSize = 34.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End),
                    cursorBrush = SolidColor(WG.Accent),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Box(
                Modifier.clip(RoundedCornerShape(14.dp)).background(WG.Surface2)
                    .clickable { vibe.tap(); val f = from; from = to; to = f; save() }
                    .padding(horizontal = 22.dp, vertical = 6.dp),
            ) { Text("⇅", color = WG.Accent, fontSize = 26.sp, fontWeight = FontWeight.Bold) }
            CurrencyBox(to, curName(to, langCode), onPick = { picking = "to" }) {
                val a = amount.toDoubleOrNull() ?: 0.0
                Text(
                    if (rate.isNaN()) "—" else fmt(a * rate, if (to in NO_DECIMALS) 0 else 2),
                    color = WG.Accent, fontSize = 34.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth(),
                )
            }
            if (!rate.isNaN()) Text("1 $from = ${fmt(rate, if (rate < 1) 6 else 4)} $to", color = WG.SubText, fontSize = 14.sp)
            Text(status, color = statusColor, fontSize = 13.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Text(s.fxNote, color = WG.SubText, fontSize = 12.sp, textAlign = TextAlign.Center)
            Text(
                "Rates By Exchange Rate API",
                color = WG.SubText, fontSize = 12.sp, textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.exchangerate-api.com"))) } catch (_: Exception) { }
                },
            )
        }
    }

    picking?.let { which ->
        Dialog(onDismissRequest = { picking = null }) {
            Column(
                Modifier.fillMaxWidth().heightIn(max = 560.dp).clip(RoundedCornerShape(20.dp)).background(WG.Surface)
                    .verticalScroll(rememberScrollState()).padding(10.dp),
            ) {
                Text(s.fxPick, color = WG.Text, fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(10.dp))
                for ((code, flag) in CURRENCIES.filter { rates == null || rates!!.rates.containsKey(it.first) }) {
                    val sel = (if (which == "from") from else to) == code
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(if (sel) WG.Surface2 else Color.Transparent)
                            .clickable {
                                vibe.tap()
                                if (which == "from") { if (to == code) to = from; from = code } else { if (from == code) from = to; to = code }
                                save()
                                picking = null
                            }
                            .padding(horizontal = 10.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(flag, fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(code, color = WG.Text, fontWeight = FontWeight.Bold, modifier = Modifier.width(48.dp))
                        Text(curName(code, langCode), color = WG.SubText, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyBox(code: String, name: String, onPick: () -> Unit, content: @Composable () -> Unit) {
    val vibe = LocalVibe.current
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(WG.Surface).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${flagOf(code)} $code ▾",
                color = WG.Text, fontSize = 16.sp,
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(WG.Bg).clickable { vibe.tap(); onPick() }.padding(horizontal = 12.dp, vertical = 8.dp),
            )
            Spacer(Modifier.weight(1f))
            Text(name, color = WG.SubText, fontSize = 13.sp, textAlign = TextAlign.End)
        }
        content()
    }
}

@Suppress("unused")
private fun Double.roundTo2() = (this * 100).roundToLong() / 100.0
