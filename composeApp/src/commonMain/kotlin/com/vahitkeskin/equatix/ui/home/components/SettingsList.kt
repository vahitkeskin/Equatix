package com.vahitkeskin.equatix.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.MusicOff
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import com.vahitkeskin.equatix.platform.getAppVersion
import com.vahitkeskin.equatix.ui.common.AnimatedSegmentedControl
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import com.vahitkeskin.equatix.ui.utils.rememberNotificationPermissionControl

@Composable
fun SettingsList(
    viewModel: HomeViewModel,
    isDark: Boolean,
    colors: EquatixDesignSystem.ThemeColors
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val isSoundOn by viewModel.isSoundOn.collectAsState()
    val isVibrationOn by viewModel.isVibrationOn.collectAsState()
    val themeConfig by viewModel.themeConfig.collectAsState()
    val isNotificationEnabled by viewModel.isNotificationEnabled.collectAsState()
    val notificationTime by viewModel.notificationTime.collectAsState()
    val isMusicOn by viewModel.isMusicOn.collectAsState()

    val strings by viewModel.strings.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshPermissionStatus()
        }
    }

    val permissionControl = rememberNotificationPermissionControl(
        onPermissionResult = { isGranted ->
            if (isGranted) viewModel.setNotificationSchedule(true)
            else viewModel.openAppSettings()
        }
    )

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {

        // --- YENİ DİL SEÇİMİ (Carousel) ---
        LanguageSelectionCarousel(
            currentLanguage = currentLanguage,
            onLanguageSelected = { viewModel.setLanguage(it) },
            colors = colors
        )

        HorizontalDivider(color = colors.divider)

        // --- GÖRÜNÜM ---
        SectionTitle(strings.appearance, colors)

        AnimatedSegmentedControl(
            items = AppThemeConfig.values().toList(),
            selectedItem = themeConfig,
            onItemSelected = { viewModel.setTheme(it) },
            modifier = Modifier.fillMaxWidth().height(48.dp).background(Color.Transparent),
            itemLabel = {
                when (it) {
                    AppThemeConfig.FOLLOW_SYSTEM -> strings.system
                    AppThemeConfig.LIGHT -> strings.light
                    AppThemeConfig.DARK -> strings.dark
                }
            }
        )

        HorizontalDivider(color = colors.divider)

        // --- TERCİHLER ---
        SectionTitle(strings.preferences, colors)

        SettingItem(
            title = strings.backgroundMusic,
            subtitle = strings.musicSubtitle,
            icon = if (isMusicOn) Icons.Rounded.MusicNote else Icons.Rounded.MusicOff,
            isOn = isMusicOn,
            isDark = isDark,
            colors = colors,
            onToggle = { viewModel.toggleMusic(it) }
        )

        SettingItem(
            title = strings.vibration,
            icon = if (isVibrationOn) Icons.Rounded.Vibration else Icons.Rounded.Smartphone,
            isOn = isVibrationOn,
            isDark = isDark,
            colors = colors,
            onToggle = { viewModel.toggleVibration() }
        )

        val timeString = "${
            notificationTime.first.toString().padStart(2, '0')
        }:${notificationTime.second.toString().padStart(2, '0')}"

        val subtitleText = if (isNotificationEnabled)
            "${strings.reminderOnPrefix} $timeString"
        else
            strings.reminderOff

        SettingItem(
            title = strings.dailyReminder,
            subtitle = subtitleText,
            icon = if (isNotificationEnabled) Icons.Rounded.Notifications else Icons.Rounded.NotificationsOff,
            isOn = isNotificationEnabled,
            isDark = isDark,
            colors = colors,
            onToggle = { shouldEnable ->
                if (shouldEnable) {
                    if (permissionControl.hasPermission()) viewModel.setNotificationSchedule(true)
                    else permissionControl.launchPermissionRequest()
                } else {
                    viewModel.setNotificationSchedule(false)
                }
            }
        )

        Text(
            text = "EQUATIX v${getAppVersion()}", // Sürüm
            color = colors.textSecondary.copy(0.5f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SectionTitle(text: String, colors: EquatixDesignSystem.ThemeColors) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = colors.accent,
        letterSpacing = 1.5.sp
    )
}