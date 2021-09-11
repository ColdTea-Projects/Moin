package de.coldtea.moin.ui.playlists

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import de.coldtea.moin.R
import de.coldtea.moin.databinding.FragmentPlaylistsBinding
import de.coldtea.moin.domain.model.playlist.Playlist
import de.coldtea.moin.ui.base.BaseFragment
import de.coldtea.moin.ui.services.DialogManager
import de.coldtea.moin.ui.services.FragmentNavigationService
import timber.log.Timber

class PlaylistsFragment : BaseFragment() {

    private val viewModel: PlaylistsViewModel by viewModels()
    var alertDialog: AlertDialog? = null
    var binding: FragmentPlaylistsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isActionBarVisible = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPlaylistsBinding.inflate(inflater, container, false).apply {
        initButtons()
    }.also {
        Timber.d("Moin --> onCreateView")
        binding = it
    }.root

    private fun FragmentPlaylistsBinding.initButtons() {
        sunny.setOnClickListener {
            try {
                FragmentNavigationService.addPlaylistFragmentToStack(Playlist.SUNNY, requireActivity())
            } catch (ex: IllegalArgumentException) {
                Timber.e("Moin --> $ex")
                showSomethingWentWrongDialog()
            }
        }

        cloudy.setOnClickListener {
            try {
                FragmentNavigationService.addPlaylistFragmentToStack(Playlist.CLOUDY, requireActivity())
            } catch (ex: IllegalArgumentException) {
                Timber.e("Moin --> $ex")
                showSomethingWentWrongDialog()
            }
        }

        rainy.setOnClickListener {
            try {
                FragmentNavigationService.addPlaylistFragmentToStack(Playlist.RAINY, requireActivity())
            } catch (ex: IllegalArgumentException) {
                Timber.e("Moin --> $ex")
                showSomethingWentWrongDialog()
            }
        }

        snowy.setOnClickListener {
            try {
                FragmentNavigationService.addPlaylistFragmentToStack(Playlist.SNOWY, requireActivity())
            } catch (ex: IllegalArgumentException) {
                Timber.e("Moin --> $ex")
                showSomethingWentWrongDialog()
            }
        }
    }

    private fun showSomethingWentWrongDialog() = try{
        alertDialog = DialogManager.buildDialog(
            requireActivity(),
            getString(R.string.something_went_wrong),
            getString(R.string.ok),
            { alertDialog?.dismiss() }
        )

        alertDialog?.show()
    }catch (ex: IllegalArgumentException){
        Timber.e("Moin:showSomethingWentWrongDialog --> $ex")
    }

    companion object {

    }

}