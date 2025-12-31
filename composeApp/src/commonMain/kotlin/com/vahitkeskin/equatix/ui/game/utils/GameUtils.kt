package com.vahitkeskin.equatix.ui.game.utils

import com.vahitkeskin.equatix.domain.model.GameState

/**
 * Calculates the progress ratio (0.0 - 1.0) based on hidden and correctly filled cells.
 */
fun calculateProgress(gameState: GameState?): Float {
    if (gameState == null) return 0f

    val totalHidden = gameState.grid.count { it.isHidden }
    val currentCorrect = gameState.grid.count {
        it.isHidden && it.userInput == it.correctValue.toString()
    }

    return if (totalHidden == 0) 1f else currentCorrect.toFloat() / totalHidden.toFloat()
}

/**
 * Formats seconds into MM:SS string.
 */
fun formatTime(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}