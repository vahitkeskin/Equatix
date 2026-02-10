package com.vahitkeskin.equatix.domain.model

// 1. Metin Şablonu (Data Class)
data class AppStrings(
    // Genel
    val appName: String,
    val close: String,
    val delete: String,

    // Başlıklar
    val scoresTitle: String,
    val settingsTitle: String,

    // Ana Ekran Seçim Paneli (YENİ EKLENDİ)
    val difficultyLevel: String,
    val gridSize: String,

    // Geçmiş (History)
    val noHistory: String,
    val scorePointSuffix: String, // Örn: "P" veya "Pts"

    // Ayarlar - Başlıklar
    val language: String,
    val appearance: String,
    val preferences: String,

    // Ayarlar - Görünüm
    val system: String,
    val light: String,
    val dark: String,

    // Ayarlar - Tercihler
    val backgroundMusic: String,
    val musicSubtitle: String,
    val vibration: String,
    val dailyReminder: String,

    // Hatırlatıcı Durumu
    val reminderOnPrefix: String, // "Her gün"
    val reminderOff: String,      // "Kapalı"

    // Aksiyonlar
    val startGame: String,

    val resultSolution: String,   // "ÇÖZÜM"
    val resultPerfect: String,    // "MÜKEMMEL!"
    val resultTimePrefix: String, // "Süre:"
    val resultNextTime: String,   // "Bir sonraki sefere!"
    val resultNewGame: String,     // "YENİ OYUN"

    // Zorluk Seviyeleri (YENİ)
    val diffEasy: String,
    val diffMedium: String,
    val diffHard: String,

    // Pause / Duraklatma (YENİ)
    val pauseTitle: String,   // "DURAKLATILDI"
    val pauseResume: String,  // "DEVAM ET"
    val pauseRestart: String, // "Yeniden"
    val pauseQuit: String,     // "Çıkış"

    val btnSelect: String,
    val langSystem: String,

    // Reward Ad
    val rewardAdTitle: String,
    val rewardAdDescription: String,
    val rewardAdWatchButton: String,
    val rewardAdCancelButton: String
) {
    // YARDIMCI FONKSİYON: Enum'ı alıp doğru metni döndürür
    fun getDifficultyLabel(difficulty: Difficulty): String {
        return when (difficulty) {
            Difficulty.EASY -> diffEasy
            Difficulty.MEDIUM -> diffMedium
            Difficulty.HARD -> diffHard
        }
    }
}

// 2. Sözlük (Dictionary)
object AppDictionary {

    val en = AppStrings(
        appName = "Equatix",
        close = "Close",
        delete = "Delete",
        scoresTitle = "SCORES",
        settingsTitle = "SETTINGS",
        difficultyLevel = "DIFFICULTY LEVEL",
        gridSize = "GRID SIZE",
        noHistory = "No games played yet.",
        scorePointSuffix = "P",
        language = "LANGUAGE",
        appearance = "APPEARANCE",
        preferences = "PREFERENCES",
        system = "System",
        light = "Light",
        dark = "Dark",
        backgroundMusic = "Background Music",
        musicSubtitle = "Relaxing Piano",
        vibration = "Vibration",
        dailyReminder = "Daily Reminder",
        reminderOnPrefix = "Every day at",
        reminderOff = "Off",
        startGame = "START GAME",
        resultSolution = "SOLUTION",
        resultPerfect = "PERFECT!",
        resultTimePrefix = "Time:",
        resultNextTime = "Better luck next time!",
        resultNewGame = "NEW GAME",
        diffEasy = "Beginner",
        diffMedium = "Medium",
        diffHard = "Expert",
        pauseTitle = "PAUSED",
        pauseResume = "RESUME",
        pauseRestart = "Restart",
        pauseQuit = "Quit",
        btnSelect = "SELECT",
        langSystem = "System",
        rewardAdTitle = "AUTO SOLVE",
        rewardAdDescription = "Watch a short ad to automatically solve the current level.",
        rewardAdWatchButton = "WATCH AD & SOLVE",
        rewardAdCancelButton = "CANCEL",
    )

