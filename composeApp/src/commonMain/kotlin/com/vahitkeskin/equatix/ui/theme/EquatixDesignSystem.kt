package com.vahitkeskin.equatix.ui.theme

import androidx.compose.ui.graphics.Color

object EquatixDesignSystem {

    // --- DARK MOD (Cyber/Neon) ---
    private val DarkBg = Color(0xFF020617)
    private val DarkCard = Color(0xFF1E293B)
    private val DarkTextPri = Color(0xFFFFFFFF)
    private val DarkTextSec = Color(0xFF94A3B8)
    private val DarkAccent = Color(0xFF38BDF8)
    private val DarkDivider = Color.White.copy(0.1f)
    private val DarkError = Color(0xFFEF4444) // Red-500 (Yeni)

    // --- LIGHT MOD (Clean/Corporate) ---
    private val LightBg = Color(0xFFF1F5F9)
    private val LightCard = Color(0xFFFFFFFF)
    private val LightTextPri = Color(0xFF0F172A)
    private val LightTextSec = Color(0xFF475569)
    private val LightAccent = Color(0xFF0EA5E9)
    private val LightDivider = Color(0xFFE2E8F0)
    private val LightError = Color(0xFFDC2626) // Red-600 (Yeni)

    // Ortak Renkler
    private val SuccessGreen = Color(0xFF10B981)
    private val Gold = Color(0xFFF59E0B)

    data class ThemeColors(
        val background: Color,
        val textPrimary: Color,
        val textSecondary: Color,
        val accent: Color,
        val gridLines: Color,
        val cardBackground: Color,
        val divider: Color,
        val numpadText: Color,
        val error: Color, // <-- YENİ EKLENDİ
        val success: Color = SuccessGreen,
        val gold: Color = Gold
    )

    fun getColors(isDark: Boolean): ThemeColors {
        return if (isDark) {
            ThemeColors(
                background = DarkBg,
                textPrimary = DarkTextPri,
                textSecondary = DarkTextSec,
                accent = DarkAccent,
                gridLines = DarkAccent.copy(alpha = 0.3f),
                cardBackground = Color.Black.copy(alpha = 0.5f),
                divider = DarkDivider,
                numpadText = Color.White,
                error = DarkError // <-- ATANDI
            )
        } else {
            ThemeColors(
                background = LightBg,
                textPrimary = LightTextPri,
                textSecondary = LightTextSec,
                accent = LightAccent,
                gridLines = Color(0xFFCBD5E1),
                cardBackground = LightCard,
                divider = LightDivider,
                numpadText = LightTextPri,
                error = LightError // <-- ATANDI
            )
        }
    }
}