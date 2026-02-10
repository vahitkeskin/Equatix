package com.vahitkeskin.equatix.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.ui.game.GameScreen
import com.vahitkeskin.equatix.ui.home.components.CyberStartButton
import com.vahitkeskin.equatix.ui.home.components.HomeBackgroundLayer
import com.vahitkeskin.equatix.ui.home.components.HomeBranding
import com.vahitkeskin.equatix.ui.home.components.HomeHeader
import com.vahitkeskin.equatix.ui.home.components.HomeOverlayPanel
import com.vahitkeskin.equatix.ui.home.components.HomeSelectionPanel
import com.vahitkeskin.equatix.ui.components.AdBanner
import com.vahitkeskin.equatix.ui.utils.systemNavBarPadding
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

import com.vahitkeskin.equatix.platform.BackHandler

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { HomeViewModel() }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    // Uygulamaya dönüldüğünde: Ayarı kontrol et, açıksa çal.
                    viewModel.checkMusicOnResume()
                } else if (event == Lifecycle.Event.ON_PAUSE) {
                    // Uygulama alta atıldığında: Müziği durdur.
                    viewModel.pauseMusicOnBackground()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        // State'ler
        var selectedDiff by remember { mutableStateOf(Difficulty.EASY) }
        var selectedSize by remember { mutableStateOf(GridSize.SIZE_3x3) }
        var activeOverlay by remember { mutableStateOf<OverlayType?>(null) }

        var startAnimation by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { startAnimation = true }

        // Geri butonu kontrolü: Eğer bir overlay (Skorlar/Ayarlar) açıksa önce onu kapat.
        BackHandler(enabled = activeOverlay != null) {
            activeOverlay = null
        }

        // Tema Hesaplama
        val themeConfig by viewModel.themeConfig.collectAsState()
        val strings by viewModel.strings.collectAsState()
        val isSystemDark = isSystemInDarkTheme()
        val isDark = when (themeConfig) {
            AppThemeConfig.FOLLOW_SYSTEM -> isSystemDark
            AppThemeConfig.DARK -> true
            AppThemeConfig.LIGHT -> false
        }

        val themeColors = EquatixDesignSystem.getColors(isDark)

        val animatedBgColor by animateColorAsState(
            targetValue = themeColors.background,
            animationSpec = tween(500)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBgColor)
        ) {
            HomeBackgroundLayer(
                isDark = isDark,
                gridColor = themeColors.gridLines,
                bgColor = animatedBgColor
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedSlideIn(visible = startAnimation, delay = 0) {
                    HomeHeader(
                        isDark = isDark,
                        colors = themeColors,
                        onHistoryClick = { activeOverlay = OverlayType.HISTORY },
                        onSettingsClick = { activeOverlay = OverlayType.SETTINGS }
                    )
                }

                Spacer(modifier = Modifier.weight(0.1f))

                AnimatedSlideIn(visible = startAnimation, delay = 200) {
                    HomeBranding(isDark = isDark, colors = themeColors)
                }

                Spacer(modifier = Modifier.height(48.dp))

                val strings by viewModel.strings.collectAsState()

                AnimatedSlideIn(visible = startAnimation, delay = 400) {
                    HomeSelectionPanel(
                        selectedDiff = selectedDiff,
                        onDiffSelect = { selectedDiff = it },
                        selectedSize = selectedSize,
                        onSizeSelect = { selectedSize = it },
                        isDark = isDark,
                        colors = themeColors,
                        appStrings = strings
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedSlideIn(visible = startAnimation, delay = 600) {
                    CyberStartButton(
                        isDark = isDark,
                        appStrings = strings,
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

            AdBanner(
                strings = strings,
                colors = themeColors,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .systemNavBarPadding()
            )

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