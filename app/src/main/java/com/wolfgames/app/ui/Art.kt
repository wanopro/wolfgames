package com.wolfgames.app.ui

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

// 공용 그림 (웹 web/art.js 와 같은 그림): 귀여운 늑대(달리기·사다리·콱 물기·해적 두건), 동물 얼굴 12종, 폭탄

private const val INK = "#2B2B33"
private val SIDES = listOf(-1f, 1f)
private const val PIf = PI.toFloat()

/** 귀여운 회색 늑대 (오른쪽을 보고 달림). (x,y)=땅, s=크기, ph=달리기 위상, run 0~1, open 0~1(입 벌림) */
fun Ctx.cuteWolf(x: Float, y: Float, s: Float, ph: Float, run: Float = 1f, open: Float = 0f, happy: Boolean = false) {
    val fur = "#9AA3AF"; val dark = "#6E7784"; val light = "#F1EEE8"; val pink = "#F6AFC0"; val ol = "rgba(0,0,0,.35)"
    val lw = max(1.2f, s * 0.025f)
    val bob = run * abs(sin(ph)) * s * 0.06f
    save(); translate(x, y - bob)
    fun m(u: Float, v: Float) = moveTo(u * s, v * s)
    fun l(u: Float, v: Float) = lineTo(u * s, v * s)
    fun q(a: Float, b: Float, u: Float, v: Float) = quadraticCurveTo(a * s, b * s, u * s, v * s)
    fun oval(u: Float, v: Float, rx: Float, ry: Float, c: String, st: Boolean = false, rot: Float = 0f) {
        beginPath(); ellipse(u * s, v * s, rx * s, ry * s, rot, 0f, TAU); fillStyle = c; fill()
        if (st) { lineWidth = lw; strokeStyle = ol; stroke() }
    }
    fun leg(hu: Float, hv: Float, ang: Float, c: String) {
        save(); translate(hu * s, hv * s); rotate(ang)
        strokeStyle = c; lineWidth = s * 0.12f; beginPath(); moveTo(0f, 0f); lineTo(0f, s * 0.2f); stroke()
        beginPath(); ellipse(s * 0.02f, s * 0.22f, s * 0.075f, s * 0.05f, 0f, 0f, TAU); fillStyle = light; fill(); lineWidth = lw; strokeStyle = ol; stroke()
        restore()
    }
    val sw = run * 0.7f
    leg(-0.28f, -0.26f, -sin(ph + 0.6f) * sw, dark)
    leg(0.2f, -0.26f, sin(ph + 0.6f) * sw, dark)
    // 꼬리
    val tw = sin(ph * 0.5f) * 0.15f * (0.5f + run)
    save(); translate(-0.42f * s, -0.42f * s); rotate(-0.5f + tw)
    oval(-0.2f, 0f, 0.24f, 0.12f, fur, true); oval(-0.38f, 0f, 0.08f, 0.075f, light)
    restore()
    // 몸통
    oval(-0.05f, -0.38f, 0.42f, 0.21f, fur, true)
    oval(-0.1f, -0.5f, 0.26f, 0.07f, dark)
    oval(0f, -0.28f, 0.27f, 0.08f, light)
    leg(-0.32f, -0.28f, sin(ph) * sw, fur)
    leg(0.16f, -0.28f, -sin(ph) * sw, fur)
    // 머리, 귀
    val hx = 0.4f; val hy = -0.7f; val hr = 0.27f
    for ((ex, c) in listOf(hx - 0.14f to dark, hx + 0.04f to fur)) {
        beginPath(); m(ex - 0.09f, hy - 0.16f); l(ex - 0.01f, hy - 0.44f); l(ex + 0.1f, hy - 0.17f); closePath()
        fillStyle = c; fill(); lineWidth = lw; strokeStyle = ol; stroke()
        beginPath(); m(ex - 0.045f, hy - 0.19f); l(ex - 0.01f, hy - 0.35f); l(ex + 0.05f, hy - 0.2f); closePath(); fillStyle = pink; fill()
    }
    beginPath(); m(hx - 0.2f, hy + 0.12f); l(hx - 0.17f, hy + 0.3f); l(hx - 0.07f, hy + 0.2f); l(hx - 0.02f, hy + 0.32f); l(hx + 0.06f, hy + 0.2f); closePath()
    fillStyle = fur; fill(); lineWidth = lw; strokeStyle = ol; stroke()
    beginPath(); arc(hx * s, hy * s, hr * s, 0f, TAU); fillStyle = fur; fill(); lineWidth = lw; strokeStyle = ol; stroke()
    beginPath(); m(hx - 0.02f, hy - 0.26f); q(hx + 0.08f, hy - 0.14f, hx + 0.2f, hy - 0.08f); q(hx + 0.06f, hy - 0.1f, hx - 0.02f, hy - 0.26f); fillStyle = light; fill()
    // 아래턱
    val jx = hx + 0.12f; val jy = hy + 0.08f; val a = open * 0.7f
    save(); translate(jx * s, jy * s); rotate(a); translate(-jx * s, -jy * s)
    oval(jx + 0.13f, jy + 0.05f, 0.14f, 0.05f, light, true)
    if (open > 0.05f) {
        fillStyle = "#fff"
        for (u in listOf(0.1f, 0.18f)) { beginPath(); m(jx + u, jy + 0.01f); l(jx + u + 0.02f, jy - 0.035f); l(jx + u + 0.04f, jy + 0.01f); fill() }
        oval(jx + 0.1f, jy + 0.025f, 0.07f, 0.018f, "#E86A86")
    }
    restore()
    if (open > 0.05f) {
        beginPath(); m(jx - 0.02f, jy); l(jx + 0.27f, jy - 0.03f - open * 0.04f); l(jx + 0.27f * cos(a), jy + 0.27f * sin(a) + 0.03f); closePath()
        fillStyle = "#8E2440"; fill()
    }
    // 위턱(코)
    save(); translate(jx * s, jy * s); rotate(-a * 0.3f); translate(-jx * s, -jy * s)
    beginPath(); m(jx - 0.06f, jy - 0.13f); q(jx + 0.14f, jy - 0.16f, jx + 0.27f, jy - 0.07f)
    q(jx + 0.31f, jy, jx + 0.24f, jy + 0.02f); l(jx - 0.04f, jy + 0.03f); closePath()
    fillStyle = light; fill(); lineWidth = lw; strokeStyle = ol; stroke()
    fillStyle = "#fff"; beginPath(); m(jx + 0.17f, jy + 0.02f); l(jx + 0.19f, jy + 0.08f); l(jx + 0.21f, jy + 0.02f); fill()
    oval(jx + 0.27f, jy - 0.065f, 0.05f, 0.04f, INK, false, 0.3f)
    oval(jx + 0.26f, jy - 0.08f, 0.013f, 0.009f, "rgba(255,255,255,.7)")
    restore()
    // 눈
    val ex = hx + 0.08f; val ey = hy - 0.06f
    if (happy || (open > 0.5f && run == 0f)) {
        beginPath(); m(ex - 0.05f, ey - 0.05f); l(ex + 0.03f, ey); l(ex - 0.05f, ey + 0.05f); strokeStyle = INK; lineWidth = s * 0.03f; stroke()
    } else {
        oval(ex, ey, 0.07f, 0.075f, "#fff"); oval(ex + 0.015f, ey + 0.005f, 0.05f, 0.055f, INK); oval(ex, ey - 0.02f, 0.02f, 0.02f, "#fff")
    }
    if (open > 0.3f && !happy) { beginPath(); m(ex - 0.08f, ey - 0.12f); l(ex + 0.06f, ey - 0.07f); strokeStyle = INK; lineWidth = s * 0.03f; stroke() }
    oval(hx - 0.02f, hy + 0.1f, 0.06f, 0.035f, "rgba(255,143,163,.6)")
    restore()
}

