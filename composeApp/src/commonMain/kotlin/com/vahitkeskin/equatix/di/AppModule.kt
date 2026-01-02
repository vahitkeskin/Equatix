package com.vahitkeskin.equatix.di

import com.vahitkeskin.equatix.data.local.AppDatabase
import com.vahitkeskin.equatix.data.local.createDataStore
import com.vahitkeskin.equatix.domain.repository.ScoreRepository
import com.vahitkeskin.equatix.domain.repository.SettingsRepository

/**
 * Bu obje, uygulama boyunca yaşayan TEKİL (Singleton) bağımlılıkları tutar.
 * DataStore sadece burada, ilk erişildiğinde 1 kere oluşturulur.
 */
object AppModule {

    // 1. Veritabanı Instance'ı (Singleton)
    // Eğer Koin kullanmıyorsak ve manuel DI yapıyorsak, DB oluşturma kısmı biraz platform spesifiktir.
    // Kolaylık olsun diye database değişkenini 'lateinit' yapıp Application sınıfında initialize edebiliriz.

    lateinit var database: AppDatabase

    // 2. Score Repository (Singleton)
    val scoreRepository by lazy {
        ScoreRepository(database.gameScoreDao())
    }

    // by lazy: Sadece ilk ihtiyaç duyulduğunda çalışır ve sonucu hafızada tutar.
    private val dataStore by lazy {
        createDataStore()
    }

    // Tüm ViewModel'ler bu tekil repository örneğini kullanacak.
    val settingsRepository by lazy {
        SettingsRepository(dataStore)
    }
}