package com.vahitkeskin.equatix.platform

import java.util.Locale

actual fun getSystemLanguageCode(): String {
    // Android tarafında Java kütüphanesine erişebiliriz
    return Locale.getDefault().language
}