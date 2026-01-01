package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameHeader(
    difficulty: Difficulty,
    gridSize: GridSize,
    isSolved: Boolean,
    colors: EquatixDesignSystem.ThemeColors, // Tema renkleri
    onBack: () -> Unit,
    onAutoSolve: () -> Unit,
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                // Light Modda: Hafif gri zemin (Slate-200) görünürlük sağlar
                // Dark Modda: Hafif şeffaf beyaz
                .background(
                    color = if(colors.background.red > 0.5f) Color(0xFFE2E8F0) else Color.White.copy(0.1f),
                    shape = RoundedCornerShape(12.dp)
                )
                .size(40.dp)
        ) {
            // İkon Rengi: Temanın ana metin rengini alır (Koyu Lacivert veya Beyaz)
            Icon(Icons.Default.ArrowBack, "Back", tint = colors.textPrimary)
        }

        // Title / Info
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = difficulty.label.uppercase(),
                color = difficulty.color,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = gridSize.label,
                color = colors.textSecondary, // Gri tonu temaya göre değişir
                fontSize = 10.sp
            )
        }

        // Action Buttons
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isSolved) {
                IconButton(onClick = onAutoSolve) {
                    Icon(Icons.Default.AutoFixHigh, "Auto Solve", tint = Color(0xFFFF3B30))
                }
            }
            IconButton(onClick = onRefresh) {
                // Refresh ikonu da temaya uyumlu
                Icon(Icons.Default.Refresh, "Refresh", tint = colors.textPrimary)
            }
        }
    }
}

@Preview
@Composable
fun PreviewGameHeader() {
    // 1. Tema Renklerini Alıyoruz (Senior Dokunuş)
    val darkColors = EquatixDesignSystem.getColors(isDark = true)
    val lightColors = EquatixDesignSystem.getColors(isDark = false)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray) // Zemin gri olsun ki Light/Dark kutuları belli olsun
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- 1. DARK MODE PREVIEW ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(darkColors.background) // Arka planı temanın rengi yapıyoruz
                .padding(16.dp)
        ) {
            GameHeader(
                difficulty = Difficulty.HARD,
                gridSize = GridSize.SIZE_4x4,
                isSolved = false,
                colors = darkColors, // <--- YENİ EKLENEN PARAMETRE
                onBack = {},
                onAutoSolve = {},
                onRefresh = {}
            )
        }

        // --- 2. LIGHT MODE PREVIEW ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(lightColors.background) // Light arka plan
                .padding(16.dp)
        ) {
            GameHeader(
                difficulty = Difficulty.EASY,
                gridSize = GridSize.SIZE_3x3,
                isSolved = true, // Çözüldüğü için 'Auto Solve' ikonu gizlenir
                colors = lightColors, // <--- YENİ EKLENEN PARAMETRE
                onBack = {},
                onAutoSolve = {},
                onRefresh = {}
            )
        }
    }
}