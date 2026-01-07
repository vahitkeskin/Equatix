package com.vahitkeskin.equatix.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.ui.game.GameScreen
import com.vahitkeskin.equatix.ui.home.components.*
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { HomeViewModel() }

        // State'ler
        var selectedDiff by remember { mutableStateOf(Difficulty.EASY) }
        var selectedSize by remember { mutableStateOf(GridSize.SIZE_3x3) }
        var activeOverlay by remember { mutableStateOf<OverlayType?>(null) }

        var startAnimation by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { startAnimation = true }

        // Tema Hesaplama
        val themeConfig by viewModel.themeConfig.collectAsState()
        val isSystemDark = isSystemInDarkTheme()
        val isDark = when (themeConfig) {
            AppThemeConfig.FOLLOW_SYSTEM -> isSystemDark
            AppThemeConfig.DARK -> true
            AppThemeConfig.LIGHT -> false
        }

        // Merkezi Renk Paletini Getir
        val themeColors = EquatixDesignSystem.getColors(isDark)

        // Arka Plan Animasyonu
        val animatedBgColor by animateColorAsState(
            targetValue = themeColors.background,
            animationSpec = tween(500)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBgColor)
        ) {
            // 1. Arka Plan Katmanı (YENİLENDİ)
            HomeBackgroundLayer(
                isDark = isDark,
                gridColor = themeColors.gridLines,
                bgColor = animatedBgColor
            )

            // 2. Ana İçerik Katmanı
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                AnimatedSlideIn(visible = startAnimation, delay = 0) {
                    HomeHeader(
                        isDark = isDark,
                        colors = themeColors,
                        onHistoryClick = { activeOverlay = OverlayType.HISTORY },
                        onSettingsClick = { activeOverlay = OverlayType.SETTINGS }
                    )
                }

                Spacer(modifier = Modifier.weight(0.1f))

                // Branding
                AnimatedSlideIn(visible = startAnimation, delay = 200) {
                    HomeBranding(isDark = isDark, colors = themeColors)
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Seçim Paneli
                AnimatedSlideIn(visible = startAnimation, delay = 400) {
                    HomeSelectionPanel(
                        viewModel = viewModel,
                        selectedDiff = selectedDiff,
                        onDiffSelect = { selectedDiff = it },
                        selectedSize = selectedSize,
                        onSizeSelect = { selectedSize = it },
                        isDark = isDark,
                        colors = themeColors
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Start Butonu
                AnimatedSlideIn(visible = startAnimation, delay = 600) {
                    CyberStartButton(
                        homeViewModel = viewModel,
                        isDark = isDark,
                        onClick = {
                            navigator.push(
                                GameScreen(
                                    difficulty = selectedDiff,
                                    gridSize = selectedSize,
                                    isDarkTheme = isDark
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            // 3. Overlay Katmanı
            HomeOverlayPanel(
                overlayType = activeOverlay,
                onDismiss = { activeOverlay = null },
                viewModel = viewModel,
                isDark = isDark,
                colors = themeColors
            )
        }
    }

    enum class OverlayType { HISTORY, SETTINGS }
}

// Yardımcı Animasyon Fonksiyonu
@Composable
fun AnimatedSlideIn(visible: Boolean, delay: Int, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { 50 },
            animationSpec = tween(600, delayMillis = delay, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(600, delayMillis = delay)),
        content = { content() }
    )
}