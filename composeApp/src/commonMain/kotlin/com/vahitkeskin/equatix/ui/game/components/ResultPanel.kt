package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.game.utils.formatTime
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.max

@Composable
fun ResultPanel(
    isSurrendered: Boolean,
    elapsedTime: Long,
    colors: EquatixDesignSystem.ThemeColors,
    onRestart: () -> Unit
) {
    // --- PUAN HESAPLAMA (Örnek Mantık) ---
    // Pes edildiyse 0, değilse süreye göre puan (Süre arttıkça puan azalır, min 100 puan)
    val score = if (isSurrendered) 0 else max(100, (1000 - (elapsedTime * 5)).toInt())

    // --- SLAM ANIMASYONU (Yere Çakılma) ---

    // 1. Ölçek: 5 kat büyüklükten (ekrandan taşmış gibi) başlayacak.
    val scoreScale = remember { Animatable(if (isSurrendered) 1f else 5f) }

    // 2. Opaklık: Görünmezden başlayacak.
    val scoreAlpha = remember { Animatable(if (isSurrendered) 1f else 0f) }

    LaunchedEffect(Unit) {
        if (!isSurrendered) {
            delay(300)
            scoreAlpha.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
        }
    }

    LaunchedEffect(Unit) {
        if (!isSurrendered) {
            delay(300) // Panel açıldıktan sonra GÜM diye vursun

            // YERE ÇAKILMA EFEKTİ
            // DampingRatio: 0.4f -> Yere düşünce biraz titrer (Jelly effect)
            // Stiffness: Medium -> Ne çok hızlı ne çok yavaş, ağırlık hissi verir
            scoreScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.4f,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    // --- UI ---

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
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // İKON (Sabit veya hafif animasyonlu kalabilir)
            Icon(
                imageVector = if (isSurrendered) Icons.Default.Info else Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = if (isSurrendered) Color(0xFFFF9F0A) else colors.success,
                modifier = Modifier.size(48.dp) // İkonu biraz küçülttük ki PUAN öne çıksın
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- BURASI PUANIN OLDUĞU YER (SLAM ANIMASYONU BURADA) ---
            if (!isSurrendered) {
                Text(
                    text = "+$score",
                    color = colors.gold, // Altın rengi (Dikkat çekici)
                    fontWeight = FontWeight.Black,
                    fontSize = 56.sp, // Çok büyük font
                    modifier = Modifier
                        .scale(scoreScale.value) // Büyüklük animasyonu
                        .alpha(scoreAlpha.value) // Görünürlük animasyonu
                )
            } else {
                Text(
                    text = "ÇÖZÜM",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(if(!isSurrendered) 8.dp else 24.dp))

            // SÜRE VE MESAJ
            if (!isSurrendered) {
                Text(
                    text = "MÜKEMMEL!",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Süre: ${formatTime(elapsedTime)}",
                    color = colors.textSecondary,
                    fontSize = 16.sp
                )
            } else {
                Text(
                    text = "Bir sonraki sefere!",
                    color = colors.textSecondary,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // YENİ OYUN BUTONU
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.textPrimary,
                    contentColor = colors.background
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
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewResultPanelWinScore() {
    val isDark = true
    val colors = EquatixDesignSystem.getColors(isDark)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ResultPanel(
            isSurrendered = false,
            elapsedTime = 45,
            colors = colors,
            onRestart = {}
        )
    }
}