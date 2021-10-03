package de.coldtea.moin.ui.playlist

import androidx.lifecycle.ViewModel
import de.coldtea.moin.data.SongRepository
import de.coldtea.moin.domain.model.playlist.Playlist

class PlaylistViewModel(
    private val songRepository: SongRepository
): ViewModel(){

    suspend fun getSongsByPlaylist(playlist: Playlist) =
        songRepository.getSongsByPlaylist(playlist)

    suspend fun deleteSong(id:Int){
        songRepository.deleteSong(id)
    }

    companion object{
        const val PLAY_LIST_FRAGMENT_WEATHER_KEY = "play_list_fragment_weather_key"
    }
}