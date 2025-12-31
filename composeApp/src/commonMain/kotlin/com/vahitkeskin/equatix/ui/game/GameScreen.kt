package com.vahitkeskin.equatix.ui.game

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.ui.game.components.*
import com.vahitkeskin.equatix.ui.game.dialogs.HintDialog
import com.vahitkeskin.equatix.ui.game.utils.calculateProgress
import com.vahitkeskin.equatix.ui.game.visuals.CosmicBackground
import com.vahitkeskin.equatix.ui.game.visuals.FireworkOverlay
import com.vahitkeskin.equatix.ui.game.visuals.ProgressPath
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class GameScreen(
    val difficulty: Difficulty,
    val gridSize: GridSize
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { GameViewModel() }

        // Initialize game state only once
        LaunchedEffect(Unit) {
            if (viewModel.gameState == null) viewModel.startGame(difficulty, gridSize)
        }

        val state = viewModel.gameState ?: return

        GameContent(
            viewModel = viewModel,
            navigator = navigator,
            difficulty = difficulty,
            gridSize = gridSize,
            gridN = state.size
        )
    }
}

@Composable
private fun GameContent(
    viewModel: GameViewModel,
    navigator: cafe.adriel.voyager.navigator.Navigator,
    difficulty: Difficulty,
    gridSize: GridSize,
    gridN: Int
) {
    val haptic = LocalHapticFeedback.current

    // UI State'leri
    var elapsedTime by remember { mutableStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var showHintDialog by remember { mutableStateOf(false) }
    var isTimerVisible by remember { mutableStateOf(true) } // Odak Modu
    var isGamePaused by remember { mutableStateOf(false) } // Pause Overlay

    // Çözüldüğünde Titreşim
    LaunchedEffect(viewModel.isSolved) {
        if (viewModel.isSolved && !viewModel.isSurrendered && viewModel.isVibrationEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Progress Animasyonu
    val progress by remember(viewModel.gameState) {
        derivedStateOf { calculateProgress(viewModel.gameState) }
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )

    // Zamanlayıcı Mantığı
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
            .background(Color(0xFF0F172A))
    ) {
        // --- KATMAN 1: Görseller (Arkaplan) ---
        CosmicBackground()
        ProgressPath(progress = animatedProgress)

        // --- KATMAN 2: Ana Oyun Arayüzü ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameHeader(
                difficulty = difficulty,
                gridSize = gridSize,
                isSolved = viewModel.isSolved,
                onBack = { navigator.pop() },
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

            // Revize Edilmiş StatsBar (Odak Modu & Pause Entegrasyonu)
            GameStatsBar(
                elapsedTime = elapsedTime,
                isTimerRunning = isTimerRunning,
                isSolved = viewModel.isSolved,
                isVibrationEnabled = viewModel.isVibrationEnabled,
                isTimerVisible = isTimerVisible,
                onHintClick = { showHintDialog = true },
                onPauseToggle = {
                    isTimerRunning = !isTimerRunning
                    isGamePaused = !isGamePaused
                },
                onVibrationToggle = { viewModel.toggleVibration() },
                onTimerToggle = { isTimerVisible = !isTimerVisible }
            )

            GamePlayArea(
                modifier = Modifier.weight(1f),
                viewModel = viewModel,
                n = gridN,
                isTimerRunning = isTimerRunning,
                isSolved = viewModel.isSolved
            )

            GameBottomPanel(
                viewModel = viewModel,
                isTimerRunning = isTimerRunning,
                elapsedTime = elapsedTime,
                onInput = { viewModel.onInput(it) },
                onRestart = {
                    // Alt paneldeki restart butonu (Hızlı Yeniden Başlat)
                    viewModel.startGame(difficulty, gridSize)
                    elapsedTime = 0
                    isTimerRunning = true
                }
            )
        }

        // --- KATMAN 3: Pause Overlay (En Üstte) ---
        GamePauseOverlay(
            isVisible = isGamePaused,
            onResume = {
                isGamePaused = false
                isTimerRunning = true
            },
            onRestart = {
                // Overlay'den Yeniden Başlatma Mantığı
                isGamePaused = false
                viewModel.startGame(difficulty, gridSize)
                elapsedTime = 0
                isTimerRunning = true
            },
            onQuit = {
                navigator.pop()
            }
        )

        // --- KATMAN 4: Dialoglar ve Efektler ---
        if (showHintDialog) {
            HintDialog(onDismiss = { showHintDialog = false })
        }

        if (viewModel.isSolved && !viewModel.isSurrendered) {
            FireworkOverlay()
        }
    }
}