package com.wolfgames.app.ui

import androidx.compose.runtime.withFrameNanos

/**
 * 정해진 시간(ms) 동안 매 프레임 진행률(0→1, 감속 곡선 적용)을 알려줌.
 * 휴대폰의 '애니메이션 끄기/절전' 설정과 상관없이 실제 시간대로 돌아감.
 * 감속: ease-out cubic — 처음엔 빠르게, 끝으로 갈수록 천천히 멈춤.
 */
suspend fun spinFor(durationMs: Long, onProgress: (Float) -> Unit) {
    val start = withFrameNanos { it }
    while (true) {
        val now = withFrameNanos { it }
        val u = ((now - start) / 1_000_000f / durationMs).coerceIn(0f, 1f)
        val inv = 1f - u
        onProgress(1f - inv * inv * inv)
        if (u >= 1f) break
    }
}

/** 정해진 시간(ms) 동안 일정한 속도로 진행률(0→1)을 알려줌 */
suspend fun runFor(durationMs: Long, onProgress: (Float) -> Unit) {
    val start = withFrameNanos { it }
    while (true) {
        val now = withFrameNanos { it }
        val u = ((now - start) / 1_000_000f / durationMs).coerceIn(0f, 1f)
        onProgress(u)
        if (u >= 1f) break
    }
}
