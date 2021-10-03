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
import de.coldtea.moin.domain.model.extensions.toSong
import de.coldtea.moin.domain.model.playlist.Playlist
import de.coldtea.moin.ui.base.BaseFragment
import de.coldtea.moin.ui.playlist.PlaylistViewModel.Companion.PLAY_LIST_FRAGMENT_WEATHER_KEY
import de.coldtea.moin.ui.playlist.adapter.PlaylistAdapter
import de.coldtea.moin.ui.playlist.adapter.model.PlaylistBundle
import de.coldtea.moin.ui.playlist.adapter.model.PlaylistDelegateItem
import de.coldtea.moin.ui.searchspotify.SearchSpotifyActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class PlaylistFragment : BaseFragment() {

    private val viewModel: PlaylistViewModel by viewModel()
    private val playlistAdapter = PlaylistAdapter()

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

            requireActivity().title = playlist.name
            addSong.setOnClickListener {
                val intent = Intent(requireActivity(), SearchSpotifyActivity::class.java)
                intent.putExtra(PLAY_LIST_FRAGMENT_WEATHER_KEY, playlist.key)
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

        lifecycleScope.launch(Dispatchers.IO) {
            loadPlaylist()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        requireActivity().title = getString(R.string.app_name)
    }

    fun onDeleteClicked(id: Int){
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.deleteSong(id)
            loadPlaylist()
        }
    }

    private suspend fun loadPlaylist(){
        playlist?.let {
            val songs = viewModel.getSongsByPlaylist(
                it
            )

            playlistAdapter.items = songs.map { songEntity ->
                val song = songEntity.toSong()
                PlaylistBundle(
                    id = song.localId,
                    playlistDelegateItem = PlaylistDelegateItem(
                        imageUrl = song.imageUrl,
                        songName = song.name,
                        artistName = song.artistName
                    ),
                    onClickDelete = ::onDeleteClicked
                )
            }
        }
    }

    companion object {

        fun getInstance(bundle: Bundle) = PlaylistFragment().apply {
            arguments = bundle
        }
    }
}
