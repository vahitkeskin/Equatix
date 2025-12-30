package com.vahitkeskin.equatix.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.vahitkeskin.equatix.domain.model.GameState
import com.vahitkeskin.equatix.domain.model.Operation
import com.vahitkeskin.equatix.ui.game.GameViewModel

@Composable
fun GameGrid(state: GameState, viewModel: GameViewModel, cellSize: Dp, opWidth: Dp, fontSize: TextUnit) {
    val n = state.size
    val gapHeight = cellSize * 0.5f

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        for (i in 0 until n) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (j in 0 until n) {
                    GridCell(state.grid[i*n + j], viewModel, cellSize, fontSize)
                    if (j < n - 1) OpSymbol(state.rowOps[i * (n - 1) + j], opWidth, fontSize)
                }
                OpText("=", opWidth, fontSize)
                ResultCell(state.rowResults[i], cellSize, fontSize)
            }
            if (i < n - 1) {
                Row(modifier = Modifier.height(gapHeight), verticalAlignment = Alignment.CenterVertically) {
                    for (j in 0 until n) {
                        VerticalOp(state.colOps[j * (n - 1) + i], cellSize, fontSize)
                        if (j < n - 1) Spacer(modifier = Modifier.width(opWidth))
                    }
                    Spacer(modifier = Modifier.width(opWidth + cellSize))
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        // DİKEY EŞİTTİRLER
        Row(verticalAlignment = Alignment.CenterVertically) {
            for (j in 0 until n) {
                VerticalEquals(cellSize, fontSize)
                if (j < n - 1) Spacer(modifier = Modifier.width(opWidth))
            }
            Spacer(modifier = Modifier.width(opWidth + cellSize))
        }

        // EN ALT SONUÇLAR
        Row(verticalAlignment = Alignment.CenterVertically) {
            for (j in 0 until n) {
                ResultCell(state.colResults[j], cellSize, fontSize)
                if (j < n - 1) Spacer(modifier = Modifier.width(opWidth))
            }
            Spacer(modifier = Modifier.width(opWidth + cellSize))
        }
    }
}

@Composable
fun VerticalEquals(width: Dp, fontSize: TextUnit) {
    Box(modifier = Modifier.width(width).height(20.dp), contentAlignment = Alignment.Center) {
        Text("=", modifier = Modifier.rotate(90f), color = Color.Gray, fontWeight = FontWeight.Light, fontSize = fontSize)
    }
}

@Composable
fun OpSymbol(op: Operation, width: Dp, fontSize: TextUnit) {
    Box(modifier = Modifier.width(width), contentAlignment = Alignment.Center) {
        val color = when(op) {
            Operation.ADD -> Color(0xFF0A84FF)
            Operation.SUB -> Color(0xFFFF453A)
            Operation.MUL -> Color(0xFFFF9F0A)
            Operation.DIV -> Color(0xFFAB47BC) // Mor renk
        }
        Text(text = op.symbol, color = color, fontWeight = FontWeight.Normal, fontSize = fontSize)
    }
}

@Composable
fun VerticalOp(op: Operation, width: Dp, fontSize: TextUnit) {
    Box(modifier = Modifier.width(width), contentAlignment = Alignment.Center) {
        val color = when(op) {
            Operation.ADD -> Color(0xFF0A84FF)
            Operation.SUB -> Color(0xFFFF453A)
            Operation.MUL -> Color(0xFFFF9F0A)
            Operation.DIV -> Color(0xFFAB47BC)
        }
        Text(text = op.symbol, color = color, fontWeight = FontWeight.Normal, fontSize = fontSize)
    }
}

@Composable
fun OpText(text: String, width: Dp, fontSize: TextUnit) {
    Box(modifier = Modifier.width(width), contentAlignment = Alignment.Center) {
        Text(text = text, color = Color.Gray, fontWeight = FontWeight.Light, fontSize = fontSize)
    }
}