package com.wolfgames.app.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.wolfgames.app.ui.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfgames.app.i18n.LocalStrings
import com.wolfgames.app.ui.BigButton
import com.wolfgames.app.ui.GameScaffold
import com.wolfgames.app.ui.LocalVibe
import com.wolfgames.app.ui.WG
import com.wolfgames.app.ui.runFor
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

private const val COLS = 7
private const val ROWS = 6
private val BoardBlue = Color(0xFF1F5FD1)
private val BoardDark = Color(0xFF15449A)
private val Hole = Color(0xFF0B0F1E)
private val Red = Color(0xFFE53935)
private val RedDark = Color(0xFF9E1C1C)
private val Yellow = Color(0xFFFDD835)
private val YellowDark = Color(0xFFC79A00)

private class Drop(val col: Int, val row: Int, val player: Int)

/** 연속 4개 찾기. 찾으면 그 칸 4개, 없으면 null */
private fun findWin(b: List<Int>, r: Int, c: Int): List<Int>? {
    val p = b[r * COLS + c]
    if (p == 0) return null
    for ((dr, dc) in listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)) {
        val line = mutableListOf(r * COLS + c)
        for (sgn in listOf(1, -1)) {
            var rr = r + dr * sgn
            var cc = c + dc * sgn
            while (rr in 0 until ROWS && cc in 0 until COLS && b[rr * COLS + cc] == p) {
                line.add(rr * COLS + cc)
                rr += dr * sgn
                cc += dc * sgn
            }
        }
        if (line.size >= 4) return line
    }
    return null
}

/** 떨어지는 위치 (0=시작, 1=도착) — 중력처럼 빨라지다가 바닥에서 살짝 튕김 */
private fun dropCurve(u: Float): Float =
    if (u < 0.72f) {
        val v = u / 0.72f
        v * v
    } else {
        val v = (u - 0.72f) / 0.28f
        1f - 0.14f * sin(PI.toFloat() * v)
    }

