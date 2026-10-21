package com.wolfgames.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 트럼프 카드 한 장. r: 1(A)~13(K), s: 0♠ 1♥ 2♦ 3♣ */
data class PCard(val r: Int, val s: Int) {
    val red get() = s == 1 || s == 2
    val rank get() = RANKS[r]
    val suit get() = SUITS[s]

    companion object {
        val RANKS = listOf("", "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val SUITS = listOf("♠", "♥", "♦", "♣")

        /** 섞인 52장 덱 */
        fun deck(): MutableList<PCard> = (0 until 52).map { PCard(it % 13 + 1, it / 13) }.shuffled().toMutableList()
    }
}

private val CardRed = Color(0xFFE53935)
private val CardBlack = Color(0xFF1A1A1A)

/**
 * 카드 그림. card가 null이거나 faceUp=false면 뒷면.
 * faceUp이 바뀌거나 flipKey가 바뀌면(새 카드로 교체) 옆으로 뒤집히는 애니메이션.
 */
@Composable
fun PlayingCard(
    card: PCard?,
    width: Dp,
    modifier: Modifier = Modifier,
    faceUp: Boolean = card != null,
    highlight: Color? = null,
    flipKey: Int? = null,
) {
    val up = faceUp && card != null
    val anim = remember { Animatable(if (up) 0f else 180f) }
    var first by remember { mutableStateOf(true) }
    LaunchedEffect(up, flipKey) {
        val target = if (up) 0f else 180f
        if (first) { first = false; anim.snapTo(target); return@LaunchedEffect }
        if (flipKey != null && up && anim.value == 0f) anim.snapTo(180f)
        anim.animateTo(target, tween(450))
    }
    val rot = anim.value
    val h = width * 1.4f
    val shape = RoundedCornerShape(width * 0.1f)
    val scale = width.value / 90f
    Box(
        modifier
            .size(width, h)
            .graphicsLayer { rotationY = rot; cameraDistance = 14f * density },
    ) {
        val base = Modifier
            .fillMaxSize()
            .shadow(6.dp, shape)
            .clip(shape)
        if (rot <= 90f && card != null) {
            val c = if (card.red) CardRed else CardBlack
            Box(
                base.background(Color.White).let { if (highlight != null) it.border(3.dp, highlight, shape) else it },
            ) {
                Corner(card, c, scale, Modifier.align(Alignment.TopStart).padding(start = width * 0.07f, top = width * 0.05f))
                Corner(card, c, scale, Modifier.align(Alignment.BottomEnd).padding(end = width * 0.07f, bottom = width * 0.05f).rotate(180f))
                Text(card.suit, color = c, fontSize = (45 * scale).sp, modifier = Modifier.align(Alignment.Center))
            }
        } else {
            Box(
                base
                    .graphicsLayer { rotationY = 180f }
                    .background(Brush.linearGradient(listOf(Color(0xFF2B3A8C), Color(0xFF1C2766))))
                    .border(4.dp, Color.White, shape),
                contentAlignment = Alignment.Center,
            ) {
                Text("🐺", fontSize = (32 * scale).sp)
            }
        }
    }
}

@Composable
private fun Corner(card: PCard, color: Color, scale: Float, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(card.rank, color = color, fontSize = (18 * scale).sp, fontWeight = FontWeight.ExtraBold, lineHeight = (18 * scale).sp)
        Text(card.suit, color = color, fontSize = (16 * scale).sp, lineHeight = (16 * scale).sp)
    }
}
