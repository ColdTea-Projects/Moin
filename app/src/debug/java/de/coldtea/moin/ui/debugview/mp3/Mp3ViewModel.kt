package de.coldtea.moin.ui.debugview.mp3

import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import de.coldtea.moin.domain.services.MP3PlayerService

class Mp3ViewModel : ViewModel() {
    var mp3Uri: Uri? = null
    var mediaPlayer: MediaPlayer? = null

    var mP3PlayerService: MP3PlayerService? = null
}
