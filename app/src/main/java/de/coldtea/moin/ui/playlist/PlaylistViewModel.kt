package de.coldtea.moin.ui.playlist

import androidx.lifecycle.ViewModel
import de.coldtea.moin.data.SongRepository
import de.coldtea.moin.domain.model.mp3.MP3Object
import de.coldtea.moin.domain.model.playlist.MediaType
import de.coldtea.moin.domain.model.playlist.PlaylistName
import de.coldtea.moin.domain.model.playlist.Song
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class PlaylistViewModel(
    private val songRepository: SongRepository
): ViewModel(){
    var playlistName: PlaylistName? = null

    private val _playlist = MutableSharedFlow<List<Song>>()
    val playlist: SharedFlow<List<Song>> = _playlist

    suspend fun refreshPlaylist() = playlistName?.let{ playlistName ->
        val songs = songRepository.getSongsByPlaylist(playlistName)
        _playlist.emit(songs)
    }

    suspend fun deleteSong(id:Int){
        songRepository.deleteSong(id)
        refreshPlaylist()
    }

    suspend fun addMP3(mP3Object: MP3Object){
        val selectedMP3 = Song(
            localId = -1,
            trackId = "",
            name = mP3Object.displayName,
            artistName = "",
            imageUrl = "",
            mediaType = MediaType.MP3.ordinal,
            source = mP3Object.uri.toString(),
            playlist = playlistName?.ordinal?:-1
        )

        songRepository.addSong(selectedMP3)
    }

    companion object{
        const val PLAY_LIST_FRAGMENT_WEATHER_KEY = "play_list_fragment_weather_key"
    }
}