/** 9. 4목 — 2인, 가로·세로·대각선으로 4개를 먼저 이으면 승리 */
@Composable
fun FourGame(onBack: () -> Unit) {
    val s = LocalStrings.current
    val vibe = LocalVibe.current
    val board = remember { mutableStateListOf(*Array(ROWS * COLS) { 0 }) } // 0 빈칸, 1 빨강, 2 노랑
    var turn by remember { mutableIntStateOf(1) }
    var starter by remember { mutableIntStateOf(1) }
    var winLine by remember { mutableStateOf<List<Int>?>(null) }
    var draw by remember { mutableStateOf(false) }
    var drop by remember { mutableStateOf<Drop?>(null) }
    var prog by remember { mutableFloatStateOf(0f) }
    var hover by remember { mutableIntStateOf(COLS / 2) }
    var pulse by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val over = winLine != null || draw

    fun colorName(p: Int) = if (p == 1) s.fourRed else s.fourYellow

    fun play(col: Int) {
        if (over || drop != null) return
        hover = col
        var row = -1
        for (r in ROWS - 1 downTo 0) if (board[r * COLS + col] == 0) { row = r; break }
        if (row < 0) return
        val d = Drop(col, row, turn)
        drop = d
        vibe.tap()
        scope.launch {
            prog = 0f
            runFor(320L + row * 55L) { prog = it }
            board[row * COLS + col] = d.player
            drop = null
            vibe.tap()
            val w = findWin(board, row, col)
            when {
                w != null -> {
                    winLine = w
                    vibe.strong()
                    runFor(1600L) { pulse = it }
                }
                board.none { it == 0 } -> {
                    draw = true
                    vibe.strong()
                }
                else -> turn = 3 - turn
            }
        }
    }

    fun reset() {
        for (i in board.indices) board[i] = 0
        winLine = null
        draw = false
        starter = 3 - starter
        turn = starter
    }

    GameScaffold(s.menuFour, onBack) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val wl = winLine
            Text(
                when {
                    wl != null -> s.fourWin(colorName(board[wl[0]]))
                    draw -> s.fourDraw
                    else -> s.fourTurn(colorName(turn))
                },
                color = when {
                    wl != null -> if (board[wl[0]] == 1) Red else Yellow
                    draw -> WG.Accent
                    turn == 1 -> Red
                    else -> Yellow
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            Text(s.fourHint, color = WG.SubText, fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))
            Canvas(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(COLS / (ROWS + 1.15f))
                    .pointerInput(Unit) {
                        detectTapGestures { p ->
                            val cell = min(size.width / COLS.toFloat(), size.height / (ROWS + 1.15f))
                            val left = (size.width - cell * COLS) / 2f
                            val col = ((p.x - left) / cell).toInt()
                            if (col in 0 until COLS) play(col)
                        }
                    },
            ) {
                val cell = min(size.width / COLS, size.height / (ROWS + 1.15f))
                val left = (size.width - cell * COLS) / 2f
                val top = cell * 1.15f
                val r = cell * 0.4f
                fun center(row: Int, col: Int) = Offset(left + (col + 0.5f) * cell, top + (row + 0.5f) * cell)

                // 위쪽: 다음에 떨어뜨릴 알 미리보기
                if (!over && drop == null) {
                    drawDisc(Offset(left + (hover + 0.5f) * cell, cell * 0.55f), r, turn, 1f)
                }
                // 판 뒤쪽 어두운 바탕 (빈 구멍이 검게 보임)
                drawRect(Hole, topLeft = Offset(left, top), size = Size(cell * COLS, cell * ROWS))
                // 판 뒤에 있는 알들 (구멍 사이로 보임)
                for (row in 0 until ROWS) for (col in 0 until COLS) {
                    val p = board[row * COLS + col]
                    if (p != 0) drawDisc(center(row, col), r, p, 1f)
                }
                drop?.let { d ->
                    val startY = cell * 0.55f
                    val endY = center(d.row, d.col).y
                    val y = startY + (endY - startY) * dropCurve(prog)
                    drawDisc(Offset(left + (d.col + 0.5f) * cell, y), r, d.player, 1f)
                }
                // 파란 판 (구멍 뚫린 모양)
                val boardRect = RoundRect(left - cell * 0.12f, top - cell * 0.1f, left + cell * COLS + cell * 0.12f, top + cell * ROWS + cell * 0.12f, CornerRadius(cell * 0.3f))
                val path = Path().apply {
                    fillType = PathFillType.EvenOdd
                    addRoundRect(boardRect)
                    for (row in 0 until ROWS) for (col in 0 until COLS) {
                        val c = center(row, col)
                        addOval(androidx.compose.ui.geometry.Rect(c.x - r, c.y - r, c.x + r, c.y + r))
                    }
                }
                drawPath(path, Brush.verticalGradient(listOf(BoardBlue, BoardDark), startY = top, endY = top + cell * ROWS))
                // 구멍 테두리
                for (row in 0 until ROWS) for (col in 0 until COLS) {
                    drawCircle(Color(0x66FFFFFF), radius = r, center = center(row, col), style = Stroke(width = cell * 0.045f))
                }
                // 승리한 4칸 강조
                winLine?.let { line ->
                    val glow = 0.5f + 0.5f * sin(pulse * PI.toFloat() * 6f)
                    for (i in line) {
                        val c = center(i / COLS, i % COLS)
                        drawCircle(Color.White.copy(alpha = 0.5f + 0.5f * glow), radius = r * 1.08f, center = c, style = Stroke(width = cell * 0.09f))
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            if (over) {
                BigButton(s.playAgain, Modifier.fillMaxWidth()) { reset() }
            } else {
                BigButton(s.fourRestart, Modifier.fillMaxWidth(), color = WG.Surface2, contentColor = WG.Text, enabled = drop == null) { reset() }
            }
        }
    }
}

/** 입체감 있는 알 */
private fun DrawScope.drawDisc(c: Offset, r: Float, player: Int, alpha: Float) {
    val main = if (player == 1) Red else Yellow
    val dark = if (player == 1) RedDark else YellowDark
    drawCircle(dark, radius = r, center = c, alpha = alpha)
    drawCircle(main, radius = r * 0.86f, center = Offset(c.x - r * 0.04f, c.y - r * 0.04f), alpha = alpha)
    drawCircle(dark.copy(alpha = 0.35f), radius = r * 0.66f, center = c, style = Stroke(width = r * 0.06f), alpha = alpha)
    drawWolfMark(Offset(c.x, c.y + r * 0.02f), r, dark, main)
    drawCircle(Color.White.copy(alpha = 0.45f), radius = r * 0.11f, center = Offset(c.x - r * 0.52f, c.y - r * 0.52f), alpha = alpha)
}

/** 알 가운데 늑대 얼굴 무늬 (웹 버전 wolfMark 와 같은 모양) */
private fun DrawScope.drawWolfMark(c: Offset, r: Float, ink: Color, base: Color) {
    fun p(x: Float, y: Float) = Offset(c.x + x * r, c.y + y * r)
    val mark = ink.copy(alpha = 0.8f)
    for (s in listOf(-1f, 1f)) {
        val ear = Path().apply {
            p(s * 0.44f, -0.16f).let { moveTo(it.x, it.y) }
            p(s * 0.33f, -0.6f).let { lineTo(it.x, it.y) }
            p(s * 0.08f, -0.3f).let { lineTo(it.x, it.y) }
            close()
        }
        drawPath(ear, mark)
    }
    val face = Path().apply {
        val a = p(-0.46f, -0.2f); moveTo(a.x, a.y)
        val c1 = p(-0.5f, 0.12f); val e1 = p(-0.18f, 0.3f); quadraticTo(c1.x, c1.y, e1.x, e1.y)
        val chin = p(0f, 0.5f); lineTo(chin.x, chin.y)
        val e2 = p(0.18f, 0.3f); lineTo(e2.x, e2.y)
        val c2 = p(0.5f, 0.12f); val e3 = p(0.46f, -0.2f); quadraticTo(c2.x, c2.y, e3.x, e3.y)
        val c3 = p(0f, -0.42f); quadraticTo(c3.x, c3.y, a.x, a.y)
        close()
    }
    drawPath(face, mark)
    for (s in listOf(-1f, 1f)) {
        val e = p(s * 0.19f, -0.04f)
        drawOval(base, topLeft = Offset(e.x - r * 0.075f, e.y - r * 0.055f), size = Size(r * 0.15f, r * 0.11f))
    }
    val n = p(0f, 0.33f)
    drawOval(base, topLeft = Offset(n.x - r * 0.07f, n.y - r * 0.05f), size = Size(r * 0.14f, r * 0.1f))
}

