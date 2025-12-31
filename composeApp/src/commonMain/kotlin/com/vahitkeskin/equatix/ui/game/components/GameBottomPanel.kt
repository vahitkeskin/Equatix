package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vahitkeskin.equatix.ui.components.TransparentNumpad
import com.vahitkeskin.equatix.ui.game.GameViewModel

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
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedVisibility(
                visible = !viewModel.isSolved && isTimerRunning,
                enter = slideInVertically { it } + fadeIn(),
                exit = fadeOut()
            ) {
                TransparentNumpad(onInput = onInput)
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