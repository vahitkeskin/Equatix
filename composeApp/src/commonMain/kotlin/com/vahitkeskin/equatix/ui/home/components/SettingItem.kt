package com.vahitkeskin.equatix.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.utils.PreviewContainer
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Vibration

@Composable
fun SettingItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    isOn: Boolean,
    isDark: Boolean,
    colors: EquatixDesignSystem.ThemeColors,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isOn) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colors.textSecondary.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = colors.textPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Switch(
            checked = isOn,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colors.accent,
                uncheckedThumbColor = colors.textSecondary,
                uncheckedTrackColor = colors.cardBackground
            )
        )
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun PreviewSettingItem() {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.background(Color.Gray).padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        com.vahitkeskin.equatix.ui.utils.PreviewContainer(isDark = true) { colors, _ ->
            SettingItem(
                title = "Background Music",
                subtitle = "Relaxing lo-fi melodies",
                icon = Icons.Rounded.MusicNote,
                isOn = true,
                isDark = true,
                colors = colors,
                onToggle = {}
            )
        }
        
        com.vahitkeskin.equatix.ui.utils.PreviewContainer(isDark = false) { colors, _ ->
            SettingItem(
                title = "Vibration",
                icon = Icons.Rounded.Vibration,
                isOn = false,
                isDark = false,
                colors = colors,
                onToggle = {}
            )
        }
    }
}