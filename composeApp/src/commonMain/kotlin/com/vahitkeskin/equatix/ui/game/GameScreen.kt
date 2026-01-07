package com.vahitkeskin.equatix.ui.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.vahitkeskin.equatix.domain.model.CellData
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GameState
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.domain.model.Operation
import com.vahitkeskin.equatix.ui.game.components.GameBottomPanel
import com.vahitkeskin.equatix.ui.game.components.GameHeader
import com.vahitkeskin.equatix.ui.game.components.GamePauseOverlay
import com.vahitkeskin.equatix.ui.game.components.GamePlayArea
import com.vahitkeskin.equatix.ui.game.components.GameStatsBar
import com.vahitkeskin.equatix.ui.game.components.tutorial.TutorialState
import com.vahitkeskin.equatix.ui.game.dialogs.HintDialog
import com.vahitkeskin.equatix.ui.game.utils.GameUiEvent
import com.vahitkeskin.equatix.ui.game.utils.calculateProgress
import com.vahitkeskin.equatix.ui.game.visuals.CosmicBackground
import com.vahitkeskin.equatix.ui.game.visuals.FireworkOverlay
import com.vahitkeskin.equatix.ui.game.visuals.ProgressPath
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.ui.tooling.preview.Preview

data class GameScreen(
    val difficulty: Difficulty,
    val gridSize: GridSize,
    val isDarkTheme: Boolean
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { GameViewModel() }
        val homeViewModel = rememberScreenModel { HomeViewModel() }

        LaunchedEffect(Unit) {
            if (viewModel.gameState == null) viewModel.startGame(difficulty, gridSize)
        }

        val state = viewModel.gameState ?: return

        GameContent(
            viewModel = viewModel,
            homeViewModel = homeViewModel,
            onNavigateBack = { navigator.pop() },
            difficulty = difficulty,
            gridSize = gridSize,
            gridN = state.size,
            isDarkTheme = isDarkTheme // <--- İçeri iletiyoruz
        )
    }
}

@Composable
private fun GameContent(
    viewModel: GameViewModel,
    homeViewModel: HomeViewModel,
    onNavigateBack: () -> Unit,
    difficulty: Difficulty,
    gridSize: GridSize,
    gridN: Int,
    isDarkTheme: Boolean
) {
    val haptic = LocalHapticFeedback.current
    val appSettings by homeViewModel.strings.collectAsState()

    var elapsedTime by remember { mutableStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var showHintDialog by remember { mutableStateOf(false) }
    var isTimerVisible by remember { mutableStateOf(true) }
    var isGamePaused by remember { mutableStateOf(false) }
    var tutorialState by remember { mutableStateOf(TutorialState.IDLE) }

    val colors = remember(isDarkTheme) { EquatixDesignSystem.getColors(isDarkTheme) }

    // --- 1. EVENT LISTENER (Hata Titreşimi İçin) ---
    // ViewModel'den gelen 'VibrateError' olayını dinler.
    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is GameUiEvent.VibrateError -> {
                    // Hatalı tuşlamada titreşim (Hata hissi için LongPress uygun)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
        }
    }

    // Tutorial Logic
    val isTutorialSeen by viewModel.isTutorialSeen.collectAsState()
    LaunchedEffect(isTutorialSeen) {
        if (!isTutorialSeen && !viewModel.isSolved) {
            delay(800)
            tutorialState = TutorialState.ROW_ANIMATION
            delay(2500)
            tutorialState = TutorialState.COLUMN_ANIMATION
            delay(2500)
            tutorialState = TutorialState.IDLE
            viewModel.markTutorialAsSeen()
        }
    }

    // Oyun Bittiğinde Kazanma Titreşimi
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

    // Timer Logic
    LaunchedEffect(isTimerRunning, viewModel.isSolved) {
        if (viewModel.isSolved) isTimerRunning = false
        while (isTimerRunning && !viewModel.isSolved && isActive) {
            delay(1000L)
            elapsedTime++
        }
    }

    // Oyun bittiğinde skoru hesaplamak için ViewModel'i tetikle
    LaunchedEffect(viewModel.isSolved) {
        if (viewModel.isSolved && !viewModel.isSurrendered) {
            viewModel.onGameFinished(elapsedTime)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Arkaplan
        val starColor = if (isDarkTheme) Color.White else colors.textPrimary
        val starAlpha = if (isDarkTheme) 1f else 0.2f

        CosmicBackground(
            modifier = Modifier.matchParentSize(),
            starColor = starColor.copy(alpha = starAlpha)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HEADER
            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                GameHeader(
                    difficulty = difficulty,
                    gridSize = gridSize,
                    isSolved = viewModel.isSolved,
                    colors = colors,
                    appStrings = appSettings,
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
            }

            // STATS
            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                GameStatsBar(
                    elapsedTime = elapsedTime,
                    isTimerRunning = isTimerRunning,
                    isSolved = viewModel.isSolved,
                    isTimerVisible = isTimerVisible,
                    colors = colors,
                    isDark = isDarkTheme,
                    onPauseToggle = {
                        isTimerRunning = !isTimerRunning
                        isGamePaused = !isGamePaused
                    },
                    onTimerToggle = { isTimerVisible = !isTimerVisible }
                )
            }

            // PROGRESS
            ProgressPath(
                progress = animatedProgress,
                colors = colors
            )

            // GRID
            GamePlayArea(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                viewModel = viewModel,
                n = gridN,
                tutorialState = tutorialState,
                colors = colors,
                isDark = isDarkTheme
            )

            // NUMPAD
            GameBottomPanel(
                viewModel = viewModel,
                isTimerRunning = isTimerRunning,
                elapsedTime = elapsedTime,
                colors = colors,
                onInput = { key ->
                    // Input mantığı artık tamamen ViewModel içinde yönetiliyor
                    viewModel.onInput(key)
                },
                homeViewModel = homeViewModel,
                onRestart = {
                    viewModel.startGame(difficulty, gridSize)
                    elapsedTime = 0
                    isTimerRunning = true
                }
            )
        }

        // --- OVERLAYS ---

        GamePauseOverlay(
            isVisible = isGamePaused,
            colors = colors,
            isDark = isDarkTheme,
            appStrings = appSettings,
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

    LaunchedEffect(viewModel.isSolved) {
        if (viewModel.isSolved && !viewModel.isSurrendered) {
            viewModel.onGameFinished(elapsedTime)
            if (viewModel.isVibrationEnabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }
}

@Preview
@Composable
fun PreviewGameContentMaximized() {
    val viewModel = remember { GameViewModel() }
    val isDark = true
    val darkColors = EquatixDesignSystem.getColors(isDark)

    LaunchedEffect(Unit) {
        // Preview Setup
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
            homeViewModel = HomeViewModel(),
            onNavigateBack = {},
            difficulty = Difficulty.EASY,
            gridSize = GridSize.SIZE_3x3,
            gridN = 3,
            isDarkTheme = isDark
        )
    } else {
        Box(modifier = Modifier.fillMaxSize().background(darkColors.background))
    }
}