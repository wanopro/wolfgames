package com.wolfgames.app.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 진동 도우미 — 휴대폰 진동 모터를 직접 사용
 * (시스템 '터치 진동' 설정이 꺼져 있어도 게임 진동은 동작)
 */
class Vibe(private val vibrator: Vibrator?) {
    /** 버튼·터치 등 가벼운 진동 */
    fun tap() = buzz(30, 140)

    /** 당첨·승리·결과 등 강한 진동 */
    fun strong() = buzz(220, 255)

    /** 짧게 두 번 (카운트다운 등) */
    fun tick() = buzz(60, 200)

    private fun buzz(ms: Long, amp: Int) {
        val v = vibrator ?: return
        try {
            if (!v.hasVibrator()) return
            val a = if (v.hasAmplitudeControl()) amp else VibrationEffect.DEFAULT_AMPLITUDE
            v.vibrate(VibrationEffect.createOneShot(ms, a))
        } catch (_: Exception) {
        }
    }

    companion object {
        fun from(context: Context): Vibe {
            val vib: Vibrator? = try {
                if (Build.VERSION.SDK_INT >= 31) {
                    (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }
            } catch (_: Exception) {
                null
            }
            return Vibe(vib)
        }
    }
}

val LocalVibe = staticCompositionLocalOf { Vibe(null) }

/** 누르면 진동이 오는 TextButton */
@Composable
fun VTextButton(onClick: () -> Unit, enabled: Boolean = true, content: @Composable RowScope.() -> Unit) {
    val vibe = LocalVibe.current
    TextButton(onClick = { vibe.tap(); onClick() }, enabled = enabled, content = content)
}
