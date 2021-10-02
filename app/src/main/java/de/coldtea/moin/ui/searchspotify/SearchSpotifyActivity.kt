package de.coldtea.moin.ui.searchspotify

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
import de.coldtea.moin.ui.playlist.PlaylistFragment.Companion.PLAYLIST_KEY
import de.coldtea.moin.ui.searchspotify.adapter.SpotifySearchAdapter
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchSpotifyActivity : AppCompatActivity() {

    private val viewModel: SearchSpotifyViewModel by viewModel()
    private var searchResultAdapter = SpotifySearchAdapter()
    var binding: ActivitySearchSpotifyBinding? = null

    private var playlist: Playlist? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_spotify)

        playlist = Playlist.values()
            .find { it.key == savedInstanceState?.getString(PLAYLIST_KEY) }

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
            startActivity(viewModel.getAuthorizationIntent())
        }
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
            when {
                count > 0 && viewModel.refreshTokenExist -> viewModel.getAccessTokenByRefreshToken()
                count > 0 && !viewModel.refreshTokenExist -> startActivity(viewModel.getAuthorizationIntent())
            }
        }
    }

    private fun initSpotify() = lifecycleScope.launchWhenResumed {
        viewModel.spotifyState.collect {
            when (it) {
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

    fun onPlayClicked(id: String){

    }

    fun onItemClicked(id: String){

    }
}
