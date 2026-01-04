package com.vahitkeskin.equatix.ui.theme

import androidx.compose.ui.graphics.Color

object EquatixDesignSystem {

    // ========================================================================
    // 🎨 RENK PALETİ (PALETTE DEFINITIONS)
    // Modern UI trendlerine uygun "Slate" ve "Sky" tonları kullanıldı.
    // ========================================================================

    // --- DARK PALETTE (Deep Focus / Cyber Math) ---
    private val DarkBaseBg      = Color(0xFF0B1121) // Saf siyah değil, çok koyu lacivert (Gözü yormaz)
    private val DarkSurface     = Color(0xFF1E293B) // Slate-800
    private val DarkTextPri     = Color(0xFFF1F5F9) // Slate-100 (Yumuşak Beyaz)
    private val DarkTextSec     = Color(0xFF94A3B8) // Slate-400
    private val DarkPrimary     = Color(0xFF38BDF8) // Sky-400 (Neon Mavi - Odak Rengi)
    private val DarkSuccess     = Color(0xFF34D399) // Emerald-400
    private val DarkWarning     = Color(0xFFFBBF24) // Amber-400
    // Senin istediğin özel kırmızı tonu (Hafifçe ayarlandı ama o canlılığı koruyor)
    private val DarkError       = Color(0xFFFF453A)

    // --- LIGHT PALETTE (Clean Academic / Corporate) ---
    private val LightBaseBg     = Color(0xFFF8FAFC) // Slate-50 (Kağıt beyazı)
    private val LightSurface    = Color(0xFFFFFFFF) // Saf Beyaz
    private val LightTextPri    = Color(0xFF0F172A) // Slate-900 (Mürekkep rengi)
    private val LightTextSec    = Color(0xFF64748B) // Slate-500
    private val LightPrimary    = Color(0xFF0284C7) // Sky-600 (Kurumsal Mavi)
    private val LightSuccess    = Color(0xFF059669) // Emerald-600
    private val LightWarning    = Color(0xFFD97706) // Amber-600
    private val LightError      = Color(0xFFDC2626) // Red-600

    // --- OPERATOR COLORS (Math Logic) ---
    // Her iki modda da okunabilir ama tona uygun renkler
    private val OpAddColor      = Color(0xFF3B82F6) // Blue (Toplama)
    private val OpSubColorDark  = Color(0xFFFF453A) // Red (Çıkarma - Dark)
    private val OpSubColorLight = Color(0xFFEF4444) // Red (Çıkarma - Light)
    private val OpMulColor      = Color(0xFFF59E0B) // Amber/Orange (Çarpma)
    private val OpDivColor      = Color(0xFFA855F7) // Purple (Bölme)


    // ========================================================================
    // 🖌️ THEME DATA CLASS
    // Tüm renkleri buradan yöneteceğiz.
    // ========================================================================
    data class ThemeColors(
        val background: Color,
        val cardBackground: Color,
        val textPrimary: Color,
        val textSecondary: Color,
        val accent: Color,      // Ana vurgu rengi
        val gridLines: Color,   // Izgara çizgileri
        val divider: Color,
        val numpadText: Color,  // Tuş takımı yazıları
        val error: Color,       // Hata / Silme rengi
        val success: Color,     // Başarı rengi
        val gold: Color,        // Bonus / Timer durdu rengi

        // Operatörler için özel renkler (Böylece UI içinde when check yapmana gerek kalmaz)
        val opAdd: Color,
        val opSub: Color,
        val opMul: Color,
        val opDiv: Color
    )

    // ========================================================================
    // ⚙️ GETTER FUNCTION
    // ========================================================================
    fun getColors(isDark: Boolean): ThemeColors {
        return if (isDark) {
            // --- DARK MOD KONFIGURASYONU ---
            ThemeColors(
                background = DarkBaseBg,
                cardBackground = Color.Black.copy(alpha = 0.5f), // Hafif transparan siyah
                textPrimary = DarkTextPri,
                textSecondary = DarkTextSec,
                accent = DarkPrimary,
                gridLines = DarkPrimary.copy(alpha = 0.2f), // Neon ızgara etkisi
                divider = Color.White.copy(alpha = 0.1f),
                numpadText = Color.White, // Senin istediğin Numpad beyazı
                error = DarkError,
                success = DarkSuccess,
                gold = DarkWarning,

                // Operatörler (Neonumsu)
                opAdd = OpAddColor,
                opSub = OpSubColorDark, // Senin kırmızın
                opMul = OpMulColor,
                opDiv = OpDivColor
            )
        } else {
            // --- LIGHT MOD KONFIGURASYONU ---
            ThemeColors(
                background = LightBaseBg,
                cardBackground = LightSurface,
                textPrimary = LightTextPri,
                textSecondary = LightTextSec,
                accent = LightPrimary,
                gridLines = Color(0xFFCBD5E1), // Slate-300 (Yumuşak gri çizgi)
                divider = Color(0xFFE2E8F0),
                numpadText = LightTextPri, // Koyu Lacivert
                error = LightError,
                success = LightSuccess,
                gold = LightWarning,

                // Operatörler (Daha mat ve kurumsal)
                opAdd = Color(0xFF2563EB), // Biraz daha koyu mavi
                opSub = OpSubColorLight,
                opMul = Color(0xFFD97706), // Koyu Amber
                opDiv = Color(0xFF9333EA)  // Koyu Mor
            )
        }
    }
}