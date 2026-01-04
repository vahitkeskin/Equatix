package com.vahitkeskin.equatix.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.vahitkeskin.equatix.domain.model.CellData
import com.vahitkeskin.equatix.domain.model.GameState
import com.vahitkeskin.equatix.domain.model.Operation
import com.vahitkeskin.equatix.ui.game.GameViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

@Composable
fun GameGrid(
    state: GameState,
    viewModel: GameViewModel,
    cellSize: Dp,
    opWidth: Dp,
    fontSize: TextUnit,
    colors: EquatixDesignSystem.ThemeColors // <--- TEMA EKLENDİ
) {
    val n = state.size
    val gapHeight = cellSize * 0.5f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        for (i in 0 until n) {
            // --- SATIRLAR ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (j in 0 until n) {
                    val cell = state.grid[i * n + j]
                    val isSelected = viewModel.selectedCellIndex == cell.id

                    GridCell(
                        data = cell,
                        isSelected = isSelected,
                        cellSize = cellSize,
                        fontSize = fontSize,
                        colors = colors, // Rengi iletiyoruz
                        onClick = { viewModel.onCellSelected(cell.id) }
                    )

                    if (j < n - 1) {
                        OpSymbol(state.rowOps[i * (n - 1) + j], opWidth, fontSize, colors)
                    }
                }
                OpText("=", opWidth, fontSize, colors)
                ResultCell(state.rowResults[i], cellSize, fontSize)
            }

            // --- ARA SATIRLAR (Dikey Operatörler) ---
            if (i < n - 1) {
                Row(
                    modifier = Modifier.height(gapHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (j in 0 until n) {
                        VerticalOp(state.colOps[j * (n - 1) + i], cellSize, fontSize, colors)
                        if (j < n - 1) Spacer(modifier = Modifier.width(opWidth))
                    }
                    Spacer(modifier = Modifier.width(opWidth + cellSize))
                }
            }
        }

        // --- DİKEY EŞİTTİRLER ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            for (j in 0 until n) {
                VerticalEquals(cellSize, fontSize, colors)
                if (j < n - 1) Spacer(modifier = Modifier.width(opWidth))
            }
            Spacer(modifier = Modifier.width(opWidth + cellSize))
        }

        // --- EN ALT SONUÇLAR ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            for (j in 0 until n) {
                ResultCell(state.colResults[j], cellSize, fontSize)
                if (j < n - 1) Spacer(modifier = Modifier.width(opWidth))
            }
            Spacer(modifier = Modifier.width(opWidth + cellSize))
        }
    }
}

// ----------------------------------------------------------------
// ALT BİLEŞENLER
// ----------------------------------------------------------------

@Composable
fun GridCell(
    data: CellData,
    isSelected: Boolean,
    cellSize: Dp,
    fontSize: TextUnit,
    colors: EquatixDesignSystem.ThemeColors,
    onClick: () -> Unit
) {
    // Light Mod: Temiz kart rengi / Dark Mod: Hafif transparan siyah
    val targetBg = when {
        data.isLocked -> colors.cardBackground
        isSelected -> colors.accent.copy(alpha = 0.2f)
        else -> colors.gridLines.copy(alpha = 0.1f)
    }

    val backgroundColor by animateColorAsState(targetValue = targetBg, animationSpec = tween(200))

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) colors.accent else Color.Transparent
    )

    Box(
        modifier = Modifier
            .size(cellSize)
            .padding(2.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !data.isLocked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Kullanıcı girdisi Accent rengiyle, Sabit sayılar Ana Metin rengiyle
        val textColor = if (data.isLocked) colors.textPrimary else colors.accent
        val displayText = if (data.isLocked) data.correctValue.toString() else data.userInput

        Text(
            text = displayText,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun OpSymbol(
    op: Operation,
    width: Dp,
    fontSize: TextUnit,
    colors: EquatixDesignSystem.ThemeColors
) {
    Box(modifier = Modifier.width(width), contentAlignment = Alignment.Center) {
        // Temadan gelen özel operatör renkleri
        val color = when(op) {
            Operation.ADD -> colors.opAdd
            Operation.SUB -> colors.opSub
            Operation.MUL -> colors.opMul
            Operation.DIV -> colors.opDiv
        }
        Text(text = op.symbol, color = color, fontWeight = FontWeight.Normal, fontSize = fontSize)
    }
}

@Composable
fun VerticalOp(op: Operation, width: Dp, fontSize: TextUnit, colors: EquatixDesignSystem.ThemeColors) {
    OpSymbol(op, width, fontSize, colors)
}

@Composable
fun VerticalEquals(width: Dp, fontSize: TextUnit, colors: EquatixDesignSystem.ThemeColors) {
    Box(modifier = Modifier.width(width).height(20.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "=",
            modifier = Modifier.rotate(90f),
            color = colors.textSecondary, // İkincil renk (Daha silik)
            fontWeight = FontWeight.Light,
            fontSize = fontSize
        )
    }
}

@Composable
fun OpText(text: String, width: Dp, fontSize: TextUnit, colors: EquatixDesignSystem.ThemeColors) {
    Box(modifier = Modifier.width(width), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = colors.textSecondary,
            fontWeight = FontWeight.Light,
            fontSize = fontSize
        )
    }
}