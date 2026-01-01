package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val w = maxWidth.value
        val h = maxHeight.value
        val isValidConstraints = w > 0 && h > 0 && w.isFinite() && h.isFinite()
        if (!isValidConstraints) return@BoxWithConstraints

        val minDimension = min(w, h)
        val safeDimension = if (minDimension > 0) minDimension * 1f else 300f
        val opWidthRatio = 0.65f
        val totalUnitsInRow = n + (n * opWidthRatio) + 1.1f
        val cellSizeValue = safeDimension / totalUnitsInRow
        val cellSize = cellSizeValue.dp
        val opWidth = (cellSizeValue * opWidthRatio).dp
        val calculatedFontSize = cellSizeValue * 0.45f
        val fontSize = if (calculatedFontSize.isFinite() && calculatedFontSize > 0) calculatedFontSize.sp else 12.sp

        Box {
            // Light Modda Gölge veriyoruz ki beyaz zemin üzerinde kart ayrılsın.
            // Dark modda gölgeye gerek yok, cam efekti yetiyor.
            val shadowElevation = if (isDark) 0.dp else 12.dp

            GlassBox(
                modifier = Modifier
                    .wrapContentSize()
                    .shadow(shadowElevation, RoundedCornerShape(24.dp)),
                cornerRadius = 24.dp
            ) {
                // Temaya göre kartın iç rengi (Light: Saf Beyaz, Dark: Yarı Saydam Siyah)
                Box(
                    modifier = Modifier
                        .background(colors.cardBackground)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // GameGrid'i MaterialTheme ile sarmalayarak renkleri zorluyoruz.
                        // GameGrid muhtemelen LocalContentColor kullanıyordur.
                        MaterialTheme(
                            colorScheme = MaterialTheme.colorScheme.copy(
                                onSurface = colors.textPrimary, // Rakamlar
                                surface = colors.textPrimary    // Genel içerik
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
                        .background(Color.Black.copy(0.7f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Rounded.Pause,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "PAUSED",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
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

    // 1. TEMA AYARLARI (Dark Mode)
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
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background), // Temadan gelen arka plan rengi
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.gameState != null) {
            GamePlayArea(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .aspectRatio(1f),
                viewModel = viewModel,
                n = 3,
                isTimerRunning = false,
                isSolved = true,
                // YENİ EKLENEN PARAMETRELER:
                colors = colors,
                isDark = isDark
            )
        }
    }
}