/** 동물 12종 (이름은 문구 animalNames 순서와 같음) */
val ANIMALS = listOf("rabbit", "pig", "chicken", "sheep", "deer", "mouse", "duck", "cow", "hamster", "chick", "dog", "cat")

/** 동물 얼굴. st: 0 보통 / 1 겁먹음 / 2 물림(X눈) */
fun Ctx.animalFace(x: Float, y: Float, r: Float, kind: String, st: Int = 0) {
    val ol = "rgba(0,0,0,.32)"; val lw = max(1.2f, r * 0.06f)
    save(); translate(x, y)
    fun c(u: Float, v: Float, rr: Float, col: String, stroke: Boolean = true) { beginPath(); arc(u * r, v * r, rr * r, 0f, TAU); fillStyle = col; fill(); if (stroke) { lineWidth = lw; strokeStyle = ol; stroke() } }
    fun e(u: Float, v: Float, rx: Float, ry: Float, col: String, stroke: Boolean = false, rot: Float = 0f) { beginPath(); ellipse(u * r, v * r, rx * r, ry * r, rot, 0f, TAU); fillStyle = col; fill(); if (stroke) { lineWidth = lw; strokeStyle = ol; stroke() } }
    fun tri(pts: List<Pair<Float, Float>>, col: String, stroke: Boolean = true) {
        beginPath(); pts.forEachIndexed { i, (u, v) -> if (i == 0) moveTo(u * r, v * r) else lineTo(u * r, v * r) }; closePath(); fillStyle = col; fill()
        if (stroke) { lineWidth = lw; strokeStyle = ol; stroke() }
    }
    var head = "#F4F1EC"; val eyeY = -0.08f; val cheek = "rgba(255,140,160,.55)"
    when (kind) {
        "rabbit" -> for (s in SIDES) { e(s * 0.32f, -1.05f, 0.2f, 0.55f, head, true, s * 0.15f); e(s * 0.32f, -1.0f, 0.1f, 0.4f, "#F6AFC0", false, s * 0.15f) }
        "pig" -> { head = "#F7B3C2"; for (s in SIDES) tri(listOf(s * 0.35f to -0.75f, s * 0.85f to -0.95f, s * 0.75f to -0.4f), "#EE97AA") }
        "chicken" -> for (k in listOf(-0.25f, 0f, 0.25f)) c(k, -1.0f + abs(k) * 0.3f, 0.2f, "#E53935")
        "sheep" -> { head = "#F3E3C8"; for (k in 0 until 9) { val a = PIf * (0.95f + k * 0.14f); c(cos(a) * 0.82f, sin(a) * 0.72f - 0.15f, 0.3f, "#FFFFFF") }; for (s in SIDES) e(s * 0.95f, -0.1f, 0.25f, 0.12f, "#5E4B3C", true, s * 0.3f) }
        "deer" -> {
            head = "#C98A55"
            strokeStyle = "#7A4E2D"; lineWidth = r * 0.1f
            for (s in SIDES) { beginPath(); moveTo(s * 0.3f * r, -0.8f * r); lineTo(s * 0.45f * r, -1.35f * r); moveTo(s * 0.38f * r, -1.05f * r); lineTo(s * 0.7f * r, -1.2f * r); moveTo(s * 0.43f * r, -1.25f * r); lineTo(s * 0.25f * r, -1.45f * r); stroke() }
            for (s in SIDES) { e(s * 0.85f, -0.45f, 0.35f, 0.16f, head, true, s * -0.5f); e(s * 0.85f, -0.45f, 0.2f, 0.08f, "#F2C6A0", false, s * -0.5f) }
        }
        "mouse" -> { head = "#BCC1CA"; for (s in SIDES) { c(s * 0.7f, -0.7f, 0.45f, head); c(s * 0.7f, -0.7f, 0.28f, "#F6AFC0", false) } }
        "duck" -> { head = "#FFFFFF"; c(0f, -0.95f, 0.12f, "#FFFFFF") }
        "cow" -> { head = "#FFFFFF"; for (s in SIDES) { tri(listOf(s * 0.45f to -0.75f, s * 0.7f to -1.15f, s * 0.6f to -0.7f), "#F2E3B3"); e(s * 0.95f, -0.4f, 0.3f, 0.14f, "#FFFFFF", true, s * -0.35f); e(s * 0.95f, -0.4f, 0.16f, 0.07f, "#F6AFC0", false, s * -0.35f) } }
        "hamster" -> { head = "#EBA762"; for (s in SIDES) { c(s * 0.62f, -0.72f, 0.25f, head); c(s * 0.62f, -0.72f, 0.13f, "#F6AFC0", false) } }
        "dog" -> { head = "#E3B07A"; for (s in SIDES) e(s * 0.82f, -0.2f, 0.26f, 0.5f, "#9A6A43", true, s * -0.35f) }
        "cat" -> { head = "#F3A451"; for (s in SIDES) { tri(listOf(s * 0.25f to -0.8f, s * 0.78f to -1.15f, s * 0.85f to -0.45f), head); tri(listOf(s * 0.4f to -0.78f, s * 0.72f to -0.98f, s * 0.75f to -0.6f), "#F6AFC0", false) } }
        "chick" -> { head = "#FFD54A"; strokeStyle = "#E6A800"; lineWidth = r * 0.08f; for (k in listOf(-0.12f, 0.05f, 0.18f)) { beginPath(); moveTo(0f, -0.9f * r); quadraticCurveTo(k * r, -1.2f * r, k * 1.6f * r, -1.25f * r); stroke() } }
    }
    c(0f, 0f, 0.95f, head)
    when (kind) {
        "cow" -> { e(-0.45f, -0.45f, 0.28f, 0.22f, INK, false, 0.4f); e(0.55f, 0.05f, 0.2f, 0.16f, INK, false, -0.3f) }
        "hamster" -> e(0f, 0.35f, 0.62f, 0.45f, "#FFF6EA")
        "deer" -> { e(0f, 0.35f, 0.5f, 0.4f, "#F2D3B0"); for ((u, v) in listOf(-0.5f to -0.5f, 0.45f to -0.6f, 0.6f to -0.2f)) c(u, v, 0.07f, "#F2D3B0", false) }
        "dog" -> { e(0f, 0.32f, 0.48f, 0.36f, "#FFF1DE"); e(0.42f, -0.4f, 0.2f, 0.17f, "#B98557", false, 0.3f) }
        "cat" -> { for (k in listOf(-0.18f, 0f, 0.18f)) { strokeStyle = "#C9712A"; lineWidth = r * 0.08f; beginPath(); moveTo(k * r, -0.92f * r); lineTo(k * r * 0.8f, -0.62f * r); stroke() }; e(0f, 0.33f, 0.42f, 0.28f, "#FFF1DE") }
        "sheep" -> for (k in 0 until 5) c(-0.5f + k * 0.25f, -0.75f, 0.2f, "#FFFFFF")
    }
    for (s in SIDES) {
        val ex = s * 0.36f; val ey = eyeY
        when (st) {
            2 -> { strokeStyle = INK; lineWidth = r * 0.1f; beginPath(); moveTo((ex - 0.12f) * r, (ey - 0.12f) * r); lineTo((ex + 0.12f) * r, (ey + 0.12f) * r); moveTo((ex + 0.12f) * r, (ey - 0.12f) * r); lineTo((ex - 0.12f) * r, (ey + 0.12f) * r); stroke() }
            1 -> { c(ex, ey, 0.2f, "#fff"); c(ex + s * 0.03f, ey + 0.02f, 0.07f, INK, false) }
            else -> { c(ex, ey, 0.13f, INK, false); c(ex - 0.04f, ey - 0.05f, 0.045f, "#fff", false) }
        }
    }
    if (kind != "sheep") for (s in SIDES) e(s * 0.58f, 0.22f, 0.17f, 0.09f, cheek)
    when (kind) {
        "pig" -> { e(0f, 0.3f, 0.32f, 0.22f, "#EE97AA", true); e(-0.11f, 0.3f, 0.06f, 0.09f, "#B5566E"); e(0.11f, 0.3f, 0.06f, 0.09f, "#B5566E") }
        "cow" -> { e(0f, 0.45f, 0.48f, 0.3f, "#F6B8C6", true); e(-0.17f, 0.45f, 0.07f, 0.1f, "#B5566E"); e(0.17f, 0.45f, 0.07f, 0.1f, "#B5566E") }
        "chicken" -> { tri(listOf(-0.16f to 0.12f, 0.16f to 0.12f, 0f to 0.42f), "#FFB300"); e(0f, 0.55f, 0.1f, 0.16f, "#E53935") }
        "duck" -> { e(0f, 0.32f, 0.4f, 0.18f, "#FF9800", true); strokeStyle = "rgba(0,0,0,.3)"; lineWidth = lw; beginPath(); moveTo(-0.35f * r, 0.32f * r); lineTo(0.35f * r, 0.32f * r); stroke() }
        "chick" -> tri(listOf(-0.14f to 0.12f, 0.14f to 0.12f, 0f to 0.36f), "#FF9800")
        "deer" -> e(0f, 0.2f, 0.14f, 0.1f, INK)
        "dog" -> {
            e(0f, 0.2f, 0.16f, 0.11f, INK); strokeStyle = INK; lineWidth = r * 0.06f
            beginPath(); moveTo(0f, 0.3f * r); quadraticCurveTo(-0.1f * r, 0.44f * r, -0.22f * r, 0.38f * r); moveTo(0f, 0.3f * r); quadraticCurveTo(0.1f * r, 0.44f * r, 0.22f * r, 0.38f * r); stroke()
            if (st == 0) e(0.08f, 0.5f, 0.09f, 0.12f, "#FF7F98")
        }
        else -> {
            tri(listOf(-0.1f to 0.13f, 0.1f to 0.13f, 0f to 0.25f), if (kind == "sheep") "#7A5A48" else "#F48FA5", false)
            strokeStyle = INK; lineWidth = r * 0.06f; beginPath(); moveTo(0f, 0.25f * r); quadraticCurveTo(-0.08f * r, 0.38f * r, -0.18f * r, 0.33f * r); moveTo(0f, 0.25f * r); quadraticCurveTo(0.08f * r, 0.38f * r, 0.18f * r, 0.33f * r); stroke()
            if (kind != "sheep") { strokeStyle = "rgba(0,0,0,.35)"; lineWidth = r * 0.03f; for (s in SIDES) for (dy in listOf(-0.04f, 0.06f)) { beginPath(); moveTo(s * 0.3f * r, (0.22f + dy) * r); lineTo(s * 0.75f * r, (0.18f + dy * 1.8f) * r); stroke() } }
        }
    }
    if (st == 1 && kind != "chicken" && kind != "duck") { val my = if (kind == "pig" || kind == "cow") 0.82f else 0.6f; e(0f, my, 0.1f, 0.12f, "#8E2440") }
    restore()
}

