package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun GameStatsBar(
    elapsedTime: Long,
    isTimerRunning: Boolean,
    isSolved: Boolean,
    onHintClick: () -> Unit,
    onPauseToggle: () -> Unit
) {
    GlassBox(
        modifier = Modifier.padding(vertical = 8.dp),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Hint Button
            if (!isSolved) {
                ControlButton(
                    icon = Icons.Outlined.Lightbulb,
                    color = Color(0xFFFFD54F),
                    size = 42.dp,
                    onClick = onHintClick
                )
            } else {
                Spacer(modifier = Modifier.size(42.dp))
            }

            // Timer Display
            AnimatedCounter(
                count = formatTime(elapsedTime),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 3.sp,
                    fontSize = 40.sp
                ),
                color = if (isTimerRunning) Color.White else Color(0xFFFF9F0A)
            )

            // Play/Pause Button
            if (!isSolved) {
                ControlButton(
                    icon = if (isTimerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    color = if (isTimerRunning) Color(0xFF38BDF8) else Color(0xFF34C759),
                    size = 42.dp,
                    onClick = onPauseToggle
                )
            } else {
                Spacer(modifier = Modifier.size(42.dp))
            }
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