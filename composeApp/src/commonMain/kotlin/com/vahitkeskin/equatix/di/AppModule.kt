package com.vahitkeskin.equatix.di

import com.vahitkeskin.equatix.data.local.createDataStore
import com.vahitkeskin.equatix.domain.repository.SettingsRepository

/**
 * Bu obje, uygulama boyunca yaşayan TEKİL (Singleton) bağımlılıkları tutar.
 * DataStore sadece burada, ilk erişildiğinde 1 kere oluşturulur.
 */
object AppModule {

    // by lazy: Sadece ilk ihtiyaç duyulduğunda çalışır ve sonucu hafızada tutar.
    private val dataStore by lazy {
        createDataStore()
    }

    // Tüm ViewModel'ler bu tekil repository örneğini kullanacak.
    val settingsRepository by lazy {
        SettingsRepository(dataStore)
    }
}