package com.vahitkeskin.equatix.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.vahitkeskin.equatix.ui.utils.PreviewContainer
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

@Composable
fun HomeBranding(isDark: Boolean, colors: EquatixDesignSystem.ThemeColors) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Gradient Logic
        val gradientColors = if (isDark) {
            listOf(colors.accent, Color.White)
        } else {
            listOf(Color(0xFF0F172A), colors.accent)
        }

        Text(
            text = "EQUATIX",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 8.sp,
                brush = Brush.linearGradient(colors = gradientColors)
            ),
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "MATRIX PUZZLE",
            color = if (isDark) colors.accent.copy(alpha = 0.8f) else Color(0xFF64748B),
            fontSize = 14.sp,
            letterSpacing = 6.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun PreviewHomeBranding() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PreviewContainer(isDark = true) { colors, _ ->
            Box(modifier = Modifier.padding(16.dp)) {
                HomeBranding(isDark = true, colors = colors)
            }
        }
        
        PreviewContainer(isDark = false) { colors, _ ->
            Box(modifier = Modifier.padding(16.dp)) {
                HomeBranding(isDark = false, colors = colors)
            }
        }
    }
}