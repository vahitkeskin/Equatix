package com.vahitkeskin.equatix

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform