package com.vahitkeskin.equatix

import android.app.Application
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.work.Configuration
import com.vahitkeskin.equatix.data.local.AppDatabase
import com.vahitkeskin.equatix.di.AppModule
// 1. BU IMPORT'U EKLE (Platform tarafındaki değişkene erişmek için)
import com.vahitkeskin.equatix.platform.appContext
import kotlinx.coroutines.Dispatchers

class EquatixApp : Application(), Configuration.Provider {

    companion object {
        lateinit var instance: EquatixApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 2. BU SATIRI EKLE (Kritik olan kısım burası)
        // KeyValueStorage'ın kullandığı değişkeni burada başlatıyoruz.
        appContext = this

        // --- Veritabanı İşlemleri ---
        val dbFile = applicationContext.getDatabasePath("equatix.db")

        val db = Room.databaseBuilder<AppDatabase>(
            context = applicationContext,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

        AppModule.database = db
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}