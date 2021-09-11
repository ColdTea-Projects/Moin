package de.coldtea.moin.ui.services

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import de.coldtea.moin.R
import de.coldtea.moin.domain.model.playlist.Playlist
import de.coldtea.moin.ui.playlist.PlaylistFragment
import de.coldtea.moin.ui.playlist.PlaylistViewModel.Companion.PLAY_LIST_FRAGMENT_WEATHER_KEY
import de.coldtea.moin.ui.searchspotify.SearchSpotifyFragment

object FragmentNavigationService {

    @Throws(IllegalStateException::class)
    fun addPlaylistFragmentToStack(playlist: Playlist, activity: FragmentActivity?) = activity?.let{
        val playlistBundle = Bundle()
        playlistBundle.putString(PLAY_LIST_FRAGMENT_WEATHER_KEY, playlist.key)

        val playlistFragment = PlaylistFragment.getInstance(playlistBundle)

        it.supportFragmentManager
            .beginTransaction()
            .replace(R.id.host_fragment, playlistFragment)
            .addToBackStack(PlaylistFragment::class.java.canonicalName)
            .commit()
    }?: throw IllegalStateException("Activity for the current fragment not found!")

    @Throws(IllegalStateException::class)
    fun addSearchSpotifyFragmentToStack(playlist: Playlist, activity: FragmentActivity?) = activity?.let{
        val playlistBundle = Bundle()
        playlistBundle.putString(PLAY_LIST_FRAGMENT_WEATHER_KEY, playlist.key)

        val searchSpotifyFragment = SearchSpotifyFragment.getInstance(playlistBundle)

        it.supportFragmentManager
            .beginTransaction()
            .replace(R.id.host_fragment, searchSpotifyFragment)
            .addToBackStack(SearchSpotifyFragment::class.java.canonicalName)
            .commit()
    }?: throw IllegalStateException("Activity for the current fragment not found!")
}