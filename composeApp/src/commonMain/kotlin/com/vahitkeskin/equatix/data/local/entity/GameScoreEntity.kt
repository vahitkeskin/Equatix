package com.vahitkeskin.equatix.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import kotlinx.datetime.Clock

@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val score: Int,
    val timeSeconds: Long, // Süreyi saniye olarak saklamak en temizidir
    val difficulty: Difficulty, // Room enumları destekler (TypeConverter gerekebilir)
    val gridSize: GridSize,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)