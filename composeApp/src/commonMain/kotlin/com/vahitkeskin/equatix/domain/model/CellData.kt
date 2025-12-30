package com.vahitkeskin.equatix.domain.model

data class CellData(
    val id: Int,
    val correctValue: Int,
    val isHidden: Boolean,
    val userInput: String = "",
    val isLocked: Boolean = false,
    val isRevealedBySystem: Boolean = false
)