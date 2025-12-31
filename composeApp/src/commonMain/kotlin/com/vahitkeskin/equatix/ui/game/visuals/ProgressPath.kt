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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Draws a Bezier curve progress bar with a neon glow effect.
 */
@Composable
fun ProgressPath(
    modifier: Modifier = Modifier, // Dışarıdan modifier alabilsin
    progress: Float
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        val width = size.width
        val height = size.height

        val startY = height * 0.5f
        val pathStart = Offset(0f, startY)
        val pathEnd = Offset(width, startY)

        val cp1 = Offset(width * 0.35f, height * 0.1f) // Yukarı kıvrım
        val cp2 = Offset(width * 0.65f, height * 0.9f) // Aşağı kıvrım

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

        val activePath = Path()
        val drawLength = pathLength * progress.coerceIn(0f, 1f)
        pathMeasure.getSegment(0f, drawLength, activePath, true)

        if (progress > 0) {
            // Neon Glow (Dış Parlama)
            drawPath(
                path = activePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF38BDF8).copy(alpha = 0f),
                        Color(0xFF38BDF8).copy(alpha = 0.6f)
                    )
                ),
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )

            // Ana Çizgi (Gradient Geçişli)
            drawPath(
                path = activePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFF34C759))
                ),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // Işık Topu (En uçtaki daire)
            val currentPos = pathMeasure.getPosition(drawLength)
            if (currentPos != Offset.Unspecified) {
                drawCircle(
                    color = Color(0xFF34C759).copy(alpha = 0.5f),
                    radius = 8.dp.toPx(),
                    center = currentPos
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = currentPos
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
        ProgressPath(progress = 0.50f)
    }
}