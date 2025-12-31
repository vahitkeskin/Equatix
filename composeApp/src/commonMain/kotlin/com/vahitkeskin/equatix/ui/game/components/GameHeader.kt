package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameHeader(
    difficulty: Difficulty,
    gridSize: GridSize,
    isSolved: Boolean,
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
                .background(Color.White.copy(0.1f), RoundedCornerShape(12.dp))
                .size(40.dp)
        ) {
            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
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
            Text(gridSize.label, color = Color.Gray, fontSize = 10.sp)
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
                Icon(Icons.Default.Refresh, "Refresh", tint = Color.White)
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
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        GameHeader(
            difficulty = Difficulty.EASY,
            gridSize = GridSize.SIZE_3x3,
            isSolved = false,
            onBack = {},
            onAutoSolve = {},
            onRefresh = {}
        )

        GameHeader(
            difficulty = Difficulty.HARD,
            gridSize = GridSize.SIZE_4x4,
            isSolved = true,
            onBack = {},
            onAutoSolve = {},
            onRefresh = {}
        )
    }
}