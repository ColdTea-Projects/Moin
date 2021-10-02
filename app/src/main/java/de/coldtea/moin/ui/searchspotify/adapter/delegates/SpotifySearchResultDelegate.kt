package de.coldtea.moin.ui.searchspotify.adapter.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ItemSearchResultsBinding
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultBundle

class SpotifySearchResultDelegate :
    AbsListItemAdapterDelegate<SpotifySearchResultBundle, SpotifySearchResultBundle, SpotifySearchResultDelegate.SpotifySearchResultViewHolder>() {

    override fun isForViewType(
        item: SpotifySearchResultBundle,
        items: MutableList<SpotifySearchResultBundle>,
        position: Int
    ): Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup): SpotifySearchResultViewHolder =
        SpotifySearchResultViewHolder(
            ItemSearchResultsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            parent.context
        )

    override fun onBindViewHolder(
        item: SpotifySearchResultBundle,
        holder: SpotifySearchResultViewHolder,
        payloads: MutableList<Any>
    ) = holder.bind(item)

    class SpotifySearchResultViewHolder(
        private val binding: ItemSearchResultsBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SpotifySearchResultBundle) {
            binding.searchItem = item.spotifySearchResultDelegateItem

            //TODO: Replace with lotti
            with(binding.play){
                val imageSource = if(item.playState) R.drawable.ic_baseline_pause_circle_filled_24 else R.drawable.ic_baseline_play_circle_outline_24
                setImageResource(imageSource)
            }

            binding.play.setOnClickListener {
                item.onClickPlay(item.id)
            }

            binding.mainContainer.setOnClickListener {
                item.onClickItem(item.id)
            }

            Glide.with(context)
                .load(item.spotifySearchResultDelegateItem.imageUrl)
                .placeholder(R.drawable.ic_baseline_library_music_24)
                .into(binding.albumCover)
        }
    }
}