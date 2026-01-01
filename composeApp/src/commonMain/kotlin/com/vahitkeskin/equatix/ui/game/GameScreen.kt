package com.vahitkeskin.equatix.ui.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import com.vahitkeskin.equatix.domain.model.CellData
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GameState
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.domain.model.Operation
import com.vahitkeskin.equatix.ui.game.components.*
import com.vahitkeskin.equatix.ui.game.components.tutorial.TutorialState
import com.vahitkeskin.equatix.ui.game.dialogs.HintDialog
import com.vahitkeskin.equatix.ui.game.utils.calculateProgress
import com.vahitkeskin.equatix.ui.game.visuals.CosmicBackground
import com.vahitkeskin.equatix.ui.game.visuals.FireworkOverlay
import com.vahitkeskin.equatix.ui.game.visuals.ProgressPath
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.ui.tooling.preview.Preview

data class GameScreen(
    val difficulty: Difficulty,
    val gridSize: GridSize
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { GameViewModel() }

        LaunchedEffect(Unit) {
            if (viewModel.gameState == null) viewModel.startGame(difficulty, gridSize)
        }

        val state = viewModel.gameState ?: return

        GameContent(
            viewModel = viewModel,
            onNavigateBack = { navigator.pop() },
            difficulty = difficulty,
            gridSize = gridSize,
            gridN = state.size
        )
    }
}

@Composable
private fun GameContent(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    difficulty: Difficulty,
    gridSize: GridSize,
    gridN: Int
) {
    val haptic = LocalHapticFeedback.current

    // --- UI State ---
    var elapsedTime by remember { mutableStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var showHintDialog by remember { mutableStateOf(false) }
    var isTimerVisible by remember { mutableStateOf(true) }
    var isGamePaused by remember { mutableStateOf(false) }
    var tutorialState by remember { mutableStateOf(TutorialState.IDLE) }

    // --- TEMA MANTIĞI (EquatixDesignSystem) ---
    val themeConfig by viewModel.themeConfig.collectAsState()
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeConfig) {
        AppThemeConfig.FOLLOW_SYSTEM -> isSystemDark
        AppThemeConfig.DARK -> true
        AppThemeConfig.LIGHT -> false
    }
    val colors = EquatixDesignSystem.getColors(isDark)

    // Arka plan rengi animasyonu
    val animatedBgColor by animateColorAsState(targetValue = colors.background, animationSpec = tween(500))

    // --- Tutorial ---
    LaunchedEffect(Unit) {
        delay(800)
        tutorialState = TutorialState.ROW_ANIMATION
        delay(2500)
        tutorialState = TutorialState.COLUMN_ANIMATION
        delay(2500)
        tutorialState = TutorialState.IDLE
    }

    LaunchedEffect(viewModel.isSolved) {
        if (viewModel.isSolved && !viewModel.isSurrendered && viewModel.isVibrationEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val progress by remember(viewModel.gameState) {
        derivedStateOf { calculateProgress(viewModel.gameState) }
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(isTimerRunning, viewModel.isSolved) {
        if (viewModel.isSolved) isTimerRunning = false
        while (isTimerRunning && !viewModel.isSolved && isActive) {
            delay(1000L)
            elapsedTime++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBgColor)
    ) {
        // --- 1. KATMAN: Arkaplan ---
        // Light modda yıldızları koyu (Slate900), Dark modda beyaz yap
        val starColor = if (isDark) Color.White else Color(0xFF0F172A)
        val starAlpha = if (isDark) 1f else 0.4f // Light modda yıldızları silikleştir

        // Yıldızlar
        CosmicBackground(
            modifier = Modifier.matchParentSize(),
            starColor = starColor.copy(alpha = starAlpha)
        )

        // --- 2. KATMAN: İçerik ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameHeader(
                difficulty = difficulty,
                gridSize = gridSize,
                isSolved = viewModel.isSolved,
                colors = colors, // Tema renkleri
                onBack = onNavigateBack,
                onAutoSolve = {
                    isTimerRunning = false
                    viewModel.giveUpAndSolve()
                },
                onRefresh = {
                    viewModel.startGame(difficulty, gridSize)
                    elapsedTime = 0
                    isTimerRunning = true
                }
            )

            GameStatsBar(
                elapsedTime = elapsedTime,
                isTimerRunning = isTimerRunning,
                isSolved = viewModel.isSolved,
                isVibrationEnabled = viewModel.isVibrationEnabled,
                isTimerVisible = isTimerVisible,
                colors = colors, // Tema renkleri
                onHintClick = { showHintDialog = true },
                onPauseToggle = {
                    isTimerRunning = !isTimerRunning
                    isGamePaused = !isGamePaused
                },
                onVibrationToggle = { viewModel.toggleVibration() },
                onTimerToggle = { isTimerVisible = !isTimerVisible }
            )

            // Progress Path
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 0.dp)
            ) {
                // Progress path rengini de temaya göre ayarlayabiliriz
                ProgressPath(progress = animatedProgress)
            }

            // OYUN ALANI
            GamePlayArea(
                modifier = Modifier.weight(1f),
                viewModel = viewModel,
                n = gridN,
                isTimerRunning = isTimerRunning,
                isSolved = viewModel.isSolved,
                tutorialState = tutorialState,
                colors = colors, // Tema renkleri
                isDark = isDark
            )

            GameBottomPanel(
                viewModel = viewModel,
                isTimerRunning = isTimerRunning,
                elapsedTime = elapsedTime,
                colors = colors, // Tema renkleri
                onInput = { viewModel.onInput(it) },
                onRestart = {
                    viewModel.startGame(difficulty, gridSize)
                    elapsedTime = 0
                    isTimerRunning = true
                }
            )
        }

        // --- 3. KATMAN: Overlayler ---

        GamePauseOverlay(
            isVisible = isGamePaused,
            colors = colors,
            isDark = isDark,
            onResume = {
                isGamePaused = false
                isTimerRunning = true
            },
            onRestart = {
                isGamePaused = false
                viewModel.startGame(difficulty, gridSize)
                elapsedTime = 0
                isTimerRunning = true
            },
            onQuit = onNavigateBack
        )

        if (showHintDialog) {
            HintDialog(onDismiss = { showHintDialog = false })
        }

        if (viewModel.isSolved && !viewModel.isSurrendered) {
            FireworkOverlay()
        }
    }
}

@Preview
@Composable
fun PreviewGameContentMaximized() {
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
        val validState = GameState(3, customCells, rowOps, colOps, rowResults, colResults, Difficulty.EASY)
        viewModel.loadPreviewState(validState)
    }

    if (viewModel.gameState != null) {
        GameContent(
            viewModel = viewModel,
            onNavigateBack = {},
            difficulty = Difficulty.EASY,
            gridSize = GridSize.SIZE_3x3,
            gridN = 3
        )
    } else {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)))
    }
}