package com.vahitkeskin.equatix.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.vahitkeskin.equatix.platform.AdBlockerManager
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import cafe.adriel.voyager.core.model.rememberScreenModel
import androidx.compose.foundation.isSystemInDarkTheme
import com.vahitkeskin.equatix.domain.model.AppThemeConfig

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    val homeViewModel = rememberScreenModel { HomeViewModel() }
    val strings by homeViewModel.strings.collectAsState()
    val themeConfig by homeViewModel.themeConfig.collectAsState()
    
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeConfig) {
        AppThemeConfig.FOLLOW_SYSTEM -> isSystemDark
        AppThemeConfig.DARK -> true
        AppThemeConfig.LIGHT -> false
    }
    val colors = EquatixDesignSystem.getColors(isDark)

    var isDnsActive by remember { mutableStateOf(AdBlockerManager.isPrivateDnsActive()) }

    // Re-check DNS status when app resumes
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isDnsActive = AdBlockerManager.isPrivateDnsActive()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (isDnsActive) {
        DnsBannerWarning(
            strings = strings,
            colors = colors,
            modifier = modifier
        )
    } else {
        InternalAdBanner(modifier)
    }
}

@Composable
expect fun InternalAdBanner(modifier: Modifier = Modifier)
