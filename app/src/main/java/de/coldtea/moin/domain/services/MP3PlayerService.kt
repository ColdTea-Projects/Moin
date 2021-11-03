package de.coldtea.moin.domain.services

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

class MP3PlayerService(private val applicationContext: Context, private val uri: Uri) {

    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(applicationContext, uri)
    }

    fun play() {
        if(!mediaPlayer.isPlaying) mediaPlayer.start()
    }

    fun stop() {
        if(mediaPlayer.isPlaying) mediaPlayer.stop()
    }

    companion object{
        const val MP3_MIME_TYPE = "audio/mpeg"
    }
}