package com.vahitkeskin.equatix.domain.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    private val KEY_SOUND = booleanPreferencesKey("is_sound_on")
    private val KEY_VIBRATION = booleanPreferencesKey("is_vibration_on")

    // Ses Ayarı Okuma (Varsayılan: True)
    val isSoundOn: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_SOUND] ?: true
    }

    // Titreşim Ayarı Okuma (Varsayılan: True)
    val isVibrationOn: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_VIBRATION] ?: true
    }

    // Ses Ayarı Değiştirme
    suspend fun setSound(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_SOUND] = enabled
        }
    }

    // Titreşim Ayarı Değiştirme
    suspend fun setVibration(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_VIBRATION] = enabled
        }
    }
}