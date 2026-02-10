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
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import com.vahitkeskin.equatix.ui.components.EquatixIcons_Answer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.domain.model.AppStrings
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import com.vahitkeskin.equatix.ui.utils.PreviewContainer
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameHeader(
    difficulty: Difficulty,
    gridSize: GridSize,
    isSolved: Boolean,
    colors: EquatixDesignSystem.ThemeColors,
    appStrings: AppStrings,
    onBack: () -> Unit,
    onAutoSolve: () -> Unit,
    onRefresh: () -> Unit,
    onHint: () -> Unit
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
                    color = if (colors.background.red > 0.5f) Color(0xFFE2E8F0) else Color.White.copy(
                        0.1f
                    ),
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
                text = appStrings.getDifficultyLabel(difficulty).uppercase(),
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
                IconButton(onClick = onHint) {
                    Icon(Icons.Default.Lightbulb, "Hint", tint = Color(0xFFEAB308))
                }
                IconButton(onClick = onAutoSolve) {
                    Icon(EquatixIcons_Answer, "Auto Solve", tint = Color(0xFFFF3B30))
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- Dark Mode / TR ---
        PreviewContainer(isDark = true, language = com.vahitkeskin.equatix.domain.model.AppLanguage.TURKISH) { colors, strings ->
            Box(modifier = Modifier.padding(16.dp)) {
                GameHeader(
                    difficulty = Difficulty.HARD,
                    gridSize = GridSize.SIZE_4x4,
                    isSolved = false,
                    colors = colors,
                    appStrings = strings,
                    onBack = {},
                    onAutoSolve = {},
                    onRefresh = {},
                    onHint = {}
                )
            }
        }

        // --- Light Mode / EN ---
        PreviewContainer(isDark = false, language = com.vahitkeskin.equatix.domain.model.AppLanguage.ENGLISH) { colors, strings ->
            Box(modifier = Modifier.padding(16.dp)) {
                GameHeader(
                    difficulty = Difficulty.EASY,
                    gridSize = GridSize.SIZE_3x3,
                    isSolved = true,
                    colors = colors,
                    appStrings = strings,
                    onBack = {},
                    onAutoSolve = {},
                    onRefresh = {},
                    onHint = {}
                )
            }
        }
    }
}