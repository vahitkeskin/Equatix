package com.vahitkeskin.equatix.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.vahitkeskin.equatix.data.local.createDataStore
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
    // Not: createDataStore() fonksiyonunun doğru import edildiğinden emin olun.
    // Gerçek projede bu sınıfı DI (Koin/Hilt) ile inject etmek daha doğrudur.
    private val settingsRepo = SettingsRepository(createDataStore())

    // --- UI State (Scores) ---
    private val _scores = MutableStateFlow<List<ScoreRecord>>(emptyList())
    val scores = _scores.asStateFlow()

    // --- UI State (Settings - DataStore) ---
    // DataStore'dan gelen Flow'u StateFlow'a çeviriyoruz.
    // Böylece UI her zaman güncel veriyi dinler ve uygulama açıldığında son ayar gelir.
    val isSoundOn: StateFlow<Boolean> = settingsRepo.isSoundOn
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000), // UI arka plana düşerse 5sn sonra durdur
            initialValue = true // Varsayılan değer
        )

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
            // TODO: Burada Room Repository'den verileri çekin
            // val data = repository.getAllScores()
            // _scores.value = data

            // MOCK DATA (Örnek Görünüm İçin)
            _scores.value = listOf(
                ScoreRecord(1, "Bugün", 1500, "00:45", Difficulty.HARD, GridSize.SIZE_5x5),
                ScoreRecord(2, "Dün", 1200, "01:10", Difficulty.MEDIUM, GridSize.SIZE_4x4),
                ScoreRecord(3, "25 Kas", 950, "00:55", Difficulty.EASY, GridSize.SIZE_3x3),
                ScoreRecord(4, "20 Kas", 800, "02:00", Difficulty.HARD, GridSize.SIZE_5x5),
            )
        }
    }

    // --- Actions ---

    fun toggleSound() {
        screenModelScope.launch {
            // Mevcut değerin tersini DataStore'a yazıyoruz.
            // isSoundOn StateFlow olduğu için otomatik olarak güncellenecektir.
            settingsRepo.setSound(!isSoundOn.value)
        }
    }

    fun toggleVibration() {
        screenModelScope.launch {
            settingsRepo.setVibration(!isVibrationOn.value)
        }
    }
}