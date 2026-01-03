package com.vahitkeskin.equatix.platform

class JvmMusicManager : MusicManager {
    override fun playLooping(volume: Float) { /* Masaüstü kodları ilerde eklenebilir */ }
    override fun stop() {}
    override fun setVolume(volume: Float) {}
    override fun isPlaying(): Boolean = false
}

actual fun createMusicManager(): MusicManager {
    return JvmMusicManager()
}