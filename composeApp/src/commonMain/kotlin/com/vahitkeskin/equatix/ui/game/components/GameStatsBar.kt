package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.components.AnimatedCounter
import com.vahitkeskin.equatix.ui.game.utils.formatTime
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameStatsBar(
    elapsedTime: Long,
    isTimerRunning: Boolean,
    isSolved: Boolean,
    isTimerVisible: Boolean,
    colors: EquatixDesignSystem.ThemeColors,
    isDark: Boolean,
    onPauseToggle: () -> Unit,
    onTimerToggle: () -> Unit
) {
    GlassBox(
        modifier = Modifier.padding(vertical = 4.dp),
        cornerRadius = 20.dp
    ) {
        // Row yerine Box kullanarak tam merkezleme garantisi veriyoruz
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .height(50.dp) // Yüksekliği biraz daha kıstık (Daha fazla yer kalsın)
        ) {

            // --- SOL BUTON (Gizle/Göster) ---
            if (!isSolved) {
                Box(modifier = Modifier.align(Alignment.CenterStart)) {
                    ControlButton(
                        icon = if (isTimerVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        color = if (isTimerVisible) colors.textSecondary else colors.textPrimary,
                        size = 42.dp,
                        onClick = onTimerToggle
                    )
                }
            }

            // --- ORTA (Kronometre) ---
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(140.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                // Arka Plan Efekti
                TimerCosmicEffect(isDark = isDark, colors = colors)

                // Süre
                AnimatedContent(
                    targetState = isTimerVisible,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f))
                            .togetherWith(fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f))
                    },
                    label = "TimerVisibility"
                ) { visible ->
                    if (visible) {
                        AnimatedCounter(
                            count = formatTime(elapsedTime),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                fontSize = 30.sp // Fontu optimize ettik
                            ),
                            color = if (isTimerRunning) colors.textPrimary else Color(0xFFFF9F0A)
                        )
                    } else {
                        FocusPulseAnimation(isPaused = !isTimerRunning, accentColor = colors.accent)
                    }
                }
            }

            // --- SAĞ BUTON (Pause/Play) ---
            if (!isSolved) {
                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    ControlButton(
                        icon = if (isTimerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        color = if (isTimerRunning) colors.accent else colors.success,
                        size = 42.dp,
                        onClick = onPauseToggle
                    )
                }
            }
        }
    }
}

// --- Görsel Efektler ---
@Composable
private fun TimerCosmicEffect(isDark: Boolean, colors: EquatixDesignSystem.ThemeColors) {
    val infiniteTransition = rememberInfiniteTransition(label = "TimerGlow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f, targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Alpha"
    )

    Box(
        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = colors.accent.copy(alpha = if (isDark) 0.1f else 0.05f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx())
            )
            drawRoundRect(
                color = colors.accent.copy(alpha = alpha),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx())
            )
        }
    }
}

@Composable
fun FocusPulseAnimation(isPaused: Boolean, accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = if (isPaused) 1f else 1.3f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "S"
    )
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color = accentColor, radius = size.minDimension / 2 * scale)
        }
    }
}

@Composable
fun ControlButton(icon: ImageVector, color: Color, size: Dp, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = color.copy(alpha = 0.1f),
        modifier = Modifier.size(size),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(size * 0.5f))
        }
    }
}

@Preview
@Composable
fun PreviewGameStatsBar() {
    val darkColors = EquatixDesignSystem.getColors(true)
    Box(modifier = Modifier.fillMaxWidth().background(darkColors.background).padding(16.dp)) {
        GameStatsBar(65, true, false, true, darkColors, true, {}, {})
    }
}