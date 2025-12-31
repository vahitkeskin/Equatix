package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vahitkeskin.equatix.ui.components.TransparentNumpad
import com.vahitkeskin.equatix.ui.game.GameViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameBottomPanel(
    viewModel: GameViewModel,
    isTimerRunning: Boolean,
    elapsedTime: Long,
    onInput: (String) -> Unit,
    onRestart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Alt boşluğu azalttık (Daha kompakt)
            .padding(bottom = 4.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedVisibility(
                visible = !viewModel.isSolved && isTimerRunning,
                enter = slideInVertically { it } + fadeIn(),
                exit = fadeOut()
            ) {
                // Numpad'i hafifçe küçülterek (%90) üstteki oyun alanına yer açıyoruz
                Box(modifier = Modifier.scale(0.9f)) {
                    TransparentNumpad(onInput = onInput)
                }
            }

            AnimatedVisibility(
                visible = viewModel.isSolved,
                enter = slideInVertically { it } + fadeIn()
            ) {
                ResultPanel(
                    isSurrendered = viewModel.isSurrendered,
                    elapsedTime = elapsedTime,
                    onRestart = onRestart,
                    onGiveUp = { }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewGameBottomPanel() {
    val viewModel = androidx.compose.runtime.remember { GameViewModel() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        GameBottomPanel(
            viewModel = viewModel,
            isTimerRunning = true,
            elapsedTime = 125L,
            onInput = {},
            onRestart = {}
        )
    }
}