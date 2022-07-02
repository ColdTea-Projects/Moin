package de.coldtea.moin.domain.services

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import okio.IOException
import timber.log.Timber

class MP3PlayerService(private val applicationContext: Context) {

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    fun play(uri: Uri): Boolean = try {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        mediaPlayer.apply {
            Timber.i("Moin --> MediaPlayer creation uri : $uri")
            setDataSource(applicationContext, uri)
            setAudioAttributes(audioAttributes)
            prepare()
        }

         mediaPlayer.start()
        true
    } catch (e: IOException) {
        Timber.e(e, "Moin --> MediaPlayer play exception")
        false
    }

    fun stop() {
        mediaPlayer.stop()
        mediaPlayer.reset()
    }

    companion object {
        const val MP3_MIME_TYPE = "audio/mpeg"
    }
}