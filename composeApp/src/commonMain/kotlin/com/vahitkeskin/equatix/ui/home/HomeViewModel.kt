package com.vahitkeskin.equatix.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.vahitkeskin.equatix.data.local.createDataStore
import com.vahitkeskin.equatix.di.AppModule
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Room veritabanındaki Entity'nize karşılık gelen model
data class ScoreRecord(
    val id: Long,
    val date: String,
    val score: Int,
    val time: String, // "01:23"
    val difficulty: Difficulty,
    val gridSize: GridSize
)

class HomeViewModel : ScreenModel {

    // --- Repositories ---
    // NOT: Gerçek bir projede burası Koin/Hilt ile inject edilmelidir.
    // Şimdilik manuel instance alıyoruz.
    private val settingsRepo = AppModule.settingsRepository

    // --- UI State (Scores) ---
    private val _scores = MutableStateFlow<List<ScoreRecord>>(emptyList())
    val scores = _scores.asStateFlow()

    // --- UI State (Settings) ---

    // 1. TEMA AYARI (YENİ)
    // Repository'den gelen akışı StateFlow'a çeviriyoruz.
    // Başlangıç değeri FOLLOW_SYSTEM, ama DataStore okununca gerçek değer gelecek.
    val themeConfig: StateFlow<AppThemeConfig> = settingsRepo.themeConfig
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppThemeConfig.FOLLOW_SYSTEM
        )

    // 2. SES AYARI
    val isSoundOn: StateFlow<Boolean> = settingsRepo.isSoundOn
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    // 3. TİTREŞİM AYARI
    val isVibrationOn: StateFlow<Boolean> = settingsRepo.isVibrationOn
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    init {
        loadScores()
    }

    private fun loadScores() {
        screenModelScope.launch {
            // İleride burası Room DB'den beslenecek.
            // Şimdilik Mock Data:
            _scores.value = listOf(
                ScoreRecord(1, "Bugün", 1500, "00:45", Difficulty.HARD, GridSize.SIZE_5x5),
                ScoreRecord(2, "Dün", 1200, "01:10", Difficulty.MEDIUM, GridSize.SIZE_4x4),
                ScoreRecord(3, "25 Kas", 950, "00:55", Difficulty.EASY, GridSize.SIZE_3x3),
                ScoreRecord(4, "20 Kas", 800, "02:00", Difficulty.HARD, GridSize.SIZE_5x5),
            )
        }
    }

    // --- Actions (Kullanıcı Etkileşimleri) ---

    // Temayı Değiştir (UI'dan çağrılır)
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
}