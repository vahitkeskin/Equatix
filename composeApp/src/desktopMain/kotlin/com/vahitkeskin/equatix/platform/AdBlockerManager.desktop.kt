package com.vahitkeskin.equatix.platform

actual object AdBlockerManager {
    actual fun isPrivateDnsActive(): Boolean = false
    actual fun openDnsSettings() {}
}
