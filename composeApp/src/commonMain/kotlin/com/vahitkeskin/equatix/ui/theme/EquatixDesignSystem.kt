package com.vahitkeskin.equatix.ui.theme

import androidx.compose.ui.graphics.Color

object EquatixDesignSystem {

    // --- DARK MOD (Cyber/Neon) ---
    private val DarkBg = Color(0xFF020617)       // Derin Lacivert Siyah
    private val DarkCard = Color(0xFF1E293B)     // Yarı saydam koyu
    private val DarkTextPri = Color(0xFFFFFFFF)  // Beyaz Metin
    private val DarkTextSec = Color(0xFF94A3B8)  // Gri Metin
    private val DarkAccent = Color(0xFF38BDF8)   // Neon Mavi
    private val DarkDivider = Color.White.copy(0.1f)

    // --- LIGHT MOD (Clean/Corporate) ---
    // Sorun buradaydı. Arka planı biraz kırıyoruz, metinleri KOYU yapıyoruz.
    private val LightBg = Color(0xFFF1F5F9)      // Slate-100 (Kırık Beyaz/Gri)
    private val LightCard = Color(0xFFFFFFFF)    // Saf Beyaz Kart
    private val LightTextPri = Color(0xFF0F172A) // Slate-900 (Çok Koyu Lacivert - Neredeyse Siyah)
    private val LightTextSec = Color(0xFF475569) // Slate-600
    private val LightAccent = Color(0xFF0EA5E9)  // Sky-500
    private val LightDivider = Color(0xFFE2E8F0) // Slate-200 (Belirgin Çizgi)

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
        val numpadText: Color, // Yeni: Numpad rengi için özel alan
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
                cardBackground = Color.Black.copy(alpha = 0.5f), // Cam efekti
                divider = DarkDivider,
                numpadText = Color.White // Dark modda tuşlar beyaz
            )
        } else {
            ThemeColors(
                background = LightBg,
                textPrimary = LightTextPri, // <-- ARTIK KOYU RENK
                textSecondary = LightTextSec,
                accent = LightAccent,
                gridLines = Color(0xFFCBD5E1), // Belirgin Gri Çizgiler
                cardBackground = LightCard,    // Saf Beyaz Kart
                divider = LightDivider,
                numpadText = LightTextPri // Light modda tuşlar koyu
            )
        }
    }
}