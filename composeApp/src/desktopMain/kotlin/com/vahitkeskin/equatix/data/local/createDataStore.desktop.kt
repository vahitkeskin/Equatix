package com.vahitkeskin.equatix.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.io.File

actual fun createDataStore(): DataStore<Preferences> {
    // 1. DÜZELTME: 'create' yerine 'createWithPath' kullanıyoruz (Çünkü Okio Path döndürüyoruz).
    return PreferenceDataStoreFactory.createWithPath(
        // 2. DÜZELTME: Parametre adı kütüphanede 'produceFile' olarak geçiyor.
        produceFile = {
            val os = System.getProperty("os.name").lowercase()
            val userHome = System.getProperty("user.home")
            val appDataDir = when {
                os.contains("win") -> File(System.getenv("APPDATA"), "Equatix")
                os.contains("mac") -> File(userHome, "Library/Application Support/Equatix")
                else -> File(userHome, ".local/share/Equatix")
            }

            if (!appDataDir.exists()) {
                appDataDir.mkdirs()
            }

            File(appDataDir, "equatix.preferences_pb").absolutePath.toPath()
        }
    )
}