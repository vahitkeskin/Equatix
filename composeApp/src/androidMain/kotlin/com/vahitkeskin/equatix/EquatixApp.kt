package com.vahitkeskin.equatix

import android.app.Application
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.vahitkeskin.equatix.data.local.AppDatabase
import com.vahitkeskin.equatix.di.AppModule
import kotlinx.coroutines.Dispatchers

class EquatixApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. Android'in veritabanı klasöründeki tam yolu alıyoruz
        val dbFile = applicationContext.getDatabasePath("equatix.db")

        // 2. Veritabanını bu tam yol (absolutePath) ile kuruyoruz
        val db = Room.databaseBuilder<AppDatabase>(
            context = applicationContext,
            name = dbFile.absolutePath // <-- KRİTİK DÜZELTME BURASI
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

        // 3. AppModule'a atıyoruz
        AppModule.database = db
    }
}