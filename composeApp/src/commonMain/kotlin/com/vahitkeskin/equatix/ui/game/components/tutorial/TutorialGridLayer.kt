package com.vahitkeskin.equatix.ui.game.components.tutorial

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

enum class TutorialState {
    IDLE,
    ROW_ANIMATION,    // Soldan sağa akış
    COLUMN_ANIMATION, // Yukarıdan aşağı akış
    FINISHED
}

@Composable
fun TutorialGridLayer(
    modifier: Modifier = Modifier,
    n: Int, // Grid boyutu (3x3, 4x4 vb.)
    state: TutorialState
) {
    if (state == TutorialState.IDLE || state == TutorialState.FINISHED) return

    val infiniteTransition = rememberInfiniteTransition(label = "NeonPulse")

    // Sürekli yanıp sönen "Nefes Alma" efekti
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "Glow"
    )

    // Akış İlerlemesi (0f -> 1f)
    val progressAnim = remember { Animatable(0f) }

    // State değiştiğinde animasyonu tetikle
    LaunchedEffect(state) {
        progressAnim.snapTo(0f)
        progressAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(2000, easing = LinearEasing) // 2 saniyelik yavaş, zarif akış
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // NOT: GamePlayArea yapısında Grid (NxN) solda/üstte, Sonuçlar sağda/altta olur.
        // Bu yüzden görsel alanı (N+1) x (N+1) parçaya bölüyoruz gibi düşünebiliriz.
        // n tane hücre + 1 tane sonuç alanı.

        val totalCellsX = n + 1f // Grid + Sağdaki Sonuç Sütunu
        val totalCellsY = n + 1f // Grid + Alttaki Sonuç Satırı

        val cellWidth = width / totalCellsX
        val cellHeight = height / totalCellsY

        val progress = progressAnim.value

        // --- SATIR ANİMASYONU (Yatay) ---
        if (state == TutorialState.ROW_ANIMATION) {
            // Örnek olarak ORTA satırı seçelim (Görsel açıdan en iyisi)
            val targetRowIndex = n / 2

            val startX = cellWidth * 0.5f
            val endX = width - (cellWidth * 0.5f) // Sonuç sütununun ortasına kadar
            val yPos = (targetRowIndex * cellHeight) + (cellHeight * 0.5f)

            val currentX = androidx.compose.ui.util.lerp(startX, endX, progress)

            // 1. Arkadaki İz (Sönük)
            drawLine(
                color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                start = Offset(startX, yPos),
                end = Offset(endX, yPos),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            // 2. Parlak Akış (Gradient)
            drawPath(
                path = Path().apply {
                    moveTo(startX, yPos)
                    lineTo(currentX, yPos)
                },
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, Color(0xFF38BDF8), Color.White),
                    startX = startX,
                    endX = currentX
                ),
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Öncü Işık Küresi
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF38BDF8).copy(alpha = glowAlpha), Color.Transparent),
                    center = Offset(currentX, yPos),
                    radius = 24.dp.toPx()
                ),
                center = Offset(currentX, yPos),
                radius = 24.dp.toPx()
            )

            // 4. Hücre Vurguları (Işık üzerinden geçtikçe yanar)
            for (col in 0 until (n + 1)) {
                val cellCenterX = (col * cellWidth) + (cellWidth * 0.5f)
                // Eğer ışık bu hücreyi geçtiyse yak
                if (currentX >= cellCenterX) {
                    val isResultCell = col == n
                    val color = if (isResultCell) Color(0xFFFFD54F) else Color(0xFF38BDF8)

                    drawCircle(
                        color = color.copy(alpha = 0.3f * glowAlpha),
                        center = Offset(cellCenterX, yPos),
                        radius = (cellWidth * 0.35f)
                    )
                    // Çerçeve
                    drawCircle(
                        color = color,
                        center = Offset(cellCenterX, yPos),
                        radius = (cellWidth * 0.4f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }

        // --- SÜTUN ANİMASYONU (Dikey) ---
        else if (state == TutorialState.COLUMN_ANIMATION) {
            // Örnek olarak ORTA sütunu seçelim
            val targetColIndex = n / 2

            val startY = cellHeight * 0.5f
            val endY = height - (cellHeight * 0.5f) // Sonuç satırının ortasına kadar
            val xPos = (targetColIndex * cellWidth) + (cellWidth * 0.5f)

            val currentY = androidx.compose.ui.util.lerp(startY, endY, progress)

            // 1. Arkadaki İz
            drawLine(
                color = Color(0xFF34C759).copy(alpha = 0.2f),
                start = Offset(xPos, startY),
                end = Offset(xPos, endY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            // 2. Parlak Akış
            drawPath(
                path = Path().apply {
                    moveTo(xPos, startY)
                    lineTo(xPos, currentY)
                },
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0xFF34C759), Color.White),
                    startY = startY,
                    endY = currentY
                ),
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Öncü Işık
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF34C759).copy(alpha = glowAlpha), Color.Transparent),
                    center = Offset(xPos, currentY),
                    radius = 24.dp.toPx()
                ),
                center = Offset(xPos, currentY),
                radius = 24.dp.toPx()
            )

            // 4. Hücre Vurguları
            for (row in 0 until (n + 1)) {
                val cellCenterY = (row * cellHeight) + (cellHeight * 0.5f)
                if (currentY >= cellCenterY) {
                    val isResultCell = row == n
                    val color = if (isResultCell) Color(0xFFFFD54F) else Color(0xFF34C759)

                    drawCircle(
                        color = color.copy(alpha = 0.3f * glowAlpha),
                        center = Offset(xPos, cellCenterY),
                        radius = (cellHeight * 0.35f)
                    )
                    drawCircle(
                        color = color,
                        center = Offset(xPos, cellCenterY),
                        radius = (cellHeight * 0.4f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}