package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    isVibrationEnabled: Boolean,
    isTimerVisible: Boolean,
    colors: EquatixDesignSystem.ThemeColors,
    onHintClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onVibrationToggle: () -> Unit,
    onTimerToggle: () -> Unit
) {
    GlassBox(
        modifier = Modifier.padding(vertical = 8.dp),
        cornerRadius = 16.dp
    ) {
        // Light modda camın içi çok şeffaf olmasın, biraz beyazımsı olsun
        Box(
            modifier = Modifier.background(colors.cardBackground)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // --- SOL GRUP ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isSolved) {
                        ControlButton(
                            icon = Icons.Outlined.Lightbulb,
                            color = colors.gold,
                            size = 42.dp,
                            onClick = onHintClick
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ControlButton(
                            icon = if (isVibrationEnabled) Icons.Rounded.Vibration else Icons.Outlined.Smartphone,
                            color = if (isVibrationEnabled) colors.accent else Color.Gray,
                            size = 42.dp,
                            onClick = onVibrationToggle
                        )
                    } else {
                        Spacer(modifier = Modifier.width(92.dp))
                    }
                }

                // --- ORTA: Zamanlayıcı ---
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.height(50.dp)
                ) {
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
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 3.sp,
                                    fontSize = 40.sp
                                ),
                                color = if (isTimerRunning) colors.textPrimary else Color(0xFFFF9F0A)
                            )
                        } else {
                            FocusPulseAnimation(isPaused = !isTimerRunning, accentColor = colors.accent)
                        }
                    }
                }

                // --- SAĞ GRUP ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isSolved) {
                        ControlButton(
                            icon = if (isTimerVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            color = if (isTimerVisible) Color.Gray else colors.textPrimary,
                            size = 42.dp,
                            onClick = onTimerToggle
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ControlButton(
                            icon = if (isTimerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            color = if (isTimerRunning) colors.accent else colors.success,
                            size = 42.dp,
                            onClick = onPauseToggle
                        )
                    } else {
                        Spacer(modifier = Modifier.width(92.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FocusPulseAnimation(isPaused: Boolean, accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPaused) 1f else 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isPaused) 0.3f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Alpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.width(120.dp)
    ) {
        Canvas(modifier = Modifier.size(30.dp, 2.dp)) {
            drawRoundRect(color = Color.Gray.copy(alpha = 0.3f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(contentAlignment = Alignment.Center) {
            if (!isPaused) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawCircle(color = accentColor, radius = size.minDimension / 2 * scale, alpha = alpha)
                }
            }
            Canvas(modifier = Modifier.size(8.dp)) {
                drawCircle(color = if (isPaused) Color.Gray else accentColor)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Canvas(modifier = Modifier.size(30.dp, 2.dp)) {
            drawRoundRect(color = Color.Gray.copy(alpha = 0.3f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f))
        }
    }
}

@Composable
fun ControlButton(
    icon: ImageVector,
    color: Color,
    size: Dp = 48.dp,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = color.copy(alpha = 0.15f),
        modifier = Modifier.size(size),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(size * 0.55f))
        }
    }
}

@Preview
@Composable
fun PreviewGameStatsBar() {
    // 1. Tema Renklerini Al (Dark Mode)
    val isDark = true
    val colors = EquatixDesignSystem.getColors(isDark)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background) // Temadan gelen arka plan
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("1. Normal Mod", color = colors.textSecondary, fontSize = 12.sp)
            GameStatsBar(
                elapsedTime = 65, // 01:05
                isTimerRunning = true,
                isSolved = false,
                isVibrationEnabled = true,
                isTimerVisible = true,
                colors = colors, // <--- YENİ EKLENEN PARAMETRE
                onHintClick = {},
                onPauseToggle = {},
                onVibrationToggle = {},
                onTimerToggle = {}
            )

            Text("2. Odak Modu (Gizli Süre)", color = colors.textSecondary, fontSize = 12.sp)
            GameStatsBar(
                elapsedTime = 120,
                isTimerRunning = true,
                isSolved = false,
                isVibrationEnabled = false,
                isTimerVisible = false,
                colors = colors, // <--- YENİ EKLENEN PARAMETRE
                onHintClick = {},
                onPauseToggle = {},
                onVibrationToggle = {},
                onTimerToggle = {}
            )

            Text("3. Oyun Duraklatıldı", color = colors.textSecondary, fontSize = 12.sp)
            GameStatsBar(
                elapsedTime = 120,
                isTimerRunning = false,
                isSolved = false,
                isVibrationEnabled = true,
                isTimerVisible = false,
                colors = colors, // <--- YENİ EKLENEN PARAMETRE
                onHintClick = {},
                onPauseToggle = {},
                onVibrationToggle = {},
                onTimerToggle = {}
            )
        }
    }
}