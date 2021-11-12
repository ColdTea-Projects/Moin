package de.coldtea.moin.domain.services

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri

class MP3PlayerService(private val applicationContext: Context, private val uri: Uri) {

    private val mediaPlayer: MediaPlayer by lazy {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        MediaPlayer().apply {
            setDataSource(applicationContext, uri)
            setAudioAttributes(audioAttributes)
            prepare()
        }
    }

    fun play() {
        if(!mediaPlayer.isPlaying) mediaPlayer.start()
    }

    fun stop() {
        mediaPlayer.stop()
    }

    companion object{
        const val MP3_MIME_TYPE = "audio/mpeg"
    }
}