/** 사다리 타는 귀여운 늑대 얼굴. 입을 앙앙 벌렸다 닫았다. dir: -1 왼쪽, 0 아래, 1 오른쪽. bite: 지정하면 입 벌림 고정 */
fun Ctx.ladderWolf(x: Float, y: Float, r: Float, t: Float, color: String, dir: Float = 0f, bite: Float? = null) {
    val fur = "#9AA5B4"; val dark = "#6E7A8A"; val cream = "#F4ECE1"; val ol = "rgba(0,0,0,.3)"
    val ch = bite ?: (0.5f + 0.5f * sin(t * 20f)).pow(1.5f)
    val lw = max(1f, r * 0.05f)
    save(); translate(x, y); rotate(dir * 0.22f + sin(t * 9f) * 0.06f)
    for (s in SIDES) {
        val e = sin(t * 9f + if (s > 0) 0f else 3f) * 0.07f
        save(); translate(s * r * 0.55f, -r * 0.6f); rotate(s * (0.25f + e))
        beginPath(); moveTo(-r * 0.3f, r * 0.15f); lineTo(0f, -r * 0.6f); lineTo(r * 0.3f, r * 0.15f); closePath(); fillStyle = dark; fill(); lineWidth = lw; strokeStyle = ol; stroke()
        beginPath(); moveTo(-r * 0.15f, r * 0.08f); lineTo(0f, -r * 0.38f); lineTo(r * 0.15f, r * 0.08f); closePath(); fillStyle = "#F6AFC0"; fill()
        restore()
    }
    beginPath()
    moveTo(-r * 0.95f, -r * 0.1f); quadraticCurveTo(-r * 0.95f, -r * 0.95f, 0f, -r * 0.95f); quadraticCurveTo(r * 0.95f, -r * 0.95f, r * 0.95f, -r * 0.1f)
    lineTo(r * 1.08f, r * 0.25f); lineTo(r * 0.82f, r * 0.3f); lineTo(r * 0.92f, r * 0.55f); lineTo(r * 0.55f, r * 0.6f)
    quadraticCurveTo(0f, r * 0.98f, -r * 0.55f, r * 0.6f); lineTo(-r * 0.92f, r * 0.55f); lineTo(-r * 0.82f, r * 0.3f); lineTo(-r * 1.08f, r * 0.25f); closePath()
    fillStyle = fur; fill(); lineWidth = lw; strokeStyle = ol; stroke()
    beginPath(); moveTo(-r * 0.16f, -r * 0.93f); lineTo(0f, -r * 0.45f); lineTo(r * 0.16f, -r * 0.93f); closePath(); fillStyle = cream; globalAlpha = 0.85f; fill(); globalAlpha = 1f
    beginPath(); ellipse(0f, r * 0.88f, r * 0.72f, r * 0.2f, 0f, 0f, TAU); fillStyle = color; fill(); lineWidth = lw; strokeStyle = ol; stroke()
    beginPath(); moveTo(r * 0.35f, r * 0.95f); lineTo(r * 0.62f, r * 1.35f); lineTo(r * 0.18f, r * 1.05f); closePath(); fillStyle = color; fill()
    beginPath(); ellipse(0f, r * 0.4f, r * 0.46f, r * 0.34f, 0f, 0f, TAU); fillStyle = cream; fill()
    val lx = dir * r * 0.05f; val ey = -r * 0.14f
    for (s in SIDES) {
        beginPath(); arc(s * r * 0.4f + lx, ey, r * 0.19f, 0f, TAU); fillStyle = INK; fill()
        beginPath(); arc(s * r * 0.4f + lx - r * 0.06f, ey - r * 0.07f, r * 0.075f, 0f, TAU); fillStyle = "#fff"; fill()
        beginPath(); arc(s * r * 0.4f + lx + r * 0.06f, ey + r * 0.06f, r * 0.035f, 0f, TAU); fill()
    }
    for (s in SIDES) { beginPath(); ellipse(s * r * 0.63f, r * 0.2f, r * 0.18f, r * 0.1f, 0f, 0f, TAU); fillStyle = "rgba(255,120,150,.55)"; fill() }
    beginPath(); ellipse(0f, r * 0.2f, r * 0.14f, r * 0.095f, 0f, 0f, TAU); fillStyle = INK; fill()
    beginPath(); arc(-r * 0.04f, r * 0.17f, r * 0.03f, 0f, TAU); fillStyle = "rgba(255,255,255,.6)"; fill()
    val mh = r * (0.04f + 0.28f * ch); val mw = r * (0.22f + 0.06f * ch); val my = r * 0.46f; val mr = max(r * 0.03f, mh * 0.6f)
    beginPath(); ellipse(0f, my + mh * 0.5f, mw, mr, 0f, 0f, TAU); fillStyle = "#8E2440"; fill()
    if (ch > 0.3f) { beginPath(); ellipse(0f, my + mh * 0.85f, mw * 0.6f, mh * 0.25f, 0f, 0f, TAU); fillStyle = "#FF7F98"; fill() }
    fillStyle = "#fff"
    for (s in SIDES) { val top = my + mh * 0.5f - mr; beginPath(); moveTo(s * mw * 0.75f - r * 0.05f, top + 1f); lineTo(s * mw * 0.75f + r * 0.05f, top + 1f); lineTo(s * mw * 0.75f, top + r * 0.11f); fill() }
    restore()
}

