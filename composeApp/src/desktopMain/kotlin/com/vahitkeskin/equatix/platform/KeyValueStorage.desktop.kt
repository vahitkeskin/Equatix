package com.vahitkeskin.equatix.platform

import java.util.prefs.Preferences

actual class KeyValueStorage {
    private val prefs = Preferences.userRoot().node("com.vahitkeskin.equatix")

    actual fun saveString(key: String, value: String) {
        prefs.put(key, value)
    }

    actual fun getString(key: String): String? {
        return prefs.get(key, null)
    }
}