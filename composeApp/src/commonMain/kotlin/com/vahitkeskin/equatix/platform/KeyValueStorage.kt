package com.vahitkeskin.equatix.platform

expect class KeyValueStorage() {
    fun saveString(key: String, value: String)
    fun getString(key: String): String?

    // --- BUNLARI EKLE ---
    fun saveBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
}