package com.vahitkeskin.equatix.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

// Bu 'expect' tanımı, diğer platformlardaki (android, ios, desktop)
// 'actual' fonksiyonlarla eşleşmek zorundadır.
expect fun createDataStore(): DataStore<Preferences>