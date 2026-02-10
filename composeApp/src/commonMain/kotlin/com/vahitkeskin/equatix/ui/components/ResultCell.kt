package com.vahitkeskin.equatix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import com.vahitkeskin.equatix.ui.utils.PreviewContainer

@Composable
fun ResultCell(value: Int, cellSize: Dp, fontSize: TextUnit, colors: EquatixDesignSystem.ThemeColors) {
    Box(
        modifier = Modifier
            .size(cellSize)
            .padding(2.dp)
            .background(colors.success.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            color = colors.success,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun PreviewResultCell() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PreviewContainer(isDark = true) { colors, _ ->
            ResultCell(value = 15, cellSize = 60.dp, fontSize = 24.sp, colors = colors)
        }
        
        PreviewContainer(isDark = false) { colors, _ ->
            ResultCell(value = 42, cellSize = 60.dp, fontSize = 24.sp, colors = colors)
        }
    }
}