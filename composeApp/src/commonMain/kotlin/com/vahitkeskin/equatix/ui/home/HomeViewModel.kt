package com.vahitkeskin.equatix.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // --- State ---
    // Room'dan gelecek verileri burada tutacağız
    private val _scores = MutableStateFlow<List<ScoreRecord>>(emptyList())
    val scores = _scores.asStateFlow()

    private val _isSoundOn = MutableStateFlow(true)
    val isSoundOn = _isSoundOn.asStateFlow()

    private val _isVibrationOn = MutableStateFlow(true)
    val isVibrationOn = _isVibrationOn.asStateFlow()

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

    fun toggleSound() { _isSoundOn.value = !_isSoundOn.value }
    fun toggleVibration() { _isVibrationOn.value = !_isVibrationOn.value }
}