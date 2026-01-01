package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    isTimerRunning: Boolean,
    isSolved: Boolean,
    tutorialState: TutorialState = TutorialState.IDLE,
    colors: EquatixDesignSystem.ThemeColors,
    isDark: Boolean
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(), // Mevcut alanın tamamını kullan
        contentAlignment = Alignment.Center
    ) {
        val availableW = maxWidth.value
        val availableH = maxHeight.value

        if (availableW <= 0 || availableH <= 0) return@BoxWithConstraints

        // --- GRID HESAPLAMA MOTORU ---
        // 5x5'te sıkışmayı önlemek için matematiksel oranlar
        val opRatio = 0.65f

        // YATAY BİRİM SAYISI
        // N sayı + (N-1) işlem + Eşittir + Sonuç
        val totalUnitsX = n + ((n - 1) * opRatio) + 0.5f + 1.0f

        // DİKEY BİRİM SAYISI (Alttaki yeşil toplar sığsın diye)
        // N sayı + (N-1) işlem + Eşittir + Sonuç
        val totalUnitsY = n + ((n - 1) * opRatio) + 0.5f + 1.0f

        // Ekranın en ve boyuna göre, bir birimin maksimum pixel değeri
        val unitSizeX = availableW / totalUnitsX
        val unitSizeY = availableH / totalUnitsY

        // En küçük olanı seç ki ekrandan taşmasın
        val unitSizeValue = min(unitSizeX, unitSizeY)

        // Değerleri ata
        val cellSize = unitSizeValue.dp
        val opWidth = (unitSizeValue * opRatio).dp

        // Font boyutu: Biraz daha büyüttük (0.45 -> 0.50) ki okunabilsin
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
                                fontSize = fontSize
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

            // Pause Overlay
            if (!isTimerRunning && !isSolved && tutorialState == TutorialState.IDLE) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(0.75f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Rounded.Pause,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "PAUSED",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 4.sp
                        )
                    }
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
                isTimerRunning = false,
                isSolved = true,
                colors = colors,
                isDark = isDark
            )
        }
    }
}