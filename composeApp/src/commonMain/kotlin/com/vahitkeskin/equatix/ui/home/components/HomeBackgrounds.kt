package com.vahitkeskin.equatix.ui.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.vahitkeskin.equatix.ui.game.visuals.CosmicBackground

@Composable
fun HomeBackgroundLayer(isDark: Boolean, gridColor: Color, bgColor: Color) {

    // YENİ MANTIK: Rengi GraphicsLayer ile değil, parametre ile belirliyoruz.
    // Dark Mod -> Beyaz Yıldızlar
    // Light Mod -> Koyu Lacivert (Slate900) Yıldızlar
    val starColor = if (isDark) Color.White else Color(0xFF0F172A)

    // Light modda yıldızlar çok göze batmasın diye biraz daha şeffaf yapıyoruz
    val starAlpha = if (isDark) 1f else 0.6f

    // 1. Katman: Yıldızlar
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = starAlpha } // Sadece opaklığı buradan yönetiyoruz
    ) {
        CosmicBackground(
            starColor = starColor // Rengi parametre olarak geçiyoruz (Performanslı Yöntem)
        )
    }

    // 2. Katman: Dijital Izgara (Grid)
    DigitalGridBackground(
        gridColor = gridColor,
        bgColor = bgColor,
        isDark = isDark
    )
}

@Composable
private fun DigitalGridBackground(gridColor: Color, bgColor: Color, isDark: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "grid")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "offset"
    )

    Canvas(
        modifier = Modifier.fillMaxSize().graphicsLayer { alpha = if (isDark) 0.15f else 0.25f }) {
        val gridSize = 50.dp.toPx()
        val width = size.width
        val height = size.height

        // Dikey Çizgiler
        for (x in 0..width.toInt() step gridSize.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), height),
                strokeWidth = 1f
            )
        }

        // Yatay Çizgiler (Hareketli - Akan Efekt)
        for (y in -100..height.toInt() step gridSize.toInt()) {
            val yPos = y + offsetY
            if (yPos < height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, yPos),
                    end = Offset(width, yPos),
                    strokeWidth = 1f
                )
            }
        }

        // Vignette (Alt Kısım Karartma/Yumuşatma)
        // Grid'in alt kısmının arka plan rengine karışmasını sağlar
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, bgColor),
                startY = height * 0.5f,
                endY = height
            )
        )
    }
}