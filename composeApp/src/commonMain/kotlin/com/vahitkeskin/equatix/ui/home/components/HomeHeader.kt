package com.vahitkeskin.equatix.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

@Composable
fun HomeHeader(
    isDark: Boolean,
    colors: EquatixDesignSystem.ThemeColors,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val btnContainerColor = if(isDark) Color.White.copy(0.05f) else Color(0xFFE2E8F0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HeaderButton(
            icon = Icons.Default.EmojiEvents,
            iconColor = colors.gold,
            containerColor = btnContainerColor,
            onClick = onHistoryClick
        )
        HeaderButton(
            icon = Icons.Default.Settings,
            iconColor = if(isDark) Color.White else Color(0xFF334155),
            containerColor = btnContainerColor,
            onClick = onSettingsClick
        )
    }
}

@Composable
private fun HeaderButton(icon: ImageVector, iconColor: Color, containerColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = containerColor,
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iconColor)
        }
    }
}