package com.vahitkeskin.equatix.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    colors: EquatixDesignSystem.ThemeColors
) {

    val n = state.size
    val gapHeight = cellSize * 0.7f
    val puzzleIdentity = remember(state.grid) {
        state.grid.map { it.correctValue }
    }

    LaunchedEffect(puzzleIdentity) {
        printBoardLog(state)
    }

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
                        colors = colors,
                        onClick = { viewModel.onCellSelected(cell.id) }
                    )

                    if (j < n - 1) {
                        OpSymbol(state.rowOps[i * (n - 1) + j], opWidth, fontSize, colors)
                    }
                }
                OpText("=", opWidth, fontSize, colors)
                GameGridResultCell(state.rowResults[i], cellSize, fontSize)
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
                GameGridResultCell(state.colResults[j], cellSize, fontSize)
                if (j < n - 1) Spacer(modifier = Modifier.width(opWidth))
            }
            Spacer(modifier = Modifier.width(opWidth + cellSize))
        }
    }
}

private fun printBoardLog(state: GameState) {
    val n = state.size
    val cellWidth = 4
    val opWidth = 3

    println("================ EQUATIX BOARD STATE ================")

    // --- İç İçe Fonksiyon: Matris Yazdırıcı ---
    fun printMatrix(title: String, isSolution: Boolean) {
        println("\n=== $title ===")
        for (i in 0 until n) {
            // 1. SATIR: Sayılar ve Yatay Operatörler
            val sbRow = StringBuilder()
            for (j in 0 until n) {
                val cell = state.grid[i * n + j]
                // Çözüm mü yoksa kullanıcının gördüğü mü?
                val value = if (isSolution) {
                    cell.correctValue.toString()
                } else {
                    if (cell.isLocked) cell.correctValue.toString()
                    else if (cell.userInput.isNotEmpty()) cell.userInput
                    else "?"
                }
                sbRow.append(value.padStart(cellWidth, ' '))

                if (j < n - 1) {
                    val op = state.rowOps[i * (n - 1) + j].symbol
                    sbRow.append(" $op ".padStart(opWidth, ' '))
                }
            }
            sbRow.append(" = ${state.rowResults[i]}")
            println(sbRow.toString())

            // 2. ARA SATIR: Dikey Operatörler
            if (i < n - 1) {
                val sbVert = StringBuilder()
                for (j in 0 until n) {
                    val op = state.colOps[j * (n - 1) + i].symbol
                    sbVert.append(" $op ".padStart(cellWidth, ' '))
                    if (j < n - 1) sbVert.append("".padStart(opWidth, ' '))
                }
                println(sbVert.toString())
            }
        }
        // 3. EN ALT: Sütun Sonuçları
        println("-".repeat(n * (cellWidth + opWidth)))
        val sbColRes = StringBuilder()
        for (j in 0 until n) {
            sbColRes.append(state.colResults[j].toString().padStart(cellWidth, ' '))
            if (j < n - 1) sbColRes.append("".padStart(opWidth, ' '))
        }
        println(sbColRes.toString())
    }

    // Fonksiyonları çağır
    printMatrix("OYUNCU EKRANI (USER VIEW)", isSolution = false)
    printMatrix("CEVAP ANAHTARI (SOLUTION)", isSolution = true)

    println("=====================================================")
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
    val targetBg = when {
        data.isLocked -> colors.cardBackground
        isSelected -> colors.accent.copy(alpha = 0.2f)
        else -> colors.gridLines.copy(alpha = 0.1f)
    }

    val backgroundColor by animateColorAsState(targetValue = targetBg, animationSpec = tween(200))
    val borderColor by animateColorAsState(targetValue = if (isSelected) colors.accent else Color.Transparent)

    Box(
        modifier = Modifier
            .size(cellSize)
            .padding(2.dp)
            .border(1.5.dp, borderColor, CircleShape)
            .background(backgroundColor, CircleShape)
            .clickable(enabled = !data.isLocked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        val textColor = if (data.isLocked) colors.textPrimary else colors.accent
        val displayText = if (data.isLocked) data.correctValue.toString() else data.userInput

        Text(
            text = displayText,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun GameGridResultCell(
    result: Int,
    cellSize: Dp,
    baseFontSize: TextUnit,
) {
    // Rakam sayısını al
    val text = result.toString()
    val length = text.length

    // Yazı boyutunu uzunluğa göre ölçekle
    // 3 basamak ise %70, 4 ve üzeri ise %60 boyuta indir.
    val dynamicFontSize = when {
        length >= 4 -> baseFontSize * 0.6f
        length == 3 -> baseFontSize * 0.7f
        else -> baseFontSize
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .padding(2.dp)
            .background(Color(0xFF34C759).copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF34C759),
            fontWeight = FontWeight.Bold,
            fontSize = dynamicFontSize, // Dinamik font
            maxLines = 1,
            softWrap = false,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun OpSymbol(op: Operation, width: Dp, fontSize: TextUnit, colors: EquatixDesignSystem.ThemeColors) {
    Box(modifier = Modifier.width(width), contentAlignment = Alignment.Center) {
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
fun VerticalEquals(cellSize: Dp, fontSize: TextUnit, colors: EquatixDesignSystem.ThemeColors) {
    val height = cellSize * 0.7f
    Box(modifier = Modifier.width(cellSize).height(height), contentAlignment = Alignment.Center) {
        Text(
            text = "=",
            modifier = Modifier.rotate(90f),
            color = colors.textSecondary,
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