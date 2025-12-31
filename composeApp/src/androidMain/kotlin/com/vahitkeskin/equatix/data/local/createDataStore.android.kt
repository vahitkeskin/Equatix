package com.vahitkeskin.equatix.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences

// Bu context'i MainActivity.kt içinde onCreate altında set etmelisin:
// com.vahitkeskin.equatix.data.local.appContext = applicationContext
lateinit var appContext: Context

actual fun createDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
        produceFile = {
            // Android dosya sistemi
            appContext.filesDir.resolve("equatix.preferences_pb")
        }
    )
}