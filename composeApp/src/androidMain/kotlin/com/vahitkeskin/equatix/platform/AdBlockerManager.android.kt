package com.vahitkeskin.equatix.platform

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.vahitkeskin.equatix.EquatixApp

actual object AdBlockerManager {
    private val context: Context get() = EquatixApp.instance.applicationContext

    actual fun isPrivateDnsActive(): Boolean {
        return try {
            val mode = Settings.Global.getString(context.contentResolver, "private_dns_mode")
            // "hostname" means Private DNS provider is set (e.g., adguard)
            // "opportunistic" means Automatic
            // "off" means disabled
            mode == "hostname"
        } catch (e: Exception) {
            false
        }
    }

    actual fun openDnsSettings() {
        try {
            // Most devices have Private DNS under Wireless/Network settings
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general settings
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