/** 폭탄 (fuse: 남은 심지 0~1, t: 초) */
fun Ctx.bombArt(x: Float, y: Float, r: Float, fuse: Float, t: Float) {
    save(); translate(x, y)
    beginPath(); arc(0f, 0f, r, 0f, TAU); radialFill(-r * 0.35f, -r * 0.35f, r * 0.1f, r * 1.3f, listOf(0f to "#5A5F6B", 1f to "#16181D")); fill()
    beginPath(); ellipse(-r * 0.35f, -r * 0.4f, r * 0.22f, r * 0.12f, -0.6f, 0f, TAU); fillStyle = "rgba(255,255,255,.35)"; fill()
    fillStyle = "#3A3E47"; fillRect(-r * 0.25f, -r * 1.15f, r * 0.5f, r * 0.3f)
    val pts = (0..20).map { k -> val u = k / 20f; (r * 0.05f + sin(u * 5f) * r * 0.25f + u * r * 0.5f) to (-r * 1.15f - u * r * 0.9f) }
    val m = max(1, (fuse * 20).roundToInt())
    beginPath(); for (k in 0..m) { val (px, py) = pts[k]; if (k == 0) moveTo(px, py) else lineTo(px, py) }; strokeStyle = "#C8A26A"; lineWidth = r * 0.12f; stroke()
    val (fx, fy) = pts[m]
    for (k in 0 until 8) {
        val a = k * PIf / 4f + t * 20f; val l = r * (0.25f + 0.2f * abs(sin(t * 30f + k)))
        beginPath(); moveTo(fx, fy); lineTo(fx + cos(a) * l, fy + sin(a) * l); strokeStyle = if (k % 2 == 1) "#FFD54F" else "#FF7043"; lineWidth = r * 0.07f; stroke()
    }
    beginPath(); arc(fx, fy, r * 0.14f * (1f + 0.3f * sin(t * 40f)), 0f, TAU); fillStyle = "#FFF59D"; fill()
    restore()
}

