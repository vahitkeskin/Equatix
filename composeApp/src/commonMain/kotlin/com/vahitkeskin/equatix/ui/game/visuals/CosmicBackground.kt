package com.vahitkeskin.equatix.ui.game.visuals

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Renders a dynamic space background with moving particles.
 */
@Composable
fun CosmicBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "cosmic_anim")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val particles = remember {
        List(30) {
            CosmicParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 2f + 1f,
                speedX = (Random.nextFloat() - 0.5f) * 0.05f,
                speedY = (Random.nextFloat() - 0.5f) * 0.05f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF000000))
            )
        )

        particles.forEachIndexed { index, p ->
            val currX = (p.x + p.speedX * (time * 10f)) % 1f
            val currY = (p.y + p.speedY * (time * 10f)) % 1f

            val finalX = (if (currX < 0) currX + 1f else currX) * width
            val finalY = (if (currY < 0) currY + 1f else currY) * height

            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = p.radius,
                center = Offset(finalX, finalY)
            )

            for (j in index + 1 until particles.size) {
                val p2 = particles[j]
                val p2XRaw = (p2.x + p2.speedX * (time * 10f)) % 1f
                val p2YRaw = (p2.y + p2.speedY * (time * 10f)) % 1f

                val finalP2X = (if (p2XRaw < 0) p2XRaw + 1f else p2XRaw) * width
                val finalP2Y = (if (p2YRaw < 0) p2YRaw + 1f else p2YRaw) * height

                val dist = sqrt((finalX - finalP2X).pow(2) + (finalY - finalP2Y).pow(2))
                val maxDist = width * 0.25f

                if (dist < maxDist) {
                    val alpha = (1f - (dist / maxDist)) * 0.15f
                    drawLine(
                        color = Color(0xFF38BDF8).copy(alpha = alpha),
                        start = Offset(finalX, finalY),
                        end = Offset(finalP2X, finalP2Y),
                        strokeWidth = 1f
                    )
                }
            }
        }
    }
}

private data class CosmicParticle(
    val x: Float, val y: Float,
    val radius: Float,
    val speedX: Float, val speedY: Float
)