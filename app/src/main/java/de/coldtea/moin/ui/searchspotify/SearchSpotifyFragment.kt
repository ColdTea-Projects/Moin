package de.coldtea.moin.ui.searchspotify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.coldtea.moin.R
import de.coldtea.moin.databinding.FragmentSearchSpotifyBinding
import de.coldtea.moin.domain.model.playlist.Playlist
import de.coldtea.moin.ui.base.BaseFragment
import de.coldtea.moin.ui.playlist.PlaylistViewModel.Companion.PLAY_LIST_FRAGMENT_WEATHER_KEY
import de.coldtea.moin.ui.searchspotify.adapter.SpotifySearchAdapter
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultDelegateItem
import timber.log.Timber

class SearchSpotifyFragment : BaseFragment() {

    private val viewModel: SearchSpotifyViewModel by viewModels()
    private var searchResultAdapter = SpotifySearchAdapter()
    var binding: FragmentSearchSpotifyBinding? = null

    val playlist: Playlist?
        get() = Playlist.values().find { it.key == arguments?.getString(PLAY_LIST_FRAGMENT_WEATHER_KEY).orEmpty() }

    override fun onResume() {
        super.onResume()

        isActionBarVisible = false
    }

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
        searchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            val itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            AppCompatResources.getDrawable(requireContext(), R.drawable.divider_alarm_list)?.let { itemDecoration.setDrawable(it) }

            if (itemDecorationCount == 0) addItemDecoration(itemDecoration)

            adapter = searchResultAdapter
        }

        searchResultAdapter.items = listOf(
            SpotifySearchResultDelegateItem(1, "https://m.media-amazon.com/images/I/31s14C1mfBL._AC_.jpg", "Dark Side of the Moon","Pink Floyd"),
            SpotifySearchResultDelegateItem(2, "https://d8mkdcmng3.imgix.net/39c1/home-and-garden-home-decor-signs-and-wall-art-nirvana-smiley-12-inch-album-cover-framed-print.jpg?auto=format&bg=0FFF&fit=fill&h=600&q=100&w=600&s=3c4c9eb861366f3c70cd4347d0e02b93", "Smells Like a Teen Spirit","Nirvana"),
            SpotifySearchResultDelegateItem(3, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSu5kZWyatlmbZTS0o63L5abm9SF11SuzB8ng&usqp=CAU", "Hot Space","Queen")
        )
    }

    companion object {

        fun getInstance(bundle: Bundle) = SearchSpotifyFragment().apply {
            arguments = bundle
        }
    }
}
