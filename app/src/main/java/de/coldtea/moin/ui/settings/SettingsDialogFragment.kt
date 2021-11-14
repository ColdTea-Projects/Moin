package de.coldtea.moin.ui.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.coldtea.moin.R
import de.coldtea.moin.databinding.DialogFragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SettingsDialogFragment : BottomSheetDialogFragment() {
    private val viewModel: SettingsViewModel by viewModel()
    private var binding: DialogFragmentSettingsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DialogFragmentSettingsBinding.inflate(inflater, container, false).apply {
        initUIItems()
    }.also {
        Timber.d("Moin --> onCreateView")
        binding = it
    }.root

    private fun DialogFragmentSettingsBinding.initUIItems(){
        val registerActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        pickDefaultAlarm.setOnClickListener {
            registerActivityResult.launch(Intent(Settings.ACTION_SOUND_SETTINGS))
        }

        initVolumeItems()
        initSnoozeItems()

    }

    private fun DialogFragmentSettingsBinding.initVolumeItems(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            volume.min = SettingsViewModel.MIN_VOLUME
        }
        volume.max = SettingsViewModel.MAX_VOLUME

        volume.setOnSeekBarChangeListener(
            VolumeSeekbarChangeListener()
        )

        volume.progress = viewModel.volume

        raiseVolume.setOnCheckedChangeListener { _, isChecked ->
            viewModel.raiseVolumeGradually = isChecked
        }
        raiseVolume.isChecked = viewModel.raiseVolumeGradually
    }

    private fun DialogFragmentSettingsBinding.initSnoozeItems(){
        snoozeDuration.progress = viewModel.snoozeDuration
        snoozeDuration.max = SettingsViewModel.MAX_SNOOZE_DURATION
        snoozeDuration.setOnSeekBarChangeListener(
            SnoozeDurationSeekbarChangeListener()
        )

        snoozeDurationHeader.text = getString(R.string.snooze_duration, viewModel.snoozeDuration.toString())
    }


    inner class VolumeSeekbarChangeListener: SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            Timber.i("Moin.onProgressChanged Volume--> $progress")
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            Timber.i("Moin.onStartTrackingTouch Volume--> ${seekBar?.progress}")
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            Timber.i("Moin.onStopTrackingTouch Volume--> ${seekBar?.progress}")
            viewModel.volume = seekBar?.progress?:100
        }

    }

    inner class SnoozeDurationSeekbarChangeListener: SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            var snoozeDuration = progress - progress% SettingsViewModel.STEP_SNOOZE_DURATION
            if (snoozeDuration < SettingsViewModel.MIN_SNOOZE_DURATION) snoozeDuration =
                SettingsViewModel.MIN_SNOOZE_DURATION

            binding?.snoozeDuration?.progress = snoozeDuration
            binding?.snoozeDurationHeader?.text = getString(R.string.snooze_duration, snoozeDuration.toString())
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            Timber.i("Moin.onStartTrackingTouch SnoozeDuration--> ${seekBar?.progress}")
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            Timber.i("Moin.onStopTrackingTouch SnoozeDuration--> ${seekBar?.progress}")
            viewModel.snoozeDuration = seekBar?.progress?: SettingsViewModel.DEF_SNOOZE_DURATION
        }

    }

    companion object{
        val TAG = SettingsDialogFragment.javaClass.canonicalName
    }
}