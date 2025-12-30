package com.vahitkeskin.equatix.domain.model

data class GameState(
    val size: Int,
    val grid: List<CellData>,
    val rowOps: List<Operation>,
    val colOps: List<Operation>,
    val rowResults: List<Int>,
    val colResults: List<Int>,
    val difficulty: Difficulty
)