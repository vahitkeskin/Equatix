package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.domain.model.Operation
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.components.GameGrid
import com.vahitkeskin.equatix.ui.utils.PreviewContainer
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.vahitkeskin.equatix.ui.game.GameViewModel
import com.vahitkeskin.equatix.ui.game.components.tutorial.TutorialGridLayer
import com.vahitkeskin.equatix.ui.game.components.tutorial.TutorialState
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import kotlin.math.min

@Composable
fun GamePlayArea(
    modifier: Modifier = Modifier,
    gameState: GameState,
    selectedCellIndex: Int?,
    isSolved: Boolean,
    onCellClick: (Int) -> Unit,
    tutorialState: TutorialState = TutorialState.IDLE,
    colors: EquatixDesignSystem.ThemeColors,
    isDark: Boolean
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val n = gameState.size
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
                                state = gameState,
                                selectedCellIndex = selectedCellIndex,
                                isSolved = isSolved,
                                cellSize = cellSize,
                                opWidth = opWidth,
                                fontSize = fontSize,
                                colors = colors,
                                onCellClick = onCellClick
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

private fun Difficulty.generateBoard(size: GridSize): GameState {
    val s = size.value
    return GameState(
        size = s,
        grid = List(s * s) { index ->
            CellData(
                id = index,
                correctValue = 1,
                isHidden = false
            )
        },
        rowOps = emptyList(),
        colOps = emptyList(),
        rowResults = emptyList(),
        colResults = emptyList(),
        difficulty = this
    )
}

@Preview
@Composable
fun PreviewGamePlayArea() {
    val mockState = Difficulty.EASY.generateBoard(GridSize.SIZE_3x3)
    
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
        PreviewContainer(isDark = true) { colors, _ ->
            GamePlayArea(
                gameState = mockState,
                selectedCellIndex = 0,
                isSolved = false,
                onCellClick = {},
                colors = colors,
                isDark = true
            )
        }
        
        PreviewContainer(isDark = false) { colors, _ ->
            GamePlayArea(
                gameState = mockState,
                selectedCellIndex = null,
                isSolved = true,
                onCellClick = {},
                colors = colors,
                isDark = false
            )
        }
    }
}