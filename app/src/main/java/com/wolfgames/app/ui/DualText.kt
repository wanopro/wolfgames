package com.wolfgames.app.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.wolfgames.app.i18n.SEP

/**
 * material3 Text 와 똑같이 쓰는 글자. 두 언어 문구(메인 SEP 보조)면
 * 메인 아래 줄에 보조 언어를 같은 크기로 살짝 흐리게 그림 (웹 .dual 과 같음).
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    /** true: 두 언어를 한 줄에 (보조 언어는 오른쪽). 큰 제목용 */
    inline: Boolean = false,
) {
    if (SEP !in text) {
        androidx.compose.material3.Text(text, modifier = modifier, color = color, fontSize = fontSize, fontStyle = fontStyle, fontWeight = fontWeight, fontFamily = fontFamily, letterSpacing = letterSpacing, textDecoration = textDecoration, textAlign = textAlign, lineHeight = lineHeight, overflow = overflow, softWrap = softWrap, maxLines = maxLines, minLines = minLines, style = style)
        return
    }
    val parts = text.split(SEP)
    // 보조 줄은 같은 크기, 투명도만 낮춤
    val base = if (color != Color.Unspecified) color else style.color.takeIf { it != Color.Unspecified } ?: androidx.compose.material3.LocalContentColor.current
    val two = buildAnnotatedString {
        append(parts[0]); append(if (inline) "  " else "\n")
        val st = length; append(parts[1])
        addStyle(SpanStyle(color = base.copy(alpha = base.alpha * 0.8f)), st, length)
    }
    androidx.compose.material3.Text(
        two, modifier = modifier, color = color, fontSize = fontSize, fontStyle = fontStyle, fontWeight = fontWeight, fontFamily = fontFamily,
        letterSpacing = letterSpacing, textDecoration = textDecoration, textAlign = textAlign, lineHeight = lineHeight, overflow = overflow, softWrap = softWrap,
        maxLines = if (maxLines == Int.MAX_VALUE || inline) maxLines else maxLines * 2, minLines = minLines, style = style,
    )
}

/** AnnotatedString 은 그대로 */
@Composable
fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
) = androidx.compose.material3.Text(text, modifier = modifier, color = color, fontSize = fontSize, fontWeight = fontWeight, textAlign = textAlign, lineHeight = lineHeight, maxLines = maxLines, style = style)
