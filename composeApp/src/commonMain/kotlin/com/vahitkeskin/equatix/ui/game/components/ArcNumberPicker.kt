package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs

@Composable
fun ArcNumberPicker(
    colors: EquatixDesignSystem.ThemeColors,
    onNumberSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 0-9 Arası sayılar
    val numbers = remember { (0..9).map { it.toString() } }

    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    // Animasyon Değişkenleri
    val angleOffset = remember { Animatable(0f) }
    val dragSensitivity = 0.005f
    val maxArcAngle = PI.toFloat() * 0.35f
    val cardCount = numbers.size
    val stepAngle = if (cardCount > 1) (2f * maxArcAngle) / (cardCount - 1) else 0f

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp), // Yüksekliği sınırla ki Grid'e yer kalsın
        contentAlignment = Alignment.BottomCenter
    ) {
        val width = maxWidth

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            angleOffset.snapTo(angleOffset.value + (delta * dragSensitivity))
                        }
                    },
                    onDragStopped = {
                        scope.launch {
                            val baseAngle = -maxArcAngle
                            val angles = numbers.indices.map { index ->
                                val angle = baseAngle + index * stepAngle + angleOffset.value
                                index to angle
                            }
                            val (closestIndex, closestAngle) = angles.minBy { (_, angle) -> abs(angle) }

                            val targetOffset = angleOffset.value - closestAngle
                            angleOffset.animateTo(
                                targetOffset,
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    }
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            val midIndex = (cardCount - 1) / 2f

            numbers.forEachIndexed { index, number ->
                val currentBaseAngle = (index - midIndex) * stepAngle
                val angle = currentBaseAngle + angleOffset.value
                val t = (angle / maxArcAngle).coerceIn(-1.5f, 1.5f)

                // Sadece görünür alandakileri çiz (Performans)
                if (abs(t) <= 1.2f) {
                    val maxHorizontalOffset = (width.value * density.density) / 2.5f
                    val x = maxHorizontalOffset * t

                    val arcDepthPx = with(density) { 60.dp.toPx() }
                    val y = abs(t) * arcDepthPx * 0.5f

                    val scale = 1f - (abs(t) * 0.3f)
                    val alpha = (1f - abs(t)).coerceIn(0.4f, 1f)
                    val rotation = t * 20f

                    NumberCard(
                        number = number,
                        isSelected = abs(t) < 0.1f,
                        colors = colors,
                        modifier = Modifier
                            .offset(y = (-20).dp)
                            .graphicsLayer {
                                translationX = x
                                translationY = y
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                                rotationZ = rotation
                            }
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    onNumberSelected(number)
                                    scope.launch {
                                        val targetOffset = angleOffset.value - angle
                                        angleOffset.animateTo(
                                            targetOffset,
                                            spring(stiffness = Spring.StiffnessMedium)
                                        )
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberCard(
    number: String,
    isSelected: Boolean,
    colors: EquatixDesignSystem.ThemeColors,
    modifier: Modifier = Modifier
) {
    val size = 64.dp
    // Seçiliyse Accent rengi, değilse kart rengi
    val bgColor = if (isSelected) colors.accent else colors.cardBackground
    val textColor = if (isSelected) Color.White else colors.textPrimary
    val borderColor = if (isSelected) colors.accent else colors.textPrimary.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = if (isSelected) 12.dp else 4.dp,
                shape = CircleShape,
                spotColor = if (isSelected) colors.accent else Color.Black
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        bgColor.copy(alpha = 0.95f),
                        bgColor.copy(alpha = 0.8f)
                    )
                ),
                shape = CircleShape
            )
            .border(1.dp, borderColor, CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )
    }
}