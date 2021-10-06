package de.coldtea.moin.ui.playlist

import androidx.lifecycle.ViewModel
import de.coldtea.moin.data.SongRepository
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

    companion object{
        const val PLAY_LIST_FRAGMENT_WEATHER_KEY = "play_list_fragment_weather_key"
    }
}