package com.vahitkeskin.equatix.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberNotificationPermissionControl(
    onPermissionResult: (Boolean) -> Unit
): PermissionControl {
    // iOS tarafı için şimdilik basit bir yapı
    return remember {
        object : PermissionControl {
            override fun launchPermissionRequest() {
                // iOS izin kodu buraya gelecek
                onPermissionResult(true)
            }

            override fun openAppSettings() {
                // iOS ayarlarını açma kodu buraya
            }

            override fun hasPermission(): Boolean = true
        }
    }
}