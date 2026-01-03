package com.vahitkeskin.equatix.platform

import android.content.Context
import android.media.MediaPlayer
import com.vahitkeskin.equatix.EquatixApp
import com.vahitkeskin.equatix.R // R.raw.background_music için gerekli

class AndroidMusicManager(private val context: Context) : MusicManager {

    private var mediaPlayer: MediaPlayer? = null

    override fun playLooping(volume: Float) {
        if (mediaPlayer == null) {
            // raw klasöründeki dosyayı yüklüyoruz
            mediaPlayer = MediaPlayer.create(context, R.raw.background_music)
        }

        mediaPlayer?.apply {
            isLooping = true // Şarkı bitince başa sar
            setVolume(volume, volume) // Sağ ve Sol kulaklık sesi (0.0f - 1.0f arası)
            if (!isPlaying) {
                start()
            }
        }
    }

    override fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause() // Tamamen durdurup kaynağı harcamamak için pause veya stop
            }
        }
    }

    override fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}

actual fun createMusicManager(): MusicManager {
    return AndroidMusicManager(EquatixApp.instance)
}