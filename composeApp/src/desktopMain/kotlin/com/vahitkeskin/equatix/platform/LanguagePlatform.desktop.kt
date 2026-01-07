package com.vahitkeskin.equatix.platform

import java.util.Locale

actual fun getSystemLanguageCode(): String {
    // Desktop (JVM) tarafında da standart Java kütüphanesini kullanabiliriz
    return Locale.getDefault().language
}