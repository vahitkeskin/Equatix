package com.vahitkeskin.equatix.domain.model

import androidx.compose.ui.graphics.Color

enum class Difficulty(val label: String, val color: Color, val maxNumber: Int) {
    EASY("Başlangıç", Color(0xFF66BB6A), 5),  // Sadece +, -
    MEDIUM("Orta", Color(0xFFFFA726), 9),     // +, -, x
    HARD("Uzman", Color(0xFFEF5350), 15)      // +, -, x, /
}