package de.coldtea.moin.ui.searchspotify

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ActivitySearchSpotifyBinding
import de.coldtea.moin.domain.model.extensions.getSearchResultBundle
import de.coldtea.moin.domain.model.playlist.Playlist
import de.coldtea.moin.domain.services.SpotifyService
import de.coldtea.moin.extensions.convertToAuthorizationResponse
import de.coldtea.moin.services.model.*
import de.coldtea.moin.ui.playlist.PlaylistViewModel
import de.coldtea.moin.ui.searchspotify.adapter.SpotifySearchAdapter
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultBundle
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultDelegateItem
import de.coldtea.moin.ui.services.DialogManager
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchSpotifyActivity : AppCompatActivity() {

    private val viewModel: SearchSpotifyViewModel by viewModel()
    private var searchResultAdapter = SpotifySearchAdapter()

    var binding: ActivitySearchSpotifyBinding? = null
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_spotify)

        viewModel.playlist = Playlist.values()
            .find {
                it.key == intent.getStringExtra(PlaylistViewModel.PLAY_LIST_FRAGMENT_WEATHER_KEY)
            }

        initUIItems()
        initSpotify()

        val data: Uri? = intent.data

        if (data != null && !TextUtils.isEmpty(data.scheme)) {
            if (SpotifyService.REDIRECT_URI_ROOT == data.scheme) {
                val authorizationResponse = data.toString().convertToAuthorizationResponse()
                viewModel.registerAuthorizationCode(authorizationResponse)
                viewModel.getAccessToken(authorizationResponse?.code)
            }
        } else if (!viewModel.refreshTokenExist) {
            viewModel.backUpPlaylist()
            startActivity(viewModel.getAuthorizationIntent())
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.connectSpotify(this)
    }

    override fun onPause() {
        super.onPause()
        pauseTrackAndCleanPlaylist()
    }

    override fun onStop() {
        super.onStop()
        viewModel.disconnectSpotify()
    }

    private fun initUIItems() = with(binding) {
        this?.searchResultsRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@SearchSpotifyActivity)
            val itemDecoration =
                DividerItemDecoration(this@SearchSpotifyActivity, DividerItemDecoration.VERTICAL)
            AppCompatResources.getDrawable(
                this@SearchSpotifyActivity,
                R.drawable.divider_alarm_list
            )
                ?.let { itemDecoration.setDrawable(it) }

            if (itemDecorationCount == 0) addItemDecoration(itemDecoration)

            adapter = searchResultAdapter
        }

        this?.searchInput?.doOnTextChanged { _, _, _, count ->

            pauseTrackAndCleanPlaylist()
            when {
                count > 0 && viewModel.refreshTokenExist -> viewModel.getAccessTokenByRefreshToken()
                count > 0 && !viewModel.refreshTokenExist -> startActivity(viewModel.getAuthorizationIntent())
            }
        }
    }

    private fun initSpotify() = lifecycleScope.launchWhenResumed {
        viewModel.spotifyState.collect {
            when (it) {
                ConnectionSuccess -> {
                    binding?.searchInput?.isEnabled = true
                }
                ConnectionFailed -> {
                    alertDialog = DialogManager.buildDialog(
                        this@SearchSpotifyActivity,
                        getString(R.string.spotify_connection_failed),
                        getString(R.string.ok),
                        { alertDialog?.dismiss() }
                    )

                    alertDialog?.show()
                }
                is AccessTokenReceived -> {
                    val keyword = binding?.searchInput?.text.toString()
                    if (keyword.isNotEmpty() && it.tokenResponse?.accessToken != null) {
                        viewModel.search(it.tokenResponse.accessToken, keyword)
                    }
                }
                is SearchResultReceived -> {
                    searchResultAdapter.items = it.searchResult
                        ?.getSearchResultBundle(::onPlayClicked, ::onItemClicked)
                }
            }
        }
    }

    private fun onPlayClicked(id: String) {
        val clickedItem = searchResultAdapter.items.first { it.id == id }

        if (clickedItem.playState) {
            pauseTrackAndCleanPlaylist()
        } else {
            playTrackAndUpdateList(id)
        }
    }

    private fun onItemClicked(id: String) =
        viewModel.addSong(
            searchResultAdapter.items.first { it.id == id }
        ).also {
            finish()
        }

    private fun pauseTrackAndCleanPlaylist() {
        viewModel.pauseTrack()
        searchResultAdapter.items = searchResultAdapter.items.listWithNewPlayingItem(null)
    }

    private fun playTrackAndUpdateList(trackId: String) {
        viewModel.playTrack(trackId)
        searchResultAdapter.items = searchResultAdapter.items.listWithNewPlayingItem(trackId)
    }

    private fun List<SpotifySearchResultBundle>.listWithNewPlayingItem(playingItemId: String?) =
        this.map {
            SpotifySearchResultBundle(
                id = it.id,
                spotifySearchResultDelegateItem = SpotifySearchResultDelegateItem(
                    imageUrl = it.spotifySearchResultDelegateItem.imageUrl,
                    songName = it.spotifySearchResultDelegateItem.songName,
                    artistName = it.spotifySearchResultDelegateItem.artistName
                ),

                onClickPlay = ::onPlayClicked,
                onClickItem = ::onItemClicked,
                playState = it.id == playingItemId
            )
        }
}
