package com.vahitkeskin.equatix.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vahitkeskin.equatix.domain.model.AppDictionary
import com.vahitkeskin.equatix.domain.model.AppLanguage
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
annotation class EquatixMultiPreview

/**
 * A container meant to be used in @Preview functions to provide
 * the necessary theme and language context.
 */
@Composable
fun PreviewContainer(
    isDark: Boolean = true,
    language: AppLanguage = AppLanguage.ENGLISH,
    content: @Composable (colors: EquatixDesignSystem.ThemeColors, strings: com.vahitkeskin.equatix.domain.model.AppStrings) -> Unit
) {
    val colors = EquatixDesignSystem.getColors(isDark)
    val strings = AppDictionary.getStrings(language)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        content(colors, strings)
    }
}

// Helper for quick language-specific previews
@Composable
fun PreviewAllLanguages(
    isDark: Boolean = true,
    content: @Composable (colors: EquatixDesignSystem.ThemeColors, strings: com.vahitkeskin.equatix.domain.model.AppStrings) -> Unit
) {
    // EN Preview
    PreviewContainer(isDark, AppLanguage.ENGLISH, content)
}
