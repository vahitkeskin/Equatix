package com.vahitkeskin.equatix.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.vahitkeskin.equatix.di.AppModule
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.ui.game.utils.formatTime
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ScoreRecord(
    val id: Long,
    val date: String,
    val score: Int,
    val time: String,
    val difficulty: Difficulty,
    val gridSize: GridSize
)

class HomeViewModel : ScreenModel {

    // Repositories
    private val settingsRepo = AppModule.settingsRepository
    private val scoreRepo = AppModule.scoreRepository

    // --- UI State (Scores) ---
    // Artık .map fonksiyonu tanınacak
    val scores: StateFlow<List<ScoreRecord>> = scoreRepo.recentScores
        .map { entities ->
            entities.map { entity ->
                ScoreRecord(
                    id = entity.id,
                    date = convertTimestampToDate(entity.timestamp),
                    score = entity.score,
                    time = formatTime(entity.timeSeconds),
                    difficulty = entity.difficulty,
                    gridSize = entity.gridSize
                )
            }
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val themeConfig: StateFlow<AppThemeConfig> = settingsRepo.themeConfig
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppThemeConfig.FOLLOW_SYSTEM
        )

    val isSoundOn: StateFlow<Boolean> = settingsRepo.isSoundOn
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val isVibrationOn: StateFlow<Boolean> = settingsRepo.isVibrationOn
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    private fun convertTimestampToDate(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${date.dayOfMonth}/${date.monthNumber} ${date.hour}:${date.minute.toString().padStart(2, '0')}"
    }

    fun setTheme(config: AppThemeConfig) {
        screenModelScope.launch {
            settingsRepo.setTheme(config)
        }
    }

    fun toggleSound() {
        screenModelScope.launch {
            settingsRepo.setSound(!isSoundOn.value)
        }
    }

    fun toggleVibration() {
        screenModelScope.launch {
            settingsRepo.setVibration(!isVibrationOn.value)
        }
    }

    fun addScore(score: Int, timeSeconds: Long, difficulty: Difficulty, gridSize: GridSize) {
        screenModelScope.launch {
            scoreRepo.saveScore(score, timeSeconds, difficulty, gridSize)
        }
    }

    // Sınıfın içine ekle:
    fun deleteScore(id: Long) {
        screenModelScope.launch {
            scoreRepo.deleteScore(id)
        }
    }
}