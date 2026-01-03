package com.vahitkeskin.equatix.ui.utils

import androidx.compose.runtime.Composable

/**
 * Android ve iOS'un farklı davranan izin mantığını tek bir çatı altında topluyoruz.
 */
interface PermissionControl {
    fun launchPermissionRequest()
    fun openAppSettings()
    fun hasPermission(): Boolean
}

/**
 * Bu fonksiyon 'expect' ile işaretlendi. Yani her platform (Android/iOS)
 * kendi 'actual' uygulamasını yazmak zorunda.
 */
@Composable
expect fun rememberNotificationPermissionControl(
    onPermissionResult: (Boolean) -> Unit
): PermissionControl