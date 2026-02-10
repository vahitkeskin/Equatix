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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.vahitkeskin.equatix.domain.model.AppDictionary
import com.vahitkeskin.equatix.ui.components.TransparentNumpad
import com.vahitkeskin.equatix.ui.game.GameViewModel
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import com.vahitkeskin.equatix.ui.utils.PreviewContainer
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameBottomPanel(
    isSolved: Boolean,
    isSurrendered: Boolean,
    isTimerRunning: Boolean,
    elapsedTime: Long,
    colors: EquatixDesignSystem.ThemeColors,
    appStrings: com.vahitkeskin.equatix.domain.model.AppStrings,
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
                visible = !isSolved && isTimerRunning,
                enter = slideInVertically { it } + fadeIn(),
                exit = fadeOut()
            ) {
                Box {
                    MaterialTheme(
                        colorScheme = MaterialTheme.colorScheme.copy(
                            onSurface = colors.numpadText,
                            primary = colors.numpadText
                        )
                    ) {
                        TransparentNumpad(
                            colors = colors,
                            onInput = onInput
                        )
                    }
                }
            }

            // SONUÇ EKRANI
            AnimatedVisibility(
                visible = isSolved,
                enter = slideInVertically { it } + fadeIn()
            ) {
                ResultPanel(
                    isSurrendered = isSurrendered,
                    elapsedTime = elapsedTime,
                    colors = colors,
                    appStrings = appStrings,
                    onRestart = onRestart
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewGameBottomPanelLight() {
    com.vahitkeskin.equatix.ui.utils.PreviewContainer(isDark = false) { colors, strings ->
        GameBottomPanel(
            isSolved = false,
            isSurrendered = false,
            isTimerRunning = true,
            elapsedTime = 125L,
            colors = colors,
            appStrings = strings,
            onInput = {},
            onRestart = {}
        )
    }
}

@Preview
@Composable
fun PreviewGameBottomPanelDark() {
    com.vahitkeskin.equatix.ui.utils.PreviewContainer(isDark = true) { colors, strings ->
        GameBottomPanel(
            isSolved = true,
            isSurrendered = false,
            isTimerRunning = false,
            elapsedTime = 42L,
            colors = colors,
            appStrings = strings,
            onInput = {},
            onRestart = {}
        )
    }
}