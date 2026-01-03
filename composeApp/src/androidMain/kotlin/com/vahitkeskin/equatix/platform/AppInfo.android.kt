package com.vahitkeskin.equatix.platform

import com.vahitkeskin.equatix.EquatixApp

actual fun getAppVersion(): String {
    return try {
        val context = EquatixApp.instance
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "1.0"
    } catch (e: Exception) {
        "1.0"
    }
}