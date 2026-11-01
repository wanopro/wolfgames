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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.i18n.one
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.ResultOverlay
import com.wolfgames.app.ui.Text
import com.wolfgames.app.ui.WG
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToLong

// 🧾 여행 정산: 누가 얼마 냈는지 → 누가 누구한테 얼마 보내면 되는지 (이 휴대폰에만 저장) — 웹 settle.js 와 같음

data class Expense(val amt: Double, val by: Int, val forWho: List<Int>, val memo: String)
data class Transfer(val from: Int, val to: Int, val amt: Double)

/** 보낼 돈 계산 (큰 빚부터 큰 받을 돈에 맞춤) */
fun settleUp(n: Int, ex: List<Expense>): List<Transfer> {
    val bal = DoubleArray(n)
    for (e in ex) { val fr = e.forWho.filter { it < n }; if (fr.isEmpty() || e.amt <= 0 || e.by >= n) continue; bal[e.by] += e.amt; for (i in fr) bal[i] -= e.amt / fr.size }
    val deb = bal.withIndex().filter { it.value < -0.005 }.map { it.index to -it.value }.sortedByDescending { it.second }.map { arrayOf(it.first.toDouble(), it.second) }
    val cre = bal.withIndex().filter { it.value > 0.005 }.map { it.index to it.value }.sortedByDescending { it.second }.map { arrayOf(it.first.toDouble(), it.second) }
    val out = ArrayList<Transfer>(); var i = 0; var j = 0
    while (i < deb.size && j < cre.size) {
        val m = min(deb[i][1], cre[j][1])
        if (m > 0.005) out.add(Transfer(deb[i][0].toInt(), cre[j][0].toInt(), m))
        deb[i][1] -= m; cre[j][1] -= m
        if (deb[i][1] < 0.005) i++
        if (cre[j][1] < 0.005) j++
    }
    return out
}
fun fmtMoney(v: Double): String { val nf = NumberFormat.getInstance(); nf.maximumFractionDigits = 2; return nf.format((v * 100).roundToLong() / 100.0) }

private const val KEY = "settle_json"
private fun load(ctx: Context): Pair<List<String>, List<Expense>> {
    val raw = ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE).getString(KEY, null) ?: return listOf("P1", "P2", "P3") to emptyList()
    return try {
        val o = JSONObject(raw); val p = o.getJSONArray("people"); val e = o.getJSONArray("ex")
        val people = (0 until p.length()).map { p.getString(it) }
        val ex = (0 until e.length()).map { val x = e.getJSONObject(it); val f = x.getJSONArray("for"); Expense(x.getDouble("amt"), x.getInt("by"), (0 until f.length()).map { k -> f.getInt(k) }, x.optString("memo")) }
        if (people.size < 2) listOf("P1", "P2", "P3") to emptyList() else people to ex
    } catch (_: Exception) { listOf("P1", "P2", "P3") to emptyList() }
}
private fun save(ctx: Context, people: List<String>, ex: List<Expense>) {
    val o = JSONObject().put("people", JSONArray(people)).put("ex", JSONArray(ex.map { JSONObject().put("amt", it.amt).put("by", it.by).put("for", JSONArray(it.forWho)).put("memo", it.memo) }))
    ctx.getSharedPreferences("wolfgames", Context.MODE_PRIVATE).edit().putString(KEY, o.toString()).apply()
}

