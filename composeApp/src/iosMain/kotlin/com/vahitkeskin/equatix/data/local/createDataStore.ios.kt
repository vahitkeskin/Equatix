package com.vahitkeskin.equatix.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun createDataStore(): DataStore<Preferences> {
    // DÜZELTME: 'create' yerine 'createWithPath' kullanıyoruz.
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { // Snippet'a göre parametre adı produceFile
            val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            val path = requireNotNull(documentDirectory).path + "/equatix.preferences_pb"
            path.toPath()
        }
    )
}