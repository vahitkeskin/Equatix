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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.vahitkeskin.equatix.domain.model.AppStrings
import com.vahitkeskin.equatix.ui.game.utils.formatTime
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.max

@Composable
fun ResultPanel(
    isSurrendered: Boolean,
    elapsedTime: Long,
    colors: EquatixDesignSystem.ThemeColors,
    appStrings: AppStrings,
    onRestart: () -> Unit
) {
    // --- PUAN HESAPLAMA ---
    val score = if (isSurrendered) 0 else max(100, (1000 - (elapsedTime * 5)).toInt())

    // --- SLAM ANIMASYONU ---
    val scoreScale = remember { Animatable(if (isSurrendered) 1f else 5f) }
    val scoreAlpha = remember { Animatable(if (isSurrendered) 1f else 0f) }

    LaunchedEffect(Unit) {
        if (!isSurrendered) {
            delay(300)
            scoreAlpha.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
        }
    }

    LaunchedEffect(Unit) {
        if (!isSurrendered) {
            delay(300)
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
            // İKON
            Icon(
                imageVector = if (isSurrendered) Icons.Default.Info else Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = if (isSurrendered) Color(0xFFFF9F0A) else colors.success,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- PUAN / BAŞLIK ---
            if (!isSurrendered) {
                Text(
                    text = "+$score",
                    color = colors.gold,
                    fontWeight = FontWeight.Black,
                    fontSize = 56.sp,
                    modifier = Modifier
                        .scale(scoreScale.value)
                        .alpha(scoreAlpha.value)
                )
            } else {
                Text(
                    text = appStrings.resultSolution, // Dinamik: "ÇÖZÜM"
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(if(!isSurrendered) 8.dp else 24.dp))

            // SÜRE VE MESAJ
            if (!isSurrendered) {
                Text(
                    text = appStrings.resultPerfect, // Dinamik: "MÜKEMMEL!"
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    // Dinamik: "Süre: 00:45"
                    text = "${appStrings.resultTimePrefix} ${formatTime(elapsedTime)}",
                    color = colors.textSecondary,
                    fontSize = 16.sp
                )
            } else {
                Text(
                    text = appStrings.resultNextTime, // Dinamik: "Bir sonraki sefere!"
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
                    text = appStrings.resultNewGame, // Dinamik: "YENİ OYUN"
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
    val strings by HomeViewModel().strings.collectAsState()

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
            appStrings = strings,
            onRestart = {}
        )
    }
}