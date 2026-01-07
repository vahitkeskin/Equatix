package com.vahitkeskin.equatix.domain.model

import androidx.compose.ui.graphics.Color

enum class Difficulty(val color: Color, val maxNumber: Int) {
    EASY(Color(0xFF66BB6A), 5),  // Sadece +, -
    MEDIUM(Color(0xFFFFA726), 9),     // +, -, x
    HARD(Color(0xFFEF5350), 15)      // +, -, x, /
}