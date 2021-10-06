package de.coldtea.moin.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.coldtea.moin.R
import de.coldtea.moin.databinding.FragmentPlaylistBinding
import de.coldtea.moin.domain.model.extensions.getPlaylistBundle
import de.coldtea.moin.domain.model.playlist.PlaylistName
import de.coldtea.moin.domain.model.playlist.Song
import de.coldtea.moin.ui.base.BaseFragment
import de.coldtea.moin.ui.playlist.PlaylistViewModel.Companion.PLAY_LIST_FRAGMENT_WEATHER_KEY
import de.coldtea.moin.ui.playlist.adapter.PlaylistAdapter
import de.coldtea.moin.ui.searchspotify.SearchSpotifyActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class PlaylistFragment : BaseFragment() {

    private val viewModel: PlaylistViewModel by viewModel()
    private val playlistAdapter = PlaylistAdapter()

    var binding: FragmentPlaylistBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.playlistName = PlaylistName
            .values()
            .find {
                it.key == arguments?.getString(PLAY_LIST_FRAGMENT_WEATHER_KEY).orEmpty()
            }

        observePlaylist()
    }

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
        viewModel.playlistName?.let { playlistName ->

            requireActivity().title = playlistName.name
            addSong.setOnClickListener {
                val intent = Intent(requireActivity(), SearchSpotifyActivity::class.java)
                intent.putExtra(PLAY_LIST_FRAGMENT_WEATHER_KEY, playlistName.key)
                startActivity(intent)
            }
        }

        playlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            val itemDecoration =
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.divider_alarm_list
            )
                ?.let { itemDecoration.setDrawable(it) }

            if (itemDecorationCount == 0) addItemDecoration(itemDecoration)

            adapter = playlistAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        refreshPlaylist()
    }

    override fun onDestroy() {
        super.onDestroy()

        requireActivity().title = getString(R.string.app_name)
    }

    fun observePlaylist() = lifecycleScope.launchWhenCreated {
        viewModel.playlist.collect { songs ->
            onPlaylistReceived(songs)
        }
    }

    fun onPlaylistReceived(songs: List<Song>){
        playlistAdapter.items = songs.map {
            it.getPlaylistBundle (::onDeleteClicked)
        }
    }

    fun refreshPlaylist() = lifecycleScope.launch(Dispatchers.IO) {
        viewModel.refreshPlaylist()
    }

    fun onDeleteClicked(id: Int) = lifecycleScope.launch(Dispatchers.IO){
        viewModel.deleteSong(id)
    }

    companion object {

        fun getInstance(bundle: Bundle) = PlaylistFragment().apply {
            arguments = bundle
        }
    }
}
