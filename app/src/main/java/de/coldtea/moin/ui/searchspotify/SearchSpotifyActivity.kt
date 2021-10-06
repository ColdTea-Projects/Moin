package de.coldtea.moin.ui.searchspotify

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.coldtea.moin.R
import de.coldtea.moin.data.network.spotify.model.TokenResponse
import de.coldtea.moin.databinding.ActivitySearchSpotifyBinding
import de.coldtea.moin.domain.model.extensions.getSearchResultBundle
import de.coldtea.moin.domain.model.playlist.PlaylistName
import de.coldtea.moin.domain.model.spotify.SearchResult
import de.coldtea.moin.domain.services.SpotifyService
import de.coldtea.moin.extensions.convertToAuthorizationResponse
import de.coldtea.moin.services.model.*
import de.coldtea.moin.ui.playlist.PlaylistViewModel
import de.coldtea.moin.ui.searchspotify.adapter.SpotifySearchAdapter
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultBundle
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultDelegateItem
import de.coldtea.moin.ui.services.DialogManager
import kotlinx.android.synthetic.main.activity_search_spotify.*
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchSpotifyActivity : AppCompatActivity() {

    private val viewModel: SearchSpotifyViewModel by viewModel()
    private var searchResultAdapter = SpotifySearchAdapter()

    var binding: ActivitySearchSpotifyBinding? = null
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_spotify)

        viewModel.playlist = PlaylistName.values()
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

            addOnScrollListener(SearchResultScrollListener())
        }

        this?.searchInput?.doOnTextChanged { _, _, _, count ->

            pauseTrackAndCleanPlaylist()
            when {
                count > 0 && viewModel.refreshTokenExist -> viewModel.getAccessTokenByRefreshToken()
                count > 0 && !viewModel.refreshTokenExist -> startActivity(viewModel.getAuthorizationIntent())
                count == 0 -> clearPlaylist()
            }

            binding?.setClearButtonVisibility(count > 0)
        }

        this?.clearText?.setOnClickListener {
            this.searchInput.text?.clear()
        }
    }

    private fun initSpotify() = lifecycleScope.launchWhenResumed {
        viewModel.spotifyState.collect {
            when (it) {
                ConnectionSuccess -> onConnectionSuccess()
                ConnectionFailed -> onConnectionFailed()
                is AccessTokenReceived -> onActivationTokenReceived(it.tokenResponse)
                is SearchResultReceived -> onSearchResultReceived(it.searchResult)
                else -> Timber.w("Received illegal spotify state")
            }
        }
    }

    private fun onConnectionSuccess(){
        binding?.searchInput?.isEnabled = true
        binding?.searchInput?.requestFocus()
        openKeyboard()
    }

    private fun onConnectionFailed(){
        alertDialog = DialogManager.buildDialog(
            this@SearchSpotifyActivity,
            getString(R.string.spotify_connection_failed),
            getString(R.string.ok),
            {
                alertDialog?.dismiss()
                finish()
            }
        )

        alertDialog?.show()
    }

    private fun onActivationTokenReceived(tokenResponse: TokenResponse?) {
        val keyword = binding?.searchInput?.text.toString()
        if (keyword.isNotEmpty() && tokenResponse?.accessToken != null) {
            viewModel.search(tokenResponse.accessToken, keyword)
        }
    }

    private fun onSearchResultReceived(searchResult: SearchResult?){
        searchResultAdapter.items = searchResult
            ?.getSearchResultBundle(::onPlayClicked, ::onItemClicked)
    }

    private fun onPlayClicked(id: String) {
        val clickedItem = searchResultAdapter.items.first { it.id == id }

        if (clickedItem.playState) {
            pauseTrackAndCleanPlaylist()
        } else {
            playTrackAndUpdateList(id)
        }

        closeKeyboard()
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

    private fun closeKeyboard(){
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun openKeyboard(){
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(currentFocus, 0)
    }

    private fun clearPlaylist(){
        searchResultAdapter.items = listOf()
    }

    private fun ActivitySearchSpotifyBinding.setClearButtonVisibility(isVisible: Boolean) = if(isVisible){
        searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0)
    }else{
        searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_search_24,0)
    }.also {
        clearText.isVisible = isVisible
    }

    inner class SearchResultScrollListener : RecyclerView.OnScrollListener(){

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            closeKeyboard()
        }
    }
}
