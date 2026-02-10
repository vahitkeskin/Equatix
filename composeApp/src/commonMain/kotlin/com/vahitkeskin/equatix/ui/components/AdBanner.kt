package com.vahitkeskin.equatix.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cafe.adriel.voyager.core.model.rememberScreenModel
import com.vahitkeskin.equatix.domain.model.AppStrings
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import com.vahitkeskin.equatix.platform.AdBlockerManager
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

@Composable
fun AdBanner(
    strings: AppStrings,
    colors: EquatixDesignSystem.ThemeColors,
    modifier: Modifier = Modifier
) {
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
