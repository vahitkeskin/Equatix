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

    // --- State: Bildirim İzni ve Durumu ---
    private val _isNotificationEnabled = MutableStateFlow(false)
    val isNotificationEnabled = _isNotificationEnabled.asStateFlow()

    // --- State: Seçilen Saat (UI'da göstermek için) ---
    // Varsayılan olarak 09:00 başlatıyoruz.
    // (İsterseniz NotificationManager'a getAlarmHour() ekleyip oradan da okuyabilirsiniz)
    private var savedHour = 9
    private var savedMinute = 0

    private val _notificationTime = MutableStateFlow(savedHour to savedMinute)
    val notificationTime = _notificationTime.asStateFlow()

    private val musicManager = AppModule.musicManager

    // Müzik Açık/Kapalı durumu (Varsayılan olarak kapalı başlasın isteyebilirsin)
    // Bunu da DataStore'a kaydedebilirsin ama şimdilik hafızada tutalım.
    private val _isMusicOn = MutableStateFlow(false)
    val isMusicOn = _isMusicOn.asStateFlow()

    private val storage = KeyValueStorage() // Platforma özel storage
    private val LANGUAGE_KEY = "selected_language"

    // 1. Dil State'i
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage = _currentLanguage.asStateFlow()

    // 2. String State'i (UI burayı dinleyecek)
    private val _strings = MutableStateFlow(AppDictionary.en)
    val strings = _strings.asStateFlow()

    // --- BAŞLANGIÇ KONTROLÜ ---
    init {
        checkInitialState()
        loadSavedLanguage()
    }

    private fun loadSavedLanguage() {
        // Kayıtlı kodu getir
        val savedCode = storage.getString(LANGUAGE_KEY)

        val language = if (savedCode != null) {
            // Kayıt varsa ona uygun dili bul
            AppLanguage.values().find { it.code == savedCode } ?: AppLanguage.ENGLISH
        } else {
            // Kayıt yoksa SİSTEM dilini kullan (önceki kodumuz)
            AppLanguage.getDeviceLanguage()
        }

        // State'leri güncelle
        updateLanguageState(language)
    }

    fun setLanguage(language: AppLanguage) {
        // 1. Kaydet
        storage.saveString(LANGUAGE_KEY, language.code)
        // 2. State'i Güncelle
        updateLanguageState(language)
    }

    private fun updateLanguageState(language: AppLanguage) {
        _currentLanguage.value = language
        // Dili değiştirdiğimiz an Stringler de değişir
        _strings.value = AppDictionary.getStrings(language)
    }

    fun toggleMusic(enabled: Boolean) {
        _isMusicOn.value = enabled
        if (enabled) {
            // 0.2f -> Çok kısık, rahatsız etmeyen (Loş) bir ses seviyesi
            musicManager.playLooping(volume = 0.2f)
        } else {
            musicManager.stop()
        }
    }

    // Uygulama alta atılınca müzik dursun istersen onDispose/onCleared içinde stop çağırmalısın.
    override fun onDispose() {
        musicManager.stop()
        super.onDispose()
    }

    // Uygulama açıldığında veya Resume olduğunda durumu kontrol et
    private fun checkInitialState() {
        val hasPerm = notificationManager.hasPermission()
        // Kullanıcı daha önce switch'i açık bıraktı mı? (Varsayılan true)
        val isUserEnabled = notificationManager.isReminderEnabled()

        // Hem izin verilmeli HEM DE kullanıcı sistemi aktif etmiş olmalı
        if (hasPerm && isUserEnabled) {
            _isNotificationEnabled.value = true
        } else {
            // İzin yoksa veya kullanıcı kapattıysa kapalı göster
            _isNotificationEnabled.value = false
        }
    }

    // UI'dan çağrılan izin kontrolü tazeleme (Örn: Ayarlardan dönünce)
    fun refreshPermissionStatus() {
        checkInitialState()
    }

    fun openAppSettings() {
        notificationManager.openSystemSettings()
    }

    // --- ALARM KURMA / İPTAL ETME ---
    fun setNotificationSchedule(enable: Boolean) {

        val nowInstant = Clock.System.now()

        //TODO time test
        val targetInstant = nowInstant.plus(2.minutes)
        val targetTime = targetInstant.toLocalDateTime(TimeZone.currentSystemDefault())
        //savedHour = targetTime.hour
        //savedMinute = targetTime.minute

        // UI'daki saati güncelle (StateFlow)
        _notificationTime.value = savedHour to savedMinute

        if (enable) {
            // Rastgele, ilgi çekici bir mesaj seç
            val message = NotificationContent.getRandomMessage()

            // Alarmı Kur (Manager bunu hafızaya da kaydeder)
            notificationManager.scheduleDailyReminder(
                hour = savedHour,
                minute = savedMinute,
                title = message.title,
                body = message.body
            )

            // Switch'i aç
            _isNotificationEnabled.value = true
        } else {
            // İptal Et (Manager hafızada is_enabled = false yapar)
            notificationManager.cancelDailyReminder()

            // Switch'i kapat
            _isNotificationEnabled.value = false
        }
    }

    // --- UI State (Scores & Settings) ---

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