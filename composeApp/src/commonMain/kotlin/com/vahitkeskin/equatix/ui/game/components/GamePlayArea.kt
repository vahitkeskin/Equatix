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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.min

@Composable
fun GamePlayArea(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel,
    n: Int,
    isTimerRunning: Boolean,
    isSolved: Boolean,
    tutorialState: TutorialState = TutorialState.IDLE
) {
    BoxWithConstraints(
        // Paddingleri kaldırdık, alanı tam kullansın
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Ekran genişliğini tam kullanması için çarpanı 1.0f yaptık (Eskisi 0.95f idi)
        val minDimension = min(maxWidth.value, maxHeight.value)
        val safeDimension = minDimension * 1f

        val opWidthRatio = 0.65f
        val totalUnitsInRow = n + (n * opWidthRatio) + 1.1f
        val cellSizeValue = safeDimension / totalUnitsInRow
        val cellSize = cellSizeValue.dp
        val opWidth = (cellSizeValue * opWidthRatio).dp
        // Fontu da hücre büyümesine oranla hafif büyüttük
        val fontSize = (cellSizeValue * 0.45f).sp

        Box {
            GlassBox(
                modifier = Modifier.wrapContentSize(),
                cornerRadius = 24.dp
            ) {
                Box(
                    // İç padding'i azalttık (16.dp -> 8.dp), böylece grid daha büyük görünür
                    modifier = Modifier.padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
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
            }

            if (!isTimerRunning && !isSolved && tutorialState == TutorialState.IDLE) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(0.85f), RoundedCornerShape(24.dp)),
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

    LaunchedEffect(Unit) {
        val fixedNumbers = listOf(8, 4, 2, 12, 10, 5, 100, 25, 4)
        val customCells = fixedNumbers.mapIndexed { index, value ->
            CellData(id = index, correctValue = value, isHidden = true, userInput = value.toString(), isRevealedBySystem = false, isLocked = false)
        }
        val rowOps = listOf(Operation.ADD, Operation.MUL, Operation.ADD, Operation.DIV, Operation.SUB, Operation.MUL)
        val colOps = listOf(Operation.ADD, Operation.ADD, Operation.MUL, Operation.ADD, Operation.MUL, Operation.ADD)
        val rowResults = listOf(16, 14, 0)
        val colResults = listOf(120, 65, 14)
        val validState = GameState(3, customCells, rowOps, colOps, rowResults, colResults, Difficulty.HARD)
        viewModel.loadPreviewState(validState)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)), contentAlignment = Alignment.Center) {
        if (viewModel.gameState != null) {
            GamePlayArea(
                modifier = Modifier.fillMaxWidth().padding(16.dp).aspectRatio(1f),
                viewModel = viewModel, n = 3, isTimerRunning = false, isSolved = true
            )
        }
    }
}