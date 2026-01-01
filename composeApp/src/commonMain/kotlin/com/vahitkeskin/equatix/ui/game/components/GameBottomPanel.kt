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
            .padding(bottom = 4.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // OYUN KLAVYESİ (Numpad)
            AnimatedVisibility(
                visible = !viewModel.isSolved && isTimerRunning,
                enter = slideInVertically { it } + fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.scale(0.9f)) {
                    // SORUN ÇÖZÜCÜ:
                    // TransparentNumpad muhtemelen MaterialTheme.colorScheme.onSurface veya primary kullanıyor.
                    // Biz burada o renkleri EquatixDesignSystem'den gelen 'numpadText' ile eziyoruz.
                    MaterialTheme(
                        colorScheme = MaterialTheme.colorScheme.copy(
                            onSurface = colors.numpadText, // Metin Rengi (Dark'ta Beyaz, Light'ta Lacivert)
                            primary = colors.numpadText    // Varsa ikon/border rengi
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
                    colors = colors, // Renkleri iletiyoruz
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
    // 1. Mock ViewModel
    val viewModel = androidx.compose.runtime.remember { GameViewModel() }

    // 2. Tema Renklerini Al (Dark Mode Örneği)
    val isDark = true
    val colors = EquatixDesignSystem.getColors(isDark)

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Arka planı hardcoded (0xFF0F172A) yerine temadan alıyoruz
            .background(colors.background)
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        GameBottomPanel(
            viewModel = viewModel,
            isTimerRunning = true,
            elapsedTime = 125L,
            colors = colors, // <--- YENİ EKLENEN PARAMETRE
            onInput = {},
            onRestart = {}
        )
    }
}