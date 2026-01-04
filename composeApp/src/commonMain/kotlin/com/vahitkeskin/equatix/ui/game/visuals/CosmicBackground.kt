package com.vahitkeskin.equatix.ui.game.visuals

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun CosmicBackground(
    modifier: Modifier = Modifier,
    starCount: Int = 100,
    starColor: Color // <--- ZORUNLU YAPILDI, TEMA KONTROLÜ İÇİN
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cosmic_stars")

    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_alpha"
    )

    val stars = remember {
        List(starCount) {
            StarData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3f + 1f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        stars.forEach { star ->
            drawCircle(
                color = starColor.copy(alpha = starAlpha * (star.size / 4f)),
                radius = star.size,
                center = Offset(star.x * width, star.y * height)
            )
        }
    }
}

private data class StarData(val x: Float, val y: Float, val size: Float)