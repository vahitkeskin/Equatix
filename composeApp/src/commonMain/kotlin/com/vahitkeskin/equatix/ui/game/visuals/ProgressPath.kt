package com.vahitkeskin.equatix.ui.game.visuals

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

/**
 * Draws a Bezier curve progress bar with a neon glow effect and a moving percentage label.
 */
@Composable
fun ProgressPath(
    modifier: Modifier = Modifier,
    progress: Float,
    primaryColor: Color = Color(0xFF34C759) // Rengi parametrik yaptık (Varsayılan Yeşil)
) {
    // 1. Text Ölçümleyiciyi Hazırla (Profesyonel Yöntem)
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp) // Yüksekliği artırdık ki yazı kesilmesin
    ) {
        val width = size.width
        val height = size.height
        // Çizgiyi biraz aşağı alıyoruz ki üstte yazıya yer kalsın
        val startY = height * 0.6f

        val pathStart = Offset(0f, startY)
        val pathEnd = Offset(width, startY)

        // Eğri noktaları
        val cp1 = Offset(width * 0.35f, height * 0.2f)
        val cp2 = Offset(width * 0.65f, height * 1.0f)

        // Arka plandaki sönük yol
        val fullPath = Path().apply {
            moveTo(pathStart.x, pathStart.y)
            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, pathEnd.x, pathEnd.y)
        }

        drawPath(
            path = fullPath,
            color = Color.White.copy(alpha = 0.1f),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        val pathMeasure = PathMeasure()
        pathMeasure.setPath(fullPath, false)
        val pathLength = pathMeasure.length

        // İlerlemeyi hesapla
        val drawLength = pathLength * progress.coerceIn(0f, 1f)
        val activePath = Path()
        pathMeasure.getSegment(0f, drawLength, activePath, true)

        if (progress > 0) {
            // Neon Glow (Dış Parlama)
            drawPath(
                path = activePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF38BDF8).copy(alpha = 0f),
                        Color(0xFF38BDF8).copy(alpha = 0.5f)
                    )
                ),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            // Ana Çizgi (Gradient Geçişli)
            drawPath(
                path = activePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF38BDF8), primaryColor)
                ),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // --- İLERLEME TOPU VE METNİ ---
            val currentPos = pathMeasure.getPosition(drawLength)

            if (currentPos != Offset.Unspecified) {
                // 1. Parlayan Top
                drawCircle(
                    color = primaryColor.copy(alpha = 0.5f),
                    radius = 8.dp.toPx(),
                    center = currentPos
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = currentPos
                )

                // 2. Yüzdelik Metni Hazırla
                val percentText = "${(progress * 100).roundToInt()}%"
                val textLayoutResult = textMeasurer.measure(
                    text = percentText,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        background = Color.Transparent
                    )
                )

                // 3. Metin Konumlandırma (Topun üstüne ortala)
                val textSize = textLayoutResult.size
                val bubblePadding = 6.dp.toPx()
                // Topun 25dp yukarısı
                val textCenter = Offset(currentPos.x, currentPos.y - 25.dp.toPx())
                val textTopLeft = Offset(
                    textCenter.x - (textSize.width / 2),
                    textCenter.y - (textSize.height / 2)
                )

                // 4. Metin Arka Planı (Pill Shape / Kapsül)
                // Yazının daha okunaklı olması için arkasına yarı saydam bir kapsül çiziyoruz
                val bgRect = androidx.compose.ui.geometry.Rect(
                    left = textTopLeft.x - bubblePadding,
                    top = textTopLeft.y - bubblePadding / 2,
                    right = textTopLeft.x + textSize.width + bubblePadding,
                    bottom = textTopLeft.y + textSize.height + bubblePadding / 2
                )

                drawRoundRect(
                    color = primaryColor,
                    topLeft = bgRect.topLeft,
                    size = bgRect.size,
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    alpha = 0.8f // Hafif şeffaf
                )

                // 5. Metni Çiz
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = textTopLeft
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewProgressPath() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Column {
            // %25 Örnek
            ProgressPath(progress = 0.25f, modifier = Modifier.padding(20.dp))
            // %78 Örnek
            ProgressPath(progress = 0.78f, modifier = Modifier.padding(20.dp))
        }
    }
}