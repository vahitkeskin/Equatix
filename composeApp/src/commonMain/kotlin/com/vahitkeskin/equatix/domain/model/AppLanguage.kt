package com.vahitkeskin.equatix.domain.model

import com.vahitkeskin.equatix.platform.getSystemLanguageCode

enum class AppLanguage(
    val code: String,
    val label: String,
    val flagEmoji: String
) {
    TURKISH("tr", "Türkçe", "🇹🇷"),
    ENGLISH("en", "English", "🇺🇸"),
    GERMAN("de", "Deutsch", "🇩🇪"),
    ARABIC("ar", "العربية", "🇸🇦");

    companion object {
        fun getDeviceLanguage(): AppLanguage {
            val systemCode = getSystemLanguageCode()
            return entries.find { it.code == systemCode } ?: ENGLISH
        }
    }
}