    val tr = AppStrings(
        appName = "Equatix",
        close = "Kapat",
        delete = "Sil",
        scoresTitle = "SKORLAR",
        settingsTitle = "AYARLAR",
        difficultyLevel = "ZORLUK SEVİYESİ",
        gridSize = "IZGARA BOYUTU",
        noHistory = "Henüz oyun oynanmadı.",
        scorePointSuffix = "P",
        language = "DİL / LANGUAGE",
        appearance = "GÖRÜNÜM",
        preferences = "TERCİHLER",
        system = "Sistem",
        light = "Açık",
        dark = "Koyu",
        backgroundMusic = "Arka Plan Müziği",
        musicSubtitle = "Rahatlatıcı Piyano",
        vibration = "Titreşim",
        dailyReminder = "Günlük Hatırlatıcı",
        reminderOnPrefix = "Her gün",
        reminderOff = "Kapalı",
        startGame = "OYUNU BAŞLAT",
        resultSolution = "ÇÖZÜM",
        resultPerfect = "MÜKEMMEL!",
        resultTimePrefix = "Süre:",
        resultNextTime = "Bir sonraki sefere!",
        resultNewGame = "YENİ OYUN",
        diffEasy = "Başlangıç",
        diffMedium = "Orta",
        diffHard = "Uzman",
        pauseTitle = "DURAKLATILDI",
        pauseResume = "DEVAM ET",
        pauseRestart = "Yeniden",
        pauseQuit = "Çıkış",
        btnSelect = "SEÇ",
        langSystem = "Sistem",
        rewardAdTitle = "OTOMATİK ÇÖZÜM",
        rewardAdDescription = "Mevcut seviyeyi otomatik olarak çözmek için kısa bir reklam izleyin.",
        rewardAdWatchButton = "REKLAM İZLE VE ÇÖZ",
        rewardAdCancelButton = "İPTAL",
    )

    val de = AppStrings(
        appName = "Equatix",
        close = "Schließen",
        delete = "Löschen",
        scoresTitle = "PUNKTZAHLEN",
        settingsTitle = "EINSTELLUNGEN",
        difficultyLevel = "SCHWIERIGKEIT",
        gridSize = "RASTERGRÖSSE",
        noHistory = "Noch keine Spiele gespielt.",
        scorePointSuffix = "Pkt",
        language = "SPRACHE",
        appearance = "AUSSEHEN",
        preferences = "PRÄFERENZEN",
        system = "System",
        light = "Hell",
        dark = "Dunkel",
        backgroundMusic = "Hintergrundmusik",
        musicSubtitle = "Entspannendes Klavier",
        vibration = "Vibration",
        dailyReminder = "Tägliche Erinnerung",
        reminderOnPrefix = "Täglich um",
        reminderOff = "Aus",
        startGame = "SPIEL STARTEN",
        resultSolution = "LÖSUNG",
        resultPerfect = "PERFEKT!",
        resultTimePrefix = "Zeit:",
        resultNextTime = "Viel Glück beim nächsten Mal!",
        resultNewGame = "NEUES SPIEL",
        diffEasy = "Anfänger",
        diffMedium = "Mittel",
        diffHard = "Experte",
        pauseTitle = "PAUSIERT",
        pauseResume = "FORTSETZEN",
        pauseRestart = "Neustart",
        pauseQuit = "Beenden",
        btnSelect = "WÄHLEN",
        langSystem = "System",
        rewardAdTitle = "AUTOMATISCH LÖSEN",
        rewardAdDescription = "Sehen Sie sich eine kurze Anzeige an, um das aktuelle Level automatisch zu lösen.",
        rewardAdWatchButton = "ANZEIGE ANSEHEN & LÖSEN",
        rewardAdCancelButton = "ABBRECHEN",
    )

    val ar = AppStrings(
        appName = "إكواتيكس",
        close = "إغلاق",
        delete = "حذف",
        scoresTitle = "النتائج",
        settingsTitle = "الإعدادات",
        difficultyLevel = "مستوى الصعوبة",
        gridSize = "حجم الشبكة",
        noHistory = "لم تلعب أي لعبة بعد.",
        scorePointSuffix = "ن",
        language = "اللغة",
        appearance = "المظهر",
        preferences = "التفضيلات",
        system = "النظام",
        light = "فاتح",
        dark = "داكن",
        backgroundMusic = "موسيقى الخلفية",
        musicSubtitle = "بيانو مريح",
        vibration = "الاهتزاز",
        dailyReminder = "تذكير يومي",
        reminderOnPrefix = "يومياً عند",
        reminderOff = "متوقف",
        startGame = "بدء اللعبة",
        resultSolution = "الحل",
        resultPerfect = "ممتاز!",
        resultTimePrefix = "الوقت:",
        resultNextTime = "حظاً أوفر في المرة القادمة!",
        resultNewGame = "لعبة جديدة",
        diffEasy = "مبتدئ",
        diffMedium = "متوسط",
        diffHard = "خبير",
        pauseTitle = "موقوف مؤقتاً",
        pauseResume = "استئناف",
        pauseRestart = "إعادة تشغيل",
        pauseQuit = "خروج",
        btnSelect = "اختيار",
        langSystem = "نظام",
        rewardAdTitle = "حل تلقائي",
        rewardAdDescription = "شاهد إعلانًا قصيرًا لحل المستوى الحالي تلقائيًا.",
        rewardAdWatchButton = "شاهد الإعلان والحل",
        rewardAdCancelButton = "إلغاء",
    )

    fun getStrings(language: AppLanguage): AppStrings {
        return when (language) {
            AppLanguage.TURKISH -> tr
            AppLanguage.GERMAN -> de
            AppLanguage.ARABIC -> ar
            else -> en
        }
    }
}