/** 앞모습 늑대가 콱 무는 얼굴. bite: 0 닫힘 ~ 1 쩍 */
fun Ctx.chompWolf(x: Float, y: Float, r: Float, bite: Float, pop: Float = 1f) {
    save(); translate(x, y); scale(pop, pop)
    for (s in SIDES) {
        beginPath(); moveTo(s * r * 0.25f, -r * 0.7f); lineTo(s * r * 0.85f, -r * 1.35f); lineTo(s * r * 0.95f, -r * 0.35f); closePath(); fillStyle = "#6E7A8A"; fill()
        beginPath(); moveTo(s * r * 0.45f, -r * 0.68f); lineTo(s * r * 0.8f, -r * 1.1f); lineTo(s * r * 0.85f, -r * 0.5f); closePath(); fillStyle = "#F6AFC0"; fill()
    }
    beginPath(); arc(0f, 0f, r, 0f, TAU); fillStyle = "#9AA5B4"; fill()
    for (s in SIDES) {
        beginPath(); arc(s * r * 0.4f, -r * 0.2f, r * 0.14f, 0f, TAU); fillStyle = INK; fill()
        beginPath(); arc(s * r * 0.36f, -r * 0.25f, r * 0.05f, 0f, TAU); fillStyle = "#fff"; fill()
        beginPath(); moveTo(s * r * 0.6f, -r * 0.48f); lineTo(s * r * 0.2f, -r * 0.36f); strokeStyle = INK; lineWidth = r * 0.08f; stroke()
    }
    val o = r * 0.55f * bite
    beginPath(); ellipse(0f, r * 0.45f, r * 0.62f, r * 0.12f + o * 0.7f, 0f, 0f, TAU); fillStyle = "#7A1E2E"; fill()
    fillStyle = "#fff"
    for (k in -3..3) {
        val x0 = k * r * 0.16f
        beginPath(); moveTo(x0 - r * 0.07f, r * 0.45f - r * 0.1f - o * 0.6f); lineTo(x0 + r * 0.07f, r * 0.45f - r * 0.1f - o * 0.6f); lineTo(x0, r * 0.45f + r * 0.06f - o * 0.6f); fill()
        beginPath(); moveTo(x0 - r * 0.07f, r * 0.45f + r * 0.1f + o * 0.6f); lineTo(x0 + r * 0.07f, r * 0.45f + r * 0.1f + o * 0.6f); lineTo(x0, r * 0.45f - r * 0.06f + o * 0.6f); fill()
    }
    beginPath(); ellipse(0f, r * 0.12f - o * 0.25f, r * 0.17f, r * 0.11f, 0f, 0f, TAU); fillStyle = INK; fill()
    restore()
}

