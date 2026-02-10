package com.vahitkeskin.equatix.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.utils.PreviewContainer
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.vahitkeskin.equatix.domain.model.CellData
import com.vahitkeskin.equatix.ui.game.GameViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign

@Composable
fun GridCell(
    data: CellData,
    isSelected: Boolean,
    isSolved: Boolean,
    size: Dp,
    fontSize: TextUnit,
    colors: EquatixDesignSystem.ThemeColors,
    onClick: () -> Unit
) {
    // RENK MANTIĞI:
    val targetBgColor = when {
        data.isLocked -> colors.cardBackground
        isSelected -> colors.accent.copy(alpha = 0.2f)
        else -> colors.gridLines.copy(alpha = 0.1f)
    }

    val backgroundColor by animateColorAsState(targetValue = targetBgColor, animationSpec = tween(200))
    val borderColor by animateColorAsState(targetValue = if (isSelected) colors.accent else Color.Transparent)

    // TEXT RENGİ:
    val textColor = when {
        data.isRevealedBySystem -> colors.gold
        data.isLocked -> colors.textPrimary
        else -> colors.accent
    }

    Box(
        modifier = Modifier
            .size(size)
            .padding(2.dp)
            .border(1.5.dp, borderColor, CircleShape)
            .background(backgroundColor, CircleShape)
            .clickable(enabled = !data.isLocked && !isSolved) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        val displayText = if (data.isLocked || data.isRevealedBySystem) {
            data.correctValue.toString()
        } else {
            data.userInput
        }

        Text(
            text = displayText,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Preview
@Composable
fun PreviewGridCell() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PreviewContainer(isDark = true) { colors, _ ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GridCell(
                    data = CellData(1, 5, isHidden = false, isLocked = true),
                    isSelected = false,
                    isSolved = false,
                    size = 48.dp,
                    fontSize = 20.sp,
                    colors = colors,
                    onClick = {}
                )
                GridCell(
                    data = CellData(2, 3, isHidden = false, userInput = "3"),
                    isSelected = true,
                    isSolved = false,
                    size = 48.dp,
                    fontSize = 20.sp,
                    colors = colors,
                    onClick = {}
                )
            }
        }
        
        PreviewContainer(isDark = false) { colors, _ ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GridCell(
                    data = CellData(1, 5, isHidden = false, isLocked = true),
                    isSelected = false,
                    isSolved = false,
                    size = 48.dp,
                    fontSize = 20.sp,
                    colors = colors,
                    onClick = {}
                )
                GridCell(
                    data = CellData(2, 3, isHidden = false, userInput = "3"),
                    isSelected = true,
                    isSolved = false,
                    size = 48.dp,
                    fontSize = 20.sp,
                    colors = colors,
                    onClick = {}
                )
            }
        }
    }
}