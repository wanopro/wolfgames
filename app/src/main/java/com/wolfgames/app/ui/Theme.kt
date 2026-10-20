package com.wolfgames.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** 앱 전체 색상 */
object WG {
    val Bg = Color(0xFF12121A)
    val Surface = Color(0xFF1E1E2A)
    val Surface2 = Color(0xFF2C2C3E)
    val Accent = Color(0xFFFFB300)
    val Text = Color(0xFFF2F2F7)
    val SubText = Color(0xFF9A9AB0)
    val Win = Color(0xFFFF3D5A)

    /** 손가락/플레이어별 색 */
    val PlayerColors = listOf(
        Color(0xFFFF5252),
        Color(0xFF448AFF),
        Color(0xFF69F0AE),
        Color(0xFFFFD740),
        Color(0xFFE040FB),
        Color(0xFF18FFFF),
        Color(0xFFFF6E40),
        Color(0xFFB2FF59),
        Color(0xFFFF80AB),
        Color(0xFF8C9EFF),
    )
}

@Composable
fun WolfTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = WG.Accent,
            onPrimary = Color.Black,
            background = WG.Bg,
            onBackground = WG.Text,
            surface = WG.Surface,
            onSurface = WG.Text,
        ),
        content = content,
    )
}