@Composable
private fun Field(value: String, hint: String, kb: KeyboardType, modifier: Modifier = Modifier, onChange: (String) -> Unit) {
    Box(modifier.clip(RoundedCornerShape(10.dp)).background(WG.Bg).padding(horizontal = 10.dp, vertical = 10.dp)) {
        if (value.isEmpty()) Text(hint, color = WG.SubText, fontSize = 15.sp)
        BasicTextField(value, onChange, singleLine = true, textStyle = TextStyle(color = WG.Text, fontSize = 16.sp), cursorBrush = SolidColor(WG.Accent), keyboardOptions = KeyboardOptions(keyboardType = kb), modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun Chip(label: String, on: Boolean, onClick: () -> Unit) {
    Text(label, color = if (on) Color.Black else WG.Text, fontSize = 14.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (on) WG.Accent else WG.Surface2).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 6.dp))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettleScreen(onBack: () -> Unit) {
    val s = LocalStrings.current; val vibe = LocalVibe.current; val ctx = LocalContext.current
    val init = remember { load(ctx) }
    val people = remember { mutableStateListOf(*init.first.toTypedArray()) }
    val ex = remember { mutableStateListOf(*init.second.toTypedArray()) }
    var by by remember { mutableIntStateOf(0) }
    val fr = remember { mutableStateListOf(*people.indices.toList().toTypedArray()) }
    var amt by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    var ask by remember { mutableStateOf(false) }
    fun commit() { save(ctx, people.toList(), ex.toList()) }
    if (by >= people.size) by = 0
    val total = ex.sumOf { it.amt }
    val share = DoubleArray(people.size).also { a -> ex.forEach { e -> e.forWho.forEach { i -> if (i < a.size) a[i] += e.amt / e.forWho.size } } }
    val out = settleUp(people.size, ex)
    Box(Modifier.fillMaxSize()) {
        GameScaffold("🧾 ${s.stTitle}", onBack) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(s.stDesc, color = WG.SubText, fontSize = 15.sp, lineHeight = 22.sp)
                Text(s.stPeople, color = WG.SubText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    people.forEachIndexed { i, p ->
                        Row(Modifier.clip(RoundedCornerShape(12.dp)).background(WG.Surface).padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.width(76.dp)) {
                                BasicTextField(p, { v -> people[i] = v.take(12); commit() }, singleLine = true, textStyle = TextStyle(color = WG.Text, fontSize = 15.sp, fontWeight = FontWeight.Bold), cursorBrush = SolidColor(WG.Accent), modifier = Modifier.padding(vertical = 8.dp))
                            }
                            if (people.size > 2) Text("✕", color = WG.SubText, fontSize = 15.sp, modifier = Modifier.clickable {
                                vibe.tap(); people.removeAt(i)
                                val kept = ex.filter { it.by != i }.map { e -> e.copy(by = if (e.by > i) e.by - 1 else e.by, forWho = e.forWho.filter { it != i }.map { if (it > i) it - 1 else it }) }.filter { it.forWho.isNotEmpty() }
                                ex.clear(); ex.addAll(kept); fr.clear(); fr.addAll(people.indices); commit()
                            }.padding(horizontal = 8.dp, vertical = 6.dp))
                        }
                    }
                    if (people.size < 12) Chip(s.stAddPerson, false) { vibe.tap(); people.add("P${people.size + 1}"); fr.add(people.size - 1); commit() }
                }
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(WG.Surface).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Field(amt, one(s.stAmount), KeyboardType.Decimal, Modifier.fillMaxWidth()) { amt = it }
                    Field(memo, one(s.stMemo), KeyboardType.Text, Modifier.fillMaxWidth()) { memo = it.take(30) }
                    Text(s.stPaidBy, color = WG.SubText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { people.forEachIndexed { i, p -> Chip(p, by == i) { vibe.tap(); by = i } } }
                    Text(s.stFor, color = WG.SubText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        people.forEachIndexed { i, p -> Chip(p, i in fr) { vibe.tap(); if (i in fr) { if (fr.size > 1) fr.remove(i) } else { fr.add(i); fr.sort() } } }
                    }
                    BigButton(s.stAdd, Modifier.fillMaxWidth()) {
                        val v = amt.replace(",", "").toDoubleOrNull() ?: 0.0
                        if (v > 0) { ex.add(Expense(v, by, fr.filter { it < people.size }.ifEmpty { people.indices.toList() }, memo.trim())); amt = ""; memo = ""; commit() }
                    }
                }
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFF1F3A2C)).padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${s.stResult} · ${s.stTotal(fmtMoney(total))}", color = WG.SubText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    if (ex.isNotEmpty()) {
                        if (out.isEmpty()) Text(s.stEven, color = WG.Text, fontSize = 18.sp)
                        out.forEach { o ->
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("${people.getOrElse(o.from) { "?" }} → ${people.getOrElse(o.to) { "?" }}", color = WG.Text, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Text(fmtMoney(o.amt), color = Color(0xFF69F0AE), fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Text(people.indices.joinToString("  ·  ") { "${people[it]} ${s.stSpent(fmtMoney(share.getOrElse(it) { 0.0 }))}" }, color = WG.SubText, fontSize = 13.sp, lineHeight = 19.sp)
                    }
                }
                Text(s.stList, color = WG.SubText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (ex.isEmpty()) Text(s.stEmpty, color = WG.SubText, fontSize = 15.sp)
                ex.indices.reversed().forEach { k ->
                    val e = ex[k]
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(WG.Surface).padding(start = 12.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${fmtMoney(e.amt)}  ${e.memo}", color = WG.Text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("${people.getOrElse(e.by) { "?" }} → ${if (e.forWho.size == people.size) "👥" else e.forWho.joinToString(", ") { people.getOrElse(it) { "?" } }}", color = WG.SubText, fontSize = 13.sp)
                        }
                        Text("🗑️", fontSize = 16.sp, modifier = Modifier.clickable { vibe.tap(); ex.removeAt(k); commit() }.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
                if (ex.isNotEmpty()) BigButton(s.stReset, Modifier.fillMaxWidth(), color = WG.Surface2, contentColor = WG.Text) { ask = true }
                Spacer(Modifier.padding(8.dp))
            }
        }
        if (ask) ResultOverlay(s.stResetAsk, null, s.ok, onPrimary = {
            ask = false; people.clear(); people.addAll(listOf("P1", "P2", "P3")); ex.clear(); fr.clear(); fr.addAll(people.indices); commit()
        }, secondary = s.cancel, onSecondary = { ask = false })
    }
}
