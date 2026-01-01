package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.game.utils.formatTime
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ResultPanel(
    isSurrendered: Boolean,
    elapsedTime: Long,
    colors: EquatixDesignSystem.ThemeColors,
    onRestart: () -> Unit,
    onGiveUp: () -> Unit
) {
    // GlassBox yerine Light Modda "Solid Card" mantığı uyguluyoruz
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp)
            .shadow(
                elevation = if (colors.background == Color(0xFFF1F5F9)) 10.dp else 0.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .background(colors.cardBackground, RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = colors.divider,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(32.dp), // Padding: 24dp -> 32dp (Genişletildi)
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // İKON BÜYÜTÜLDÜ (48dp -> 64dp)
            Icon(
                imageVector = if (isSurrendered) Icons.Default.Info else Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = if (isSurrendered) Color(0xFFFF9F0A) else colors.success,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // BAŞLIK BÜYÜTÜLDÜ (24sp -> 32sp)
            Text(
                text = if (isSurrendered) "ÇÖZÜM" else "MÜKEMMEL!",
                color = colors.textPrimary,
                fontWeight = FontWeight.Black, // Bold -> Black (Daha kalın)
                fontSize = 32.sp,
                letterSpacing = 1.sp
            )

            if (!isSurrendered) {
                Spacer(modifier = Modifier.height(12.dp))
                // SÜRE METNİ BÜYÜTÜLDÜ (14sp -> 20sp)
                Text(
                    text = "Süre: ${formatTime(elapsedTime)}",
                    color = colors.textSecondary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Bir sonraki sefere!",
                    color = colors.textSecondary,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BUTON BÜYÜTÜLDÜ (Height: 56dp -> 64dp)
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.textPrimary, // Light: Lacivert, Dark: Beyaz
                    contentColor = colors.background     // Light: Gri, Dark: Siyah
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(8.dp, RoundedCornerShape(18.dp))
            ) {
                Text(
                    "YENİ OYUN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp // 16sp -> 18sp
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewResultPanelLose() {
    // 1. Tema Rengini Belirliyoruz (Örn: Dark Mode)
    val isDark = true
    val colors = EquatixDesignSystem.getColors(isDark)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background) // Temanın arka plan rengi
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ResultPanel(
            isSurrendered = true, // "Lose" senaryosu (Pes Etmiş)
            elapsedTime = 123,
            colors = colors, // <--- YENİ EKLENEN PARAMETRE
            onRestart = {},
            onGiveUp = {}
        )
    }
}