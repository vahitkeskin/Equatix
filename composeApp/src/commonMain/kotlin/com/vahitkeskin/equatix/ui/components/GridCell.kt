package com.vahitkeskin.equatix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.vahitkeskin.equatix.domain.model.CellData
import com.vahitkeskin.equatix.ui.game.GameViewModel

@Composable
fun GridCell(data: CellData, viewModel: GameViewModel, size: Dp, fontSize: TextUnit) {
    val isSelected = viewModel.selectedCellIndex == data.id
    val bgColor = when {
        data.isLocked -> Color.White.copy(alpha = 0.05f)
        isSelected -> Color(0xFF34C759).copy(alpha = 0.3f)
        else -> Color.White.copy(alpha = 0.1f)
    }
    val borderColor = if (isSelected) Color(0xFF34C759) else Color.White.copy(alpha = 0.1f)
    val textColor = if (data.isLocked) Color.Gray else if (data.isRevealedBySystem) Color(0xFFFF9F0A) else Color.White

    Box(modifier = Modifier.size(size).clip(RoundedCornerShape(8.dp)).background(bgColor).border(1.dp, borderColor, RoundedCornerShape(8.dp)).clickable(enabled = !data.isLocked && !viewModel.isSolved) { viewModel.onCellSelected(data.id) }, contentAlignment = Alignment.Center) {
        Text(text = if (data.isHidden) data.userInput else data.correctValue.toString(), fontSize = fontSize, fontWeight = FontWeight.Medium, color = textColor)
    }
}