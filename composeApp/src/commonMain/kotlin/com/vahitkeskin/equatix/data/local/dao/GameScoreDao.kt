package com.vahitkeskin.equatix.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vahitkeskin.equatix.data.local.entity.GameScoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameScoreDao {
    // Skorları puana göre azalan, süreye göre artan şekilde getir (En iyi skor en üstte)
    @Query("SELECT * FROM game_scores ORDER BY score DESC, timeSeconds ASC")
    fun getAllScores(): Flow<List<GameScoreEntity>>

    // Son eklenen 10 skoru getir (Geçmiş listesi için)
    @Query("SELECT * FROM game_scores ORDER BY timestamp DESC LIMIT 10")
    fun getRecentScores(): Flow<List<GameScoreEntity>>

    @Insert
    suspend fun insertScore(score: GameScoreEntity)

    // İsteğe bağlı: Tüm geçmişi temizle
    @Query("DELETE FROM game_scores")
    suspend fun clearAllScores()

    // Mevcut kodların içine ekle:
    @Query("DELETE FROM game_scores WHERE id = :id")
    suspend fun deleteScoreById(id: Long)
}