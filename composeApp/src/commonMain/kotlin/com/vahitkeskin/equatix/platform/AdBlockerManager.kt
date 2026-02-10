package com.vahitkeskin.equatix.platform

expect object AdBlockerManager {
    fun isPrivateDnsActive(): Boolean
    fun openDnsSettings()
}
