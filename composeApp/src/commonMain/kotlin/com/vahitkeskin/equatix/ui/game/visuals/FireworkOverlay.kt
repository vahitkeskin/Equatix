package com.vahitkeskin.equatix.ui.game.visuals

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val GRAVITY = 0.45f
private const val DRAG = 0.97f
private const val SPAWN_CHANCE = 0.05f
private const val PARTICLE_COUNT = 80

@Composable
fun FireworkOverlay() {
    val particles = remember { mutableListOf<FireworkParticle>() }

    val colors = remember {
        listOf(
            Color(0xFFFF453A), // Error Red (Tema)
            Color(0xFFFBBF24), // Gold (Tema)
            Color(0xFF38BDF8), // Accent Sky (Tema)
            Color(0xFF34D399), // Success Green (Tema)
            Color(0xFFAF52DE), // Purple (OpDiv rengi)
            Color(0xFFFFFFFF)  // Sparkle White
        )
    }

    LaunchedEffect(Unit) {
        var lastFrameTime = withFrameNanos { it }
        while (true) {
            withFrameNanos { currentFrameTime ->
                val delta = (currentFrameTime - lastFrameTime) / 1_000_000_000f
                lastFrameTime = currentFrameTime

                if (Random.nextFloat() < SPAWN_CHANCE) {
                    val centerX = Random.nextFloat()
                    val centerY = Random.nextFloat() * 0.4f
                    val color = colors.random()

                    repeat(PARTICLE_COUNT) {
                        val angle = Random.nextDouble() * 2 * PI
                        val speed = Random.nextFloat() * 0.3f + 0.1f

                        particles.add(
                            FireworkParticle(
                                x = centerX,
                                y = centerY,
                                vx = (cos(angle) * speed).toFloat(),
                                vy = (sin(angle) * speed).toFloat(),
                                color = color,
                                alpha = 1f,
                                size = Random.nextFloat() * 4f + 2f
                            )
                        )
                    }
                }

                val iterator = particles.listIterator()
                while (iterator.hasNext()) {
                    val p = iterator.next()
                    p.x += p.vx * delta
                    p.y += p.vy * delta
                    p.vy += GRAVITY * delta
                    p.vx *= DRAG
                    p.vy *= DRAG
                    p.alpha -= 0.5f * delta
                    if (p.alpha <= 0f) iterator.remove()
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        particles.forEach { p ->
            val screenX = p.x * width
            val screenY = p.y * height
            if (p.alpha > 0f) {
                drawCircle(
                    color = p.color.copy(alpha = p.alpha),
                    radius = p.size,
                    center = Offset(screenX, screenY)
                )
            }
        }
    }
}

data class FireworkParticle(
    var x: Float, var y: Float,
    var vx: Float, var vy: Float,
    val color: Color,
    var alpha: Float,
    val size: Float
)