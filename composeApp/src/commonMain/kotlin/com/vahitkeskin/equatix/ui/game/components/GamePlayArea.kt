package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.domain.model.CellData
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GameState
import com.vahitkeskin.equatix.domain.model.Operation
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.components.GameGrid
import com.vahitkeskin.equatix.ui.game.GameViewModel
import com.vahitkeskin.equatix.ui.game.components.tutorial.TutorialGridLayer
import com.vahitkeskin.equatix.ui.game.components.tutorial.TutorialState
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.min

@Composable
fun GamePlayArea(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel,
    n: Int,
    tutorialState: TutorialState = TutorialState.IDLE,
    colors: EquatixDesignSystem.ThemeColors,
    isDark: Boolean
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // --- PADDING & SAFE MARGINS ---
        // GlassBox padding (16dp) + Column padding (16dp) + Shadow margin
        val horizontalPadding = 32.dp
        val verticalPadding = 32.dp

        val availableW = (maxWidth - horizontalPadding).value
        val availableH = (maxHeight - verticalPadding).value

        if (availableW <= 0 || availableH <= 0) return@BoxWithConstraints

        // --- GRID HESAPLAMA MOTORU ---
        val opRatio = 0.70f 

        // YATAY BİRİM SAYISI
        // (N+1) hücre + N işlem (opRatio)
        val totalUnitsX = (1.7f * n) + 1.2f 

        // DİKEY BİRİM SAYISI
        // N hücre + (N-1) ara işlem (0.7) + dikey eşittir (0.7) + sonuç (1.0)
        // N + (N-1)*0.7 + 0.7 + 1.0 = 1.7N + 1.0
        val totalUnitsY = (1.7f * n) + 1.2f // 0.2f emniyet payı

        // Ekranın en ve boyuna göre, bir birimin maksimum pixel değeri
        val unitSizeX = availableW / totalUnitsX
        val unitSizeY = availableH / totalUnitsY

        // En küçük olanı seç ki ekrandan taşmasın
        val unitSizeValue = min(unitSizeX, unitSizeY)

        // Değerleri ata
        val cellSize = unitSizeValue.dp
        val opWidth = (unitSizeValue * opRatio).dp

        // Font boyutu
        val fontSize = (unitSizeValue * 0.50f).sp

        Box {
            val shadowElevation = if (isDark) 0.dp else 16.dp

            GlassBox(
                modifier = Modifier
                    .wrapContentSize()
                    .shadow(shadowElevation, RoundedCornerShape(24.dp)),
                cornerRadius = 24.dp
            ) {
                Box(
                    modifier = Modifier
                        .background(colors.cardBackground)
                        .padding(8.dp), // Çok az padding, maksimum içerik
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        MaterialTheme(
                            colorScheme = MaterialTheme.colorScheme.copy(
                                onSurface = colors.textPrimary,
                                surface = colors.textPrimary
                            )
                        ) {
                            GameGrid(
                                state = viewModel.gameState!!,
                                viewModel = viewModel,
                                cellSize = cellSize,
                                opWidth = opWidth,
                                fontSize = fontSize,
                                colors = colors
                            )
                        }
                    }

                    TutorialGridLayer(
                        modifier = Modifier.matchParentSize(),
                        n = n,
                        state = tutorialState
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewGamePlayAreaValid() {
    val viewModel = remember { GameViewModel() }
    val isDark = true
    val colors = EquatixDesignSystem.getColors(isDark)

    LaunchedEffect(Unit) {
        val fixedNumbers = listOf(8, 4, 2, 12, 10, 5, 100, 25, 4)
        val customCells = fixedNumbers.mapIndexed { index, value ->
            CellData(
                id = index,
                correctValue = value,
                isHidden = true,
                userInput = value.toString(),
                isRevealedBySystem = false,
                isLocked = false
            )
        }
        val rowOps = listOf(
            Operation.ADD,
            Operation.MUL,
            Operation.ADD,
            Operation.DIV,
            Operation.SUB,
            Operation.MUL
        )
        val colOps = listOf(
            Operation.ADD,
            Operation.ADD,
            Operation.MUL,
            Operation.ADD,
            Operation.MUL,
            Operation.ADD
        )
        val rowResults = listOf(16, 14, 0)
        val colResults = listOf(120, 65, 14)
        val validState =
            GameState(3, customCells, rowOps, colOps, rowResults, colResults, Difficulty.HARD)
        viewModel.loadPreviewState(validState)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(colors.background),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.gameState != null) {
            GamePlayArea(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
                n = 3,
                colors = colors,
                isDark = isDark
            )
        }
    }
}