package com.vahitkeskin.equatix.platform

import platform.Foundation.NSUserDefaults

actual class KeyValueStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun saveString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    actual fun getString(key: String): String? {
        return defaults.stringForKey(key)
    }

    // --- iOS İÇİN BOOLEAN IMPLEMENTASYONU ---
    actual fun saveBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        // iOS'te key yoksa false döner, bu yüzden kontrol etmemiz daha sağlıklı
        if (defaults.objectForKey(key) == null) {
            return defaultValue
        }
        return defaults.boolForKey(key)
    }
}