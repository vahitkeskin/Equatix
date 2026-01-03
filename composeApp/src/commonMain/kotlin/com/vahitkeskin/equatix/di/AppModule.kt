package com.vahitkeskin.equatix.di

import com.vahitkeskin.equatix.data.local.AppDatabase
import com.vahitkeskin.equatix.data.local.createDataStore
import com.vahitkeskin.equatix.domain.repository.ScoreRepository
import com.vahitkeskin.equatix.domain.repository.SettingsRepository
import com.vahitkeskin.equatix.platform.createNotificationManager // <-- BU IMPORT'U EKLE

object AppModule {

    lateinit var database: AppDatabase

    val scoreRepository by lazy {
        ScoreRepository(database.gameScoreDao())
    }

    private val dataStore by lazy {
        createDataStore()
    }

    val settingsRepository by lazy {
        SettingsRepository(dataStore)
    }

    // ▼▼▼ YENİ EKLENECEK KISIM ▼▼▼
    // Bildirim Yöneticisi (Singleton)
    // createNotificationManager() fonksiyonu platforma özel (android/ios) kodu çalıştırır.
    val notificationManager by lazy {
        createNotificationManager()
    }
}