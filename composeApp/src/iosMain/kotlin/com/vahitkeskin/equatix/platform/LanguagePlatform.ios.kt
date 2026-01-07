package com.vahitkeskin.equatix.platform

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getSystemLanguageCode(): String {
    // iOS tarafında Foundation kütüphanesini kullanıyoruz
    return NSLocale.currentLocale.languageCode
}