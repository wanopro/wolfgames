package com.wolfgames.app.ui

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import kotlin.math.PI
import kotlin.math.abs

/**
 * 웹 버전(HTML canvas)과 같은 방식으로 그릴 수 있게 만든 작은 그리기 도구.
 * 웹의 그림 코드를 거의 그대로 옮겨 쓰려고 만듦 (beginPath / moveTo / arc / fill / stroke …).
 * 좌표 단위는 dp (생성할 때 density 배율을 걸어 둠).
 */
class Ctx(val c: Canvas, density: Float) {
    private val fillP = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val strokeP = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND }
    private val textP = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    private var path = Path()
    private var fillShader: Shader? = null
    private val alphaStack = ArrayList<Float>()

    var fillStyle: String = "#000"
        set(v) { field = v; fillShader = null }
    var strokeStyle: String = "#000"
    var lineWidth = 1f
    var globalAlpha = 1f
    var fontSize = 16f
    var fontBold = true
    var lineCap = "round"

    init { c.save(); c.scale(density, density) }
    fun finish() = c.restore()

    // ---------- 상태 ----------
    fun save() { c.save(); alphaStack.add(globalAlpha) }
    fun restore() { c.restore(); if (alphaStack.isNotEmpty()) globalAlpha = alphaStack.removeAt(alphaStack.size - 1) }
    fun translate(x: Float, y: Float) = c.translate(x, y)
    fun rotate(rad: Float) = c.rotate(rad * 180f / PI.toFloat())
    fun scale(x: Float, y: Float) = c.scale(x, y)
    fun clip() { c.clipPath(path) }

    // ---------- 경로 ----------
    fun beginPath() { path = Path() }
    fun moveTo(x: Float, y: Float) = path.moveTo(x, y)
    fun lineTo(x: Float, y: Float) = path.lineTo(x, y)
    fun quadraticCurveTo(cx: Float, cy: Float, x: Float, y: Float) = path.quadTo(cx, cy, x, y)
    fun bezierCurveTo(c1x: Float, c1y: Float, c2x: Float, c2y: Float, x: Float, y: Float) = path.cubicTo(c1x, c1y, c2x, c2y, x, y)
    fun closePath() = path.close()
    fun roundRect(x: Float, y: Float, w: Float, h: Float, r: Float) = path.addRoundRect(RectF(x, y, x + w, y + h), r, r, Path.Direction.CW)
    fun rect(x: Float, y: Float, w: Float, h: Float) = path.addRect(RectF(x, y, x + w, y + h), Path.Direction.CW)

    /** canvas arc: a0→a1 (라디안). 한 바퀴 이상이면 원 */
    fun arc(x: Float, y: Float, r: Float, a0: Float, a1: Float, ccw: Boolean = false) = ellipse(x, y, r, r, 0f, a0, a1, ccw)

    fun ellipse(x: Float, y: Float, rx: Float, ry: Float, rot: Float, a0: Float, a1: Float, ccw: Boolean = false) {
        val full = abs(a1 - a0) >= 2 * PI - 1e-3
        if (!full && rot == 0f) {
            var sweep = (a1 - a0) * 180f / PI.toFloat()
            if (ccw && sweep > 0) sweep -= 360f
            if (!ccw && sweep < 0) sweep += 360f
            // 웹처럼 현재 점에서 호의 시작점까지 직선으로 이어 그림
            path.arcTo(RectF(x - rx, y - ry, x + rx, y + ry), a0 * 180f / PI.toFloat(), sweep, false)
            return
        }
        val p = Path()
        val rect = RectF(-rx, -ry, rx, ry)
        if (full) p.addOval(rect, Path.Direction.CW)
        else {
            var sweep = (a1 - a0) * 180f / PI.toFloat()
            if (ccw && sweep > 0) sweep -= 360f
            if (!ccw && sweep < 0) sweep += 360f
            p.arcTo(rect, a0 * 180f / PI.toFloat(), sweep, true)
        }
        val m = Matrix(); m.postRotate(rot * 180f / PI.toFloat()); m.postTranslate(x, y); p.transform(m)
        path.addPath(p)
    }

    // ---------- 칠하기 ----------
    fun fill() {
        if (fillShader != null) { fillP.shader = fillShader; fillP.color = 0xFFFFFFFF.toInt() } else { fillP.shader = null; fillP.color = parseColor(fillStyle) }
        fillP.alpha = (Color8.a(fillP.color) * globalAlpha).toInt().coerceIn(0, 255)
        if (fillShader != null) fillP.alpha = (255 * globalAlpha).toInt()
        c.drawPath(path, fillP)
    }
    fun stroke() {
        strokeP.color = parseColor(strokeStyle)
        strokeP.alpha = (Color8.a(strokeP.color) * globalAlpha).toInt().coerceIn(0, 255)
        strokeP.strokeWidth = lineWidth
        strokeP.strokeCap = if (lineCap == "butt") Paint.Cap.BUTT else Paint.Cap.ROUND
        c.drawPath(path, strokeP)
    }
    fun fillRect(x: Float, y: Float, w: Float, h: Float) { beginPath(); rect(x, y, w, h); fill() }

    /** 원형 그라데이션을 다음 fill 에 사용 (stops: 위치 to 색) */
    fun radialFill(x: Float, y: Float, r0: Float, r1: Float, stops: List<Pair<Float, String>>) {
        val cols = stops.map { parseColor(it.second) }.toIntArray()
        val pos = stops.map { (r0 + (r1 - r0) * it.first) / r1 }.toFloatArray()
        fillShader = RadialGradient(x, y, r1, cols, pos, Shader.TileMode.CLAMP)
    }
    fun linearFill(x0: Float, y0: Float, x1: Float, y1: Float, a: String, b: String) {
        fillShader = LinearGradient(x0, y0, x1, y1, parseColor(a), parseColor(b), Shader.TileMode.CLAMP)
    }

    // ---------- 글자 (가운데 정렬, 세로 가운데) ----------
    fun font(size: Float, bold: Boolean = true) { fontSize = size; fontBold = bold }
    fun fillText(s: String, x: Float, y: Float) {
        textP.textSize = fontSize
        textP.typeface = if (fontBold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT
        textP.color = parseColor(fillStyle)
        textP.alpha = (Color8.a(textP.color) * globalAlpha).toInt().coerceIn(0, 255)
        val fm = textP.fontMetrics
        c.drawText(s, x, y - (fm.ascent + fm.descent) / 2f, textP)
    }
    fun measureText(s: String): Float { textP.textSize = fontSize; textP.typeface = if (fontBold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT; return textP.measureText(s) }

    companion object {
        private val cache = HashMap<String, Int>()

        /** "#RGB", "#RRGGBB", "#RRGGBBAA"(웹 방식), "rgba(r,g,b,a)" */
        fun parseColor(s: String): Int = cache.getOrPut(s) {
            val t = s.trim()
            when {
                t.startsWith("rgba") || t.startsWith("rgb") -> {
                    val v = t.substringAfter('(').substringBefore(')').split(',').map { it.trim() }
                    val a = if (v.size > 3) v[3].toFloat() else 1f
                    Color8.argb((a * 255).toInt(), v[0].toFloat().toInt(), v[1].toFloat().toInt(), v[2].toFloat().toInt())
                }
                t.length == 4 -> { val r = t[1].digitToInt(16) * 17; val g = t[2].digitToInt(16) * 17; val b = t[3].digitToInt(16) * 17; Color8.argb(255, r, g, b) }
                t.length == 7 -> (0xFF000000 or t.substring(1).toLong(16)).toInt()
                t.length == 9 -> { val rgb = t.substring(1, 7).toLong(16); val a = t.substring(7, 9).toInt(16); ((a.toLong() shl 24) or rgb).toInt() }
                else -> 0xFF000000.toInt()
            }
        }
    }
}

object Color8 {
    fun a(c: Int) = (c ushr 24) and 0xFF
    fun argb(a: Int, r: Int, g: Int, b: Int) = (a.coerceIn(0, 255) shl 24) or (r.coerceIn(0, 255) shl 16) or (g.coerceIn(0, 255) shl 8) or b.coerceIn(0, 255)
}

/** 웹의 7(=한 바퀴 이상) 대신 쓰는 상수 */
const val TAU = 7f
