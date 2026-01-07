package com.vahitkeskin.equatix.platform

import android.content.Context
import android.preference.PreferenceManager

// Bu Context'i Application sınıfında static bir değişkene atadığını varsayıyoruz
// veya constructor'a parametre olarak geçilebilir.
lateinit var appContext: Context

actual class KeyValueStorage {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(appContext)

    actual fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getString(key: String): String? {
        return prefs.getString(key, null)
    }
}