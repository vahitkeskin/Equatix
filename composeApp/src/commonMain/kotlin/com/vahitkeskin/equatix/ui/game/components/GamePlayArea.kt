package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.components.GameGrid
import com.vahitkeskin.equatix.ui.game.GameViewModel
import kotlin.math.min

@Composable
fun GamePlayArea(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel,
    n: Int,
    isTimerRunning: Boolean,
    isSolved: Boolean
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Calculate responsive cell sizes
        val minDimension = min(maxWidth.value, maxHeight.value)
        val safeDimension = minDimension * 0.95f
        val opWidthRatio = 0.65f
        val totalUnitsInRow = n + (n * opWidthRatio) + 1.1f
        val cellSizeValue = safeDimension / totalUnitsInRow
        val cellSize = cellSizeValue.dp
        val opWidth = (cellSizeValue * opWidthRatio).dp
        val fontSize = (cellSizeValue * 0.42f).sp

        Box {
            GlassBox(modifier = Modifier.wrapContentSize(), cornerRadius = 24.dp) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GameGrid(
                        state = viewModel.gameState!!,
                        viewModel = viewModel,
                        cellSize = cellSize,
                        opWidth = opWidth,
                        fontSize = fontSize
                    )
                }
            }

            // Pause Overlay
            if (!isTimerRunning && !isSolved) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(0.85f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Pause, null, tint = Color.White, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("PAUSED", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                }
            }
        }
    }
}