package de.coldtea.moin.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import de.coldtea.moin.databinding.FragmentPlaylistBinding
import de.coldtea.moin.domain.model.playlist.Playlist
import de.coldtea.moin.ui.base.BaseFragment
import de.coldtea.moin.ui.playlist.PlaylistViewModel.Companion.PLAY_LIST_FRAGMENT_WEATHER_KEY
import de.coldtea.moin.ui.searchspotify.SearchSpotifyActivity
import timber.log.Timber

class PlaylistFragment : BaseFragment() {

    private val viewModel: PlaylistViewModel by viewModels()
    var binding: FragmentPlaylistBinding? = null

    val playlist: Playlist?
        get() = Playlist.values().find { it.key == arguments?.getString(PLAY_LIST_FRAGMENT_WEATHER_KEY).orEmpty() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPlaylistBinding.inflate(inflater, container, false).apply {
        initUIItems()
    }.also {
        Timber.d("Moin --> onCreateView")
        binding = it
    }.root

    private fun FragmentPlaylistBinding.initUIItems() {
        playlist?.let { playlist ->
            weatherText.text = playlist.key
            addSong.setOnClickListener {
                val intent = Intent(requireActivity(), SearchSpotifyActivity::class.java)
                intent.putExtra(PLAYLIST_KEY, playlist.key)
                startActivity(intent)
//                FragmentNavigationService.addSearchSpotifyFragmentToStack(
//                    playlist,
//                    requireActivity()
//                )
            }
        }
    }

    companion object {
        const val PLAYLIST_KEY = "playlist_key"

        fun getInstance(bundle: Bundle) = PlaylistFragment().apply {
            arguments = bundle
        }
    }
}