/** 해적 두건 늑대 머리. mode: "idle" | "tease"(t초, 메롱) | "sur"(당첨으로 튀어나옴) */
fun Ctx.pirateHead2(cx: Float, cy: Float, r: Float, mode: String, t: Float = 0f) {
    val tease = mode == "tease"; val sur = mode == "sur"
    for (sx in SIDES) {
        val e = if (tease) sin(t * 14f + if (sx > 0) 0f else 3f) * 0.06f else 0f
        save(); translate(cx + sx * r * 0.6f, cy - r * 0.85f); rotate(sx * e); translate(-(cx + sx * r * 0.6f), -(cy - r * 0.85f))
        beginPath(); moveTo(cx + sx * r * 0.25f, cy - r * 0.7f); lineTo(cx + sx * r * 0.85f, cy - r * 1.35f); lineTo(cx + sx * r * 0.95f, cy - r * 0.35f); closePath(); fillStyle = "#6E7A8A"; fill()
        beginPath(); moveTo(cx + sx * r * 0.45f, cy - r * 0.68f); lineTo(cx + sx * r * 0.8f, cy - r * 1.1f); lineTo(cx + sx * r * 0.85f, cy - r * 0.5f); closePath(); fillStyle = "#F6AFC0"; fill()
        restore()
    }
    beginPath(); arc(cx, cy, r, 0f, TAU); fillStyle = "#9AA5B4"; fill()
    beginPath(); moveTo(cx - r * 0.97f, cy - r * 0.25f); quadraticCurveTo(cx, cy - r * 1.25f, cx + r * 0.97f, cy - r * 0.25f); quadraticCurveTo(cx, cy - r * 0.75f, cx - r * 0.97f, cy - r * 0.25f); fillStyle = "#E53935"; fill()
    for (k in listOf(-0.45f, 0f, 0.45f)) { beginPath(); arc(cx + k * r, cy - r * 0.68f + abs(k) * r * 0.25f, r * 0.06f, 0f, TAU); fillStyle = "#fff"; fill() }
    val tail = if (tease) sin(t * 10f) * 0.15f else 0f
    beginPath(); arc(cx + r * 0.95f, cy - r * 0.3f, r * 0.13f, 0f, TAU); fillStyle = "#E53935"; fill()
    beginPath(); moveTo(cx + r * 0.95f, cy - r * 0.3f); lineTo(cx + r * (1.25f + tail * 0.3f), cy - r * (0.05f - tail)); strokeStyle = "#E53935"; lineWidth = r * 0.12f; stroke()
    beginPath(); ellipse(cx, cy + r * 0.44f, r * 0.48f, r * 0.36f, 0f, 0f, TAU); fillStyle = "#F4ECE1"; fill()
    val ey = cy - r * 0.08f
    for (sx in SIDES) {
        val ex = cx + sx * r * 0.42f
        if (sur || (tease && sx < 0)) {
            val d = r * 0.13f; beginPath(); moveTo(ex - d * sx, ey - d); lineTo(ex + d * sx, ey); lineTo(ex - d * sx, ey + d); strokeStyle = INK; lineWidth = r * 0.08f; stroke()
        } else {
            beginPath(); arc(ex, ey, r * 0.15f, 0f, TAU); fillStyle = INK; fill()
            beginPath(); arc(ex - r * 0.04f, ey - r * 0.05f, r * 0.06f, 0f, TAU); fillStyle = "#fff"; fill()
            if (tease) { beginPath(); moveTo(ex - r * 0.16f, ey - r * 0.3f); quadraticCurveTo(ex, ey - r * 0.4f, ex + r * 0.17f, ey - r * 0.26f); strokeStyle = INK; lineWidth = r * 0.06f; stroke() }
        }
        beginPath(); ellipse(ex + sx * r * 0.08f, ey + r * 0.3f, r * 0.2f, r * 0.1f, 0f, 0f, TAU); fillStyle = if (tease) "rgba(255,120,150,.55)" else "rgba(255,143,163,.4)"; fill()
    }
    beginPath(); ellipse(cx, cy + r * 0.245f, r * 0.13f, r * 0.085f, 0f, 0f, TAU); fillStyle = INK; fill()
    if (sur) { beginPath(); arc(cx, cy + r * 0.54f, r * 0.12f, 0f, TAU); fillStyle = "#8E2440"; fill() }
    else { beginPath(); moveTo(cx - r * 0.2f, cy + r * 0.45f); quadraticCurveTo(cx, cy + r * 0.6f, cx + r * 0.2f, cy + r * 0.45f); strokeStyle = INK; lineWidth = r * 0.05f; stroke() }
    if (tease) {
        val wag = sin(t * 16f) * 0.3f; val len = r * 0.5f * (0.85f + sin(t * 8f) * 0.15f); val w = r * 0.18f
        save(); translate(cx, cy + r * 0.52f); rotate(wag)
        beginPath(); moveTo(-w, 0f); lineTo(-w, len * 0.6f); quadraticCurveTo(-w, len + w * 0.4f, 0f, len + w * 0.4f); quadraticCurveTo(w, len + w * 0.4f, w, len * 0.6f); lineTo(w, 0f); closePath()
        fillStyle = "#FF6F91"; fill(); lineWidth = r * 0.03f; strokeStyle = "#C2405F"; stroke()
        beginPath(); moveTo(0f, len * 0.15f); lineTo(0f, len * 0.7f); strokeStyle = "#D9506F"; lineWidth = r * 0.03f; stroke()
        restore()
    }
}
