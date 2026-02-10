package com.vahitkeskin.equatix.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.vahitkeskin.equatix.domain.model.AppLanguage
import com.vahitkeskin.equatix.platform.getAppVersion
import com.vahitkeskin.equatix.ui.common.AnimatedSegmentedControl
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import com.vahitkeskin.equatix.ui.utils.PreviewContainer
import com.vahitkeskin.equatix.ui.utils.rememberNotificationPermissionControl

@Composable
fun SettingsList(
    isSoundOn: Boolean,
    isVibrationOn: Boolean,
    themeConfig: AppThemeConfig,
    isNotificationEnabled: Boolean,
    notificationTime: Pair<Int, Int>,
    isMusicOn: Boolean,
    currentLanguage: com.vahitkeskin.equatix.domain.model.AppLanguage,
    onSoundToggle: (Boolean) -> Unit,
    onVibrationToggle: () -> Unit,
    onThemeSelect: (AppThemeConfig) -> Unit,
    onNotificationToggle: (Boolean) -> Unit,
    onMusicToggle: (Boolean) -> Unit,
    onLanguageSelect: (com.vahitkeskin.equatix.domain.model.AppLanguage) -> Unit,
    onRefreshPermission: () -> Unit,
    onOpenAppSettings: () -> Unit,
    isDark: Boolean,
    colors: EquatixDesignSystem.ThemeColors,
    appStrings: com.vahitkeskin.equatix.domain.model.AppStrings
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            onRefreshPermission()
        }
    }

    val permissionControl = com.vahitkeskin.equatix.ui.utils.rememberNotificationPermissionControl(
        onPermissionResult = { isGranted ->
            if (isGranted) onNotificationToggle(true)
            else onOpenAppSettings()
        }
    )

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        LanguageSelectionCarousel(
            currentLanguage = currentLanguage,
            onLanguageSelected = onLanguageSelect,
            colors = colors,
            appStrings = appStrings
        )

        HorizontalDivider(color = colors.divider)

        SectionTitle(appStrings.appearance, colors)

        AnimatedSegmentedControl(
            items = AppThemeConfig.values().toList(),
            selectedItem = themeConfig,
            onItemSelected = onThemeSelect,
            modifier = Modifier.fillMaxWidth().height(48.dp).background(Color.Transparent),
            itemLabel = {
                when (it) {
                    AppThemeConfig.FOLLOW_SYSTEM -> appStrings.system
                    AppThemeConfig.LIGHT -> appStrings.light
                    AppThemeConfig.DARK -> appStrings.dark
                }
            }
        )

        HorizontalDivider(color = colors.divider)

        SectionTitle(appStrings.preferences, colors)

        SettingItem(
            title = appStrings.backgroundMusic,
            subtitle = appStrings.musicSubtitle,
            icon = if (isMusicOn) Icons.Rounded.MusicNote else Icons.Rounded.MusicOff,
            isOn = isMusicOn,
            isDark = isDark,
            colors = colors,
            onToggle = onMusicToggle
        )

        SettingItem(
            title = appStrings.vibration,
            icon = if (isVibrationOn) Icons.Rounded.Vibration else Icons.Rounded.Smartphone,
            isOn = isVibrationOn,
            isDark = isDark,
            colors = colors,
            onToggle = { onVibrationToggle() }
        )

        val timeString = "${
            notificationTime.first.toString().padStart(2, '0')
        }:${notificationTime.second.toString().padStart(2, '0')}"

        val subtitleText = if (isNotificationEnabled)
            "${appStrings.reminderOnPrefix} $timeString"
        else
            appStrings.reminderOff

        SettingItem(
            title = appStrings.dailyReminder,
            subtitle = subtitleText,
            icon = if (isNotificationEnabled) Icons.Rounded.Notifications else Icons.Rounded.NotificationsOff,
            isOn = isNotificationEnabled,
            isDark = isDark,
            colors = colors,
            onToggle = { shouldEnable ->
                if (shouldEnable) {
                    if (permissionControl.hasPermission()) onNotificationToggle(true)
                    else permissionControl.launchPermissionRequest()
                } else {
                    onNotificationToggle(false)
                }
            }
        )

        Text(
            text = "EQUATIX v${com.vahitkeskin.equatix.platform.getAppVersion()}",
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

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun PreviewSettingsList() {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        com.vahitkeskin.equatix.ui.utils.PreviewContainer(isDark = true) { colors, strings ->
            SettingsList(
                isSoundOn = true,
                isVibrationOn = true,
                themeConfig = AppThemeConfig.DARK,
                isNotificationEnabled = true,
                notificationTime = Pair(20, 30),
                isMusicOn = true,
                currentLanguage = com.vahitkeskin.equatix.domain.model.AppLanguage.TURKISH,
                onSoundToggle = {},
                onVibrationToggle = {},
                onThemeSelect = {},
                onNotificationToggle = {},
                onMusicToggle = {},
                onLanguageSelect = {},
                onRefreshPermission = {},
                onOpenAppSettings = {},
                isDark = true,
                colors = colors,
                appStrings = strings
            )
        }
        
        com.vahitkeskin.equatix.ui.utils.PreviewContainer(isDark = false) { colors, strings ->
            SettingsList(
                isSoundOn = false,
                isVibrationOn = false,
                themeConfig = AppThemeConfig.LIGHT,
                isNotificationEnabled = false,
                notificationTime = Pair(9, 0),
                isMusicOn = false,
                currentLanguage = com.vahitkeskin.equatix.domain.model.AppLanguage.ENGLISH,
                onSoundToggle = {},
                onVibrationToggle = {},
                onThemeSelect = {},
                onNotificationToggle = {},
                onMusicToggle = {},
                onLanguageSelect = {},
                onRefreshPermission = {},
                onOpenAppSettings = {},
                isDark = false,
                colors = colors,
                appStrings = strings
            )
        }
    }
}