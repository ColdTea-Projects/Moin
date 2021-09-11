package de.coldtea.moin.ui.searchspotify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import de.coldtea.moin.databinding.FragmentSearchSpotifyBinding
import de.coldtea.moin.domain.model.playlist.Playlist
import de.coldtea.moin.ui.base.BaseFragment
import de.coldtea.moin.ui.playlist.PlaylistViewModel.Companion.PLAY_LIST_FRAGMENT_WEATHER_KEY
import timber.log.Timber

class SearchSpotifyFragment : BaseFragment() {

    private val viewModel: SearchSpotifyViewModel by viewModels()
    var binding: FragmentSearchSpotifyBinding? = null

    val playlist: Playlist?
        get() = Playlist.values().find { it.key == arguments?.getString(PLAY_LIST_FRAGMENT_WEATHER_KEY).orEmpty() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentSearchSpotifyBinding.inflate(inflater, container, false).apply {
        initUIItems()
    }.also {
        Timber.d("Moin --> onCreateView")
        binding = it
    }.root

    private fun FragmentSearchSpotifyBinding.initUIItems() {
        weatherText.text = playlist?.name
    }

    companion object {

        fun getInstance(bundle: Bundle) = SearchSpotifyFragment().apply {
            arguments = bundle
        }
    }
}
