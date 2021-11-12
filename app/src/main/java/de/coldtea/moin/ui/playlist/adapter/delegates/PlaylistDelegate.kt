package de.coldtea.moin.ui.playlist.adapter.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ItemPlaylistBinding
import de.coldtea.moin.ui.playlist.adapter.model.PlaylistBundle

class PlaylistDelegate :
    AbsListItemAdapterDelegate<PlaylistBundle, PlaylistBundle, PlaylistDelegate.PlaylistViewHolder>() {

    override fun isForViewType(
        item: PlaylistBundle,
        items: MutableList<PlaylistBundle>,
        position: Int
    ): Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup): PlaylistViewHolder =
        PlaylistViewHolder(
            ItemPlaylistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            parent.context
        )

    override fun onBindViewHolder(
        item: PlaylistBundle,
        holder: PlaylistViewHolder,
        payloads: MutableList<Any>
    ) = holder.bind(item)

    class PlaylistViewHolder(
        private val binding: ItemPlaylistBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaylistBundle) {
            binding.songName.text = item.playlistDelegateItem.songName
            binding.artistName.text = item.playlistDelegateItem.artistName

            binding.delete.setOnClickListener {
                item.onClickDelete(item.id)
            }

            Glide.with(context)
                .load(item.playlistDelegateItem.imageUrl)
                .placeholder(R.drawable.ic_baseline_library_music_24)
                .into(binding.albumCover)
        }
    }
}