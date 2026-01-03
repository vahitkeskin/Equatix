package com.vahitkeskin.equatix.domain.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val KEY_SOUND = booleanPreferencesKey("is_sound_on")
        private val KEY_VIBRATION = booleanPreferencesKey("is_vibration_on")
        private val IS_TUTORIAL_SEEN = booleanPreferencesKey("is_tutorial_seen")
    }

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

    val themeConfig: Flow<AppThemeConfig> = dataStore.data.map { preferences ->
        val ordinal =
            preferences[intPreferencesKey("app_theme")] ?: AppThemeConfig.DARK.ordinal
        AppThemeConfig.values().getOrElse(ordinal) { AppThemeConfig.DARK }
    }

    suspend fun setTheme(config: AppThemeConfig) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey("app_theme")] = config.ordinal
        }
    }

    // Okuma
    val isTutorialSeen: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_TUTORIAL_SEEN] ?: false
        }

    // Yazma (Görüldü olarak işaretle)
    suspend fun setTutorialSeen() {
        dataStore.edit { preferences ->
            preferences[IS_TUTORIAL_SEEN] = true
        }
    }
}