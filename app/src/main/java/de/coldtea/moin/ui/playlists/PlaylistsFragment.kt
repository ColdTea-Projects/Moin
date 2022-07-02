package de.coldtea.moin.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import de.coldtea.moin.R
import de.coldtea.moin.databinding.FragmentPlaylistsBinding
import de.coldtea.moin.domain.model.playlist.PlaylistName
import de.coldtea.moin.domain.model.playlist.getIcon
import de.coldtea.moin.domain.model.playlist.getTitle
import de.coldtea.moin.ui.base.BaseFragment
import de.coldtea.moin.ui.services.DialogManager
import de.coldtea.moin.ui.services.FragmentNavigationService
import timber.log.Timber

class PlaylistsFragment : BaseFragment() {

    private val viewModel: PlaylistsViewModel by viewModels()
    var binding: FragmentPlaylistsBinding? = null

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
        sunnyIcon.setImageResource(PlaylistName.SUNNY.getIcon())
        sunnyText.text = PlaylistName.SUNNY.getTitle()

        sunny.setOnClickListener {
            try {
                FragmentNavigationService.addPlaylistFragmentToStack(PlaylistName.SUNNY, requireActivity())
            } catch (ex: IllegalArgumentException) {
                Timber.e("Moin --> $ex")
                showSomethingWentWrongDialog()
            }
        }

        cloudyIcon.setImageResource(PlaylistName.CLOUDY.getIcon())
        cloudyText.text = PlaylistName.CLOUDY.getTitle()

        cloudy.setOnClickListener {
            try {
                FragmentNavigationService.addPlaylistFragmentToStack(PlaylistName.CLOUDY, requireActivity())
            } catch (ex: IllegalArgumentException) {
                Timber.e("Moin --> $ex")
                showSomethingWentWrongDialog()
            }
        }

        rainyIcon.setImageResource(PlaylistName.RAINY.getIcon())
        rainyText.text = PlaylistName.RAINY.getTitle()

        rainy.setOnClickListener {
            try {
                FragmentNavigationService.addPlaylistFragmentToStack(PlaylistName.RAINY, requireActivity())
            } catch (ex: IllegalArgumentException) {
                Timber.e("Moin --> $ex")
                showSomethingWentWrongDialog()
            }
        }

        snowyIcon.setImageResource(PlaylistName.SNOWY.getIcon())
        snowyText.text = PlaylistName.SNOWY.getTitle()
        snowy.setOnClickListener {
            try {
                FragmentNavigationService.addPlaylistFragmentToStack(PlaylistName.SNOWY, requireActivity())
            } catch (ex: IllegalArgumentException) {
                Timber.e("Moin --> $ex")
                showSomethingWentWrongDialog()
            }
        }
    }

    private fun showSomethingWentWrongDialog() = try{
        DialogManager.buildDialog(
            activity = requireActivity(),
            message = getString(R.string.something_went_wrong),
            positiveText = getString(R.string.ok),
            positiveAction = { dialogInterface ->  dialogInterface?.dismiss() }
        ).show()
    }catch (ex: IllegalArgumentException){
        Timber.e("Moin:showSomethingWentWrongDialog --> $ex")
    }

    companion object {

    }

}