package com.vahitkeskin.equatix.ui.game.visuals

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Draws a Bezier curve progress bar with a neon glow effect.
 */
@Composable
fun ProgressPath(progress: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val pathStart = Offset(0f, height * 0.85f)
        val pathEnd = Offset(width, height * 0.85f)
        val cp1 = Offset(width * 0.25f, height * 0.75f)
        val cp2 = Offset(width * 0.75f, height * 0.95f)

        val fullPath = Path().apply {
            moveTo(pathStart.x, pathStart.y)
            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, pathEnd.x, pathEnd.y)
        }

        drawPath(
            path = fullPath,
            color = Color.White.copy(alpha = 0.05f),
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )

        val pathMeasure = PathMeasure()
        pathMeasure.setPath(fullPath, false)
        val pathLength = pathMeasure.length

        val activePath = Path()
        val drawLength = pathLength * progress.coerceIn(0f, 1f)
        pathMeasure.getSegment(0f, drawLength, activePath, true)

        if (progress > 0) {
            drawPath(
                path = activePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF38BDF8).copy(alpha = 0f), Color(0xFF38BDF8).copy(alpha = 0.5f))
                ),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            drawPath(
                path = activePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFF34C759))
                ),
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            val currentPos = pathMeasure.getPosition(drawLength)
            if (currentPos != Offset.Unspecified) {
                drawCircle(
                    color = Color(0xFF34C759).copy(alpha = 0.4f),
                    radius = 12.dp.toPx(),
                    center = currentPos
                )
                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = currentPos
                )
            }
        }
    }
}