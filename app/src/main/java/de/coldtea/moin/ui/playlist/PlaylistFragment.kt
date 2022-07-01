package de.coldtea.moin.ui.playlist

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.coldtea.moin.R
import de.coldtea.moin.databinding.FragmentPlaylistBinding
import de.coldtea.moin.domain.model.extensions.getPlaylistBundle
import de.coldtea.moin.domain.model.playlist.PlaylistName
import de.coldtea.moin.domain.model.playlist.Song
import de.coldtea.moin.domain.model.playlist.getTitle
import de.coldtea.moin.domain.services.FilePickerConverter
import de.coldtea.moin.domain.services.MP3PlayerService
import de.coldtea.moin.ui.base.BaseFragment
import de.coldtea.moin.ui.playlist.PlaylistViewModel.Companion.PLAY_LIST_FRAGMENT_WEATHER_KEY
import de.coldtea.moin.ui.playlist.adapter.PlaylistAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class PlaylistFragment : BaseFragment() {

    private val viewModel: PlaylistViewModel by viewModel()
    private val playlistAdapter = PlaylistAdapter()

    var binding: FragmentPlaylistBinding? = null

    private var registerActivityResult: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.playlistName = PlaylistName
            .values()
            .find {
                it.key == arguments?.getString(PLAY_LIST_FRAGMENT_WEATHER_KEY).orEmpty()
            }

        registerActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onFilePickerActivityResult(result)
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

            requireActivity().title = playlistName.getTitle()

            addLocalMusic.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_OPEN_DOCUMENT
                intent.type = MP3PlayerService.MP3_MIME_TYPE

                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                registerActivityResult?.launch(intent)
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
        }.also {
            with(binding){
                this?:return@with

                songEmptyListMessage.isVisible = it.isEmpty()
                if(it.size > (playlistRecyclerView.adapter?.itemCount ?: Int.MAX_VALUE)){
                    playlistRecyclerView.smoothScrollToPosition(it.size - 1)
                }
            }
        }

    }

    fun refreshPlaylist() = lifecycleScope.launch(Dispatchers.IO) {
        viewModel.refreshPlaylist()
    }

    fun onDeleteClicked(id: Int) = lifecycleScope.launch(Dispatchers.IO){
        viewModel.deleteSong(id)
    }

    fun onFilePickerActivityResult(result: ActivityResult) {
        if(result.resultCode != RESULT_OK) return
        val uri = Uri.parse(result.data?.data.toString())?:return
        val mP3Object = FilePickerConverter.getMP3Object(requireActivity().contentResolver, uri)

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.addMP3(mP3Object)
        }
    }

    companion object {

        fun getInstance(bundle: Bundle) = PlaylistFragment().apply {
            arguments = bundle
        }
    }
}
