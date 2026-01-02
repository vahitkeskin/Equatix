package com.vahitkeskin.equatix.ui.game

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.font.FontWeight

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

    var elapsedTime by remember { mutableStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var showHintDialog by remember { mutableStateOf(false) }
    var isTimerVisible by remember { mutableStateOf(true) }
    var isGamePaused by remember { mutableStateOf(false) }
    var tutorialState by remember { mutableStateOf(TutorialState.IDLE) }

    val themeConfig by viewModel.themeConfig.collectAsState()
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeConfig) {
        AppThemeConfig.FOLLOW_SYSTEM -> isSystemDark
        AppThemeConfig.DARK -> true
        AppThemeConfig.LIGHT -> false
    }
    val colors = EquatixDesignSystem.getColors(isDark)

    // Tutorial
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
            .background(colors.background)
    ) {
        val starColor = if (isDark) Color.White else Color(0xFF0F172A)
        val starAlpha = if (isDark) 1f else 0.4f
        CosmicBackground(
            modifier = Modifier.matchParentSize(),
            starColor = starColor.copy(alpha = starAlpha)
        )

        // ANA YERLEŞİM KOLONU
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 0.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. HEADER
            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                GameHeader(
                    difficulty = difficulty,
                    gridSize = gridSize,
                    isSolved = viewModel.isSolved,
                    colors = colors,
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

            // 2. STATS BAR (Simetrik)
            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                GameStatsBar(
                    elapsedTime = elapsedTime,
                    isTimerRunning = isTimerRunning,
                    isSolved = viewModel.isSolved,
                    isTimerVisible = isTimerVisible,
                    colors = colors,
                    isDark = isDark,
                    onPauseToggle = {
                        isTimerRunning = !isTimerRunning
                        isGamePaused = !isGamePaused
                    },
                    onTimerToggle = { isTimerVisible = !isTimerVisible }
                )
            }

            // 3. PROGRESS
            ProgressPath(progress = animatedProgress, primaryColor = colors.success)

            // 4. GRID (ASLAN PAYI - weight 1f)
            // Bu alan ekranın kalan tüm dikey boşluğunu kullanacak.
            GamePlayArea(
                modifier = Modifier
                    .weight(1f) // Kritik!
                    .fillMaxWidth(),
                viewModel = viewModel,
                n = gridN,
                isTimerRunning = isTimerRunning,
                isSolved = viewModel.isSolved,
                tutorialState = tutorialState,
                colors = colors,
                isDark = isDark
            )

            // 5. NUMPAD (Sıkıştırılmış)
            GameBottomPanel(
                viewModel = viewModel,
                isTimerRunning = isTimerRunning,
                elapsedTime = elapsedTime,
                colors = colors,
                onInput = { viewModel.onInput(it) },
                onRestart = {
                    viewModel.startGame(difficulty, gridSize)
                    elapsedTime = 0
                    isTimerRunning = true
                }
            )
        }

        // Overlayler
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

    // Oyun bittiğini (isSolved) yakaladığımız bir LaunchedEffect var ya, oraya ekleme yapalım:
    LaunchedEffect(viewModel.isSolved) {
        if (viewModel.isSolved && !viewModel.isSurrendered) {

            // YENİ: Süreyi ve bitişi ViewModel'e bildirip kaydettiriyoruz
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
    val darkColors = EquatixDesignSystem.getColors(true)

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
        Box(modifier = Modifier.fillMaxSize().background(darkColors.background))
    }
}