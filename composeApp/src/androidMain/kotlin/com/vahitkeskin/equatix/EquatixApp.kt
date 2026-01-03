package com.vahitkeskin.equatix

import android.app.Application
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.work.Configuration // <-- BU EKLENDİ
import com.vahitkeskin.equatix.data.local.AppDatabase
import com.vahitkeskin.equatix.di.AppModule
import kotlinx.coroutines.Dispatchers

class EquatixApp : Application(), Configuration.Provider {

    companion object {
        lateinit var instance: EquatixApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 1. Veritabanı Yolu
        val dbFile = applicationContext.getDatabasePath("equatix.db")

        // 2. Veritabanı Kurulumu
        val db = Room.databaseBuilder<AppDatabase>(
            context = applicationContext,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

        // 3. Dependency Injection
        AppModule.database = db
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO) // Log seviyesi
            .build()
}