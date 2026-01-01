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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.vahitkeskin.equatix.ui.components.TransparentNumpad
import com.vahitkeskin.equatix.ui.game.GameViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameBottomPanel(
    viewModel: GameViewModel,
    isTimerRunning: Boolean,
    elapsedTime: Long,
    colors: EquatixDesignSystem.ThemeColors,
    onInput: (String) -> Unit,
    onRestart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // OYUN KLAVYESİ (Numpad)
            AnimatedVisibility(
                visible = !viewModel.isSolved && isTimerRunning,
                enter = slideInVertically { it } + fadeIn(),
                exit = fadeOut()
            ) {
                // DÜZELTME: Scale 0.9f -> 0.85f
                // Tuş takımını biraz daha küçülterek yukarıdaki Grid'e daha çok yer bıraktık.
                Box(modifier = Modifier.scale(0.85f)) {
                    MaterialTheme(
                        colorScheme = MaterialTheme.colorScheme.copy(
                            onSurface = colors.numpadText,
                            primary = colors.numpadText
                        )
                    ) {
                        TransparentNumpad(onInput = onInput)
                    }
                }
            }

            // SONUÇ EKRANI
            AnimatedVisibility(
                visible = viewModel.isSolved,
                enter = slideInVertically { it } + fadeIn()
            ) {
                ResultPanel(
                    isSurrendered = viewModel.isSurrendered,
                    elapsedTime = elapsedTime,
                    colors = colors,
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
    val darkColors = EquatixDesignSystem.getColors(true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkColors.background)
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        GameBottomPanel(
            viewModel = viewModel,
            isTimerRunning = true,
            elapsedTime = 125L,
            colors = darkColors,
            onInput = {},
            onRestart = {}
        )
    }
}