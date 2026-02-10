package com.vahitkeskin.equatix.ui.game.visuals

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import kotlin.math.roundToInt

@Composable
fun ProgressPath(
    modifier: Modifier = Modifier,
    progress: Float,
    colors: EquatixDesignSystem.ThemeColors // <--- TEMA EKLENDİ
) {
    val textMeasurer = rememberTextMeasurer()

    // Renkleri Temadan Alıyoruz
    val trackColor = colors.gridLines // Yol rengi (Light: Gri, Dark: Silik Mavi)
    val activeColor = colors.success // Doluluk rengi
    val glowColor = colors.accent    // Parlama rengi

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        val width = size.width
        val height = size.height
        val startY = height * 0.6f

        val pathStart = Offset(0f, startY)
        val pathEnd = Offset(width, startY)

        val cp1 = Offset(width * 0.35f, height * 0.2f)
        val cp2 = Offset(width * 0.65f, height * 1.0f)

        val fullPath = Path().apply {
            moveTo(pathStart.x, pathStart.y)
            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, pathEnd.x, pathEnd.y)
        }

        // 1. ARKA PLAN YOLU (Dinamik Renk)
        drawPath(
            path = fullPath,
            color = trackColor,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        val pathMeasure = PathMeasure()
        pathMeasure.setPath(fullPath, false)
        val pathLength = pathMeasure.length
        val drawLength = pathLength * progress.coerceIn(0f, 1f)
        val activePath = Path()
        pathMeasure.getSegment(0f, drawLength, activePath, true)

        if (progress > 0) {
            // 2. NEON PARLAMA (Accent Rengi)
            drawPath(
                path = activePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0f),
                        glowColor.copy(alpha = 0.5f)
                    )
                ),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. AKTİF ÇİZGİ (Accent -> Success Geçişi)
            drawPath(
                path = activePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(glowColor, activeColor)
                ),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            val currentPos = pathMeasure.getPosition(drawLength)

            if (currentPos != Offset.Unspecified) {
                drawCircle(color = activeColor.copy(alpha = 0.5f), radius = 8.dp.toPx(), center = currentPos)
                drawCircle(color = Color.White, radius = 4.dp.toPx(), center = currentPos)

                val percentText = "${(progress * 100).roundToInt()}%"
                val textLayoutResult = textMeasurer.measure(
                    text = percentText,
                    style = TextStyle(
                        color = Color.White, // Baloncuk içi yazı her zaman beyaz
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                val textSize = textLayoutResult.size
                val bubblePadding = 6.dp.toPx()
                val textCenter = Offset(currentPos.x, currentPos.y - 25.dp.toPx())
                val textTopLeft = Offset(
                    textCenter.x - (textSize.width / 2),
                    textCenter.y - (textSize.height / 2)
                )

                val bgRect = androidx.compose.ui.geometry.Rect(
                    left = textTopLeft.x - bubblePadding,
                    top = textTopLeft.y - bubblePadding / 2,
                    right = textTopLeft.x + textSize.width + bubblePadding,
                    bottom = textTopLeft.y + textSize.height + bubblePadding / 2
                )

                drawRoundRect(
                    color = activeColor,
                    topLeft = bgRect.topLeft,
                    size = bgRect.size,
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    alpha = 0.9f
                )

                drawText(textLayoutResult = textLayoutResult, topLeft = textTopLeft)
            }
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun PreviewProgressPath() {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        com.vahitkeskin.equatix.ui.utils.PreviewContainer(isDark = true) { colors, _ ->
            ProgressPath(progress = 0.35f, colors = colors)
        }
        
        com.vahitkeskin.equatix.ui.utils.PreviewContainer(isDark = false) { colors, _ ->
            ProgressPath(progress = 0.75f, colors = colors)
        }
    }
}