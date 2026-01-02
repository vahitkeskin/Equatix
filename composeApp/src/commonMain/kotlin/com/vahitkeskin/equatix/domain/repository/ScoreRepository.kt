package com.vahitkeskin.equatix.domain.repository

import com.vahitkeskin.equatix.data.local.dao.GameScoreDao
import com.vahitkeskin.equatix.data.local.entity.GameScoreEntity
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import kotlinx.coroutines.flow.Flow

class ScoreRepository(private val dao: GameScoreDao) {

    // Tüm skorları anlık izle (Flow sayesinde veritabanı değişince UI otomatik güncellenir)
    val recentScores: Flow<List<GameScoreEntity>> = dao.getRecentScores()

    // Yeni skor kaydet
    suspend fun saveScore(score: Int, timeSeconds: Long, difficulty: Difficulty, gridSize: GridSize) {
        val entity = GameScoreEntity(
            score = score,
            timeSeconds = timeSeconds,
            difficulty = difficulty,
            gridSize = gridSize
        )
        dao.insertScore(entity)
    }

    // Mevcut kodların içine ekle:
    suspend fun deleteScore(id: Long) {
        dao.deleteScoreById(id)
    }
}