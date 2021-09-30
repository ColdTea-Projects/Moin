package de.coldtea.moin.ui.searchspotify.adapter.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ItemSearchResultsBinding
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultDelegateItem

class SpotifySearchResultDelegate :
    AbsListItemAdapterDelegate<SpotifySearchResultDelegateItem, SpotifySearchResultDelegateItem, SpotifySearchResultDelegate.SpotifySearchResultViewHolder>() {

    override fun isForViewType(
        item: SpotifySearchResultDelegateItem,
        items: MutableList<SpotifySearchResultDelegateItem>,
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
        item: SpotifySearchResultDelegateItem,
        holder: SpotifySearchResultViewHolder,
        payloads: MutableList<Any>
    ) = holder.bind(item)

    class SpotifySearchResultViewHolder(
        private val binding: ItemSearchResultsBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SpotifySearchResultDelegateItem) {
            binding.searchItem = item

            Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_baseline_library_music_24)
                .into(binding.albumCover)
        }
    }
}