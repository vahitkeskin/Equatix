package com.vahitkeskin.equatix.platform

import android.content.Context
import android.preference.PreferenceManager

// Application sınıfında atandığı varsayılan Context
lateinit var appContext: Context

actual class KeyValueStorage {
    // PreferenceManager deprecated olsa da projenin mevcut yapısını bozmamak için korudum.
    // İleride 'appContext.getSharedPreferences("equatix_prefs", Context.MODE_PRIVATE)' yapısına geçebilirsin.
    private val prefs = PreferenceManager.getDefaultSharedPreferences(appContext)

    actual fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getString(key: String): String? {
        return prefs.getString(key, null)
    }

    // --- YENİ EKLENENLER ---
    actual fun saveBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }
}