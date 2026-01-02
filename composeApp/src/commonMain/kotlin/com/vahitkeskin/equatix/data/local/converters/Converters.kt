package com.vahitkeskin.equatix.data.local.converters

import androidx.room.TypeConverter
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize

class Converters {
    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String = difficulty.name
    @TypeConverter
    fun toDifficulty(value: String): Difficulty = Difficulty.valueOf(value)

    @TypeConverter
    fun fromGridSize(gridSize: GridSize): String = gridSize.name
    @TypeConverter
    fun toGridSize(value: String): GridSize = GridSize.valueOf(value)
}