package com.vahitkeskin.equatix.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.vahitkeskin.equatix.di.AppModule
import com.vahitkeskin.equatix.domain.model.AppDictionary
import com.vahitkeskin.equatix.domain.model.AppLanguage
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.platform.KeyValueStorage
import com.vahitkeskin.equatix.ui.game.utils.formatTime
import com.vahitkeskin.equatix.utils.NotificationContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes

data class ScoreRecord(
    val id: Long,
    val date: String,
    val score: Int,
    val time: String,
    val difficulty: Difficulty,
    val gridSize: GridSize
)

class HomeViewModel : ScreenModel {

    // --- Repositories ---
    private val settingsRepo = AppModule.settingsRepository
    private val scoreRepo = AppModule.scoreRepository
    private val notificationManager = AppModule.notificationManager
    private val musicManager = AppModule.musicManager
    private val storage = KeyValueStorage()

    // --- KEYS ---
    private val LANGUAGE_KEY = "selected_language"
    private val MUSIC_KEY = "is_music_on"

    // --- State: Bildirim İzni ve Durumu ---
    private val _isNotificationEnabled = MutableStateFlow(false)
    val isNotificationEnabled = _isNotificationEnabled.asStateFlow()

    // --- State: Seçilen Saat ---
    private var savedHour = 9
    private var savedMinute = 0

    private val _notificationTime = MutableStateFlow(savedHour to savedMinute)
    val notificationTime = _notificationTime.asStateFlow()

    // --- State: Müzik ---
    private val _isMusicOn = MutableStateFlow(true)
    val isMusicOn = _isMusicOn.asStateFlow()

    // --- State: Dil ---
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage = _currentLanguage.asStateFlow()

    private val _strings = MutableStateFlow(AppDictionary.en)
    val strings = _strings.asStateFlow()

    // --- BAŞLANGIÇ KONTROLÜ ---
    init {
        checkInitialState()
        loadSavedLanguage()
        // NOT: loadMusicState() buraya KOYMUYORUZ.
        // Çünkü müziği HomeScreen'deki Lifecycle (Resume) yönetecek.
    }

    // --- DİL YÖNETİMİ ---
    private fun loadSavedLanguage() {
        val savedCode = storage.getString(LANGUAGE_KEY)

        val savedLanguage = if (savedCode != null) {
            AppLanguage.values().find { it.code == savedCode } ?: AppLanguage.SYSTEM
        } else {
            AppLanguage.SYSTEM
        }

        _currentLanguage.value = savedLanguage

        val realLanguageToLoad = if (savedLanguage == AppLanguage.SYSTEM) {
            AppLanguage.getDeviceLanguage()
        } else {
            savedLanguage
        }

        _strings.value = AppDictionary.getStrings(realLanguageToLoad)
    }

    fun setLanguage(selectedLanguage: AppLanguage) {
        _currentLanguage.value = selectedLanguage
        storage.saveString(LANGUAGE_KEY, selectedLanguage.code)

        val languageToLoad = if (selectedLanguage == AppLanguage.SYSTEM) {
            AppLanguage.getDeviceLanguage()
        } else {
            selectedLanguage
        }

        _strings.value = AppDictionary.getStrings(languageToLoad)
    }

    // --- MÜZİK YÖNETİMİ (Lifecycle ve Ayarlar) ---

    // 1. Ekran açıldığında (Resume) çağrılır
    fun checkMusicOnResume() {
        // Kayıtlı ayarı oku, yoksa varsayılan TRUE olsun
        val isMusicEnabled = storage.getBoolean(MUSIC_KEY, defaultValue = true)

        // UI State'i güncelle
        _isMusicOn.value = isMusicEnabled

        // Sadece ayar açıksa çal
        if (isMusicEnabled) {
            musicManager.playLooping(volume = 0.2f)
        }
    }

    // 2. Ekran kapandığında (Pause) çağrılır
    fun pauseMusicOnBackground() {
        musicManager.stop()
    }

    // 3. Kullanıcı Switch'e bastığında çağrılır
    fun toggleMusic(enabled: Boolean) {
        _isMusicOn.value = enabled
        // Ayarı kalıcı olarak kaydet
        storage.saveBoolean(MUSIC_KEY, enabled)

        if (enabled) {
            musicManager.playLooping(volume = 0.2f)
        } else {
            musicManager.stop()
        }
    }

    override fun onDispose() {
        musicManager.stop()
        super.onDispose()
    }

    // --- BİLDİRİM YÖNETİMİ ---
    private fun checkInitialState() {
        val hasPerm = notificationManager.hasPermission()
        val isUserEnabled = notificationManager.isReminderEnabled()

        if (hasPerm && isUserEnabled) {
            _isNotificationEnabled.value = true
        } else {
            _isNotificationEnabled.value = false
        }
    }

    fun refreshPermissionStatus() {
        checkInitialState()
    }

    fun openAppSettings() {
        notificationManager.openSystemSettings()
    }

    fun setNotificationSchedule(enable: Boolean) {
        val nowInstant = Clock.System.now()
        val targetInstant = nowInstant.plus(2.minutes)
        // val targetTime = targetInstant.toLocalDateTime(TimeZone.currentSystemDefault())

        _notificationTime.value = savedHour to savedMinute

        if (enable) {
            val message = NotificationContent.getRandomMessage()
            notificationManager.scheduleDailyReminder(
                hour = savedHour,
                minute = savedMinute,
                title = message.title,
                body = message.body
            )
            _isNotificationEnabled.value = true
        } else {
            notificationManager.cancelDailyReminder()
            _isNotificationEnabled.value = false
        }
    }

    // --- SKORLAR VE AYARLAR ---

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
        return "${date.dayOfMonth}/${date.monthNumber} ${date.hour}:${
            date.minute.toString().padStart(2, '0')
        }"
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

    fun deleteScore(id: Long) {
        screenModelScope.launch {
            scoreRepo.deleteScore(id)
        }
    }
}