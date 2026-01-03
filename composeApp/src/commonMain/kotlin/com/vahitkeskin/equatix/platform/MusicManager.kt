package com.vahitkeskin.equatix.platform

interface MusicManager {
    fun playLooping(volume: Float = 0.3f) // Varsayılan ses kısık (Loş)
    fun stop()
    fun setVolume(volume: Float)
    fun isPlaying(): Boolean
}

//expect fun createMusicManager(): MusicManager