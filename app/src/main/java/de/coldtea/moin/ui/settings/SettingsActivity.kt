package de.coldtea.moin.ui.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ActivitySettingsBinding
import de.coldtea.moin.ui.settings.SettingsViewModel.Companion.DEF_SNOOZE_DURATION
import de.coldtea.moin.ui.settings.SettingsViewModel.Companion.MAX_SNOOZE_DURATION
import de.coldtea.moin.ui.settings.SettingsViewModel.Companion.MAX_VOLUME
import de.coldtea.moin.ui.settings.SettingsViewModel.Companion.MIN_SNOOZE_DURATION
import de.coldtea.moin.ui.settings.SettingsViewModel.Companion.MIN_VOLUME
import de.coldtea.moin.ui.settings.SettingsViewModel.Companion.STEP_SNOOZE_DURATION
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SettingsActivity: AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModel()
    private var binding: ActivitySettingsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val registerActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        binding?.pickDefaultAlarm?.setOnClickListener {
            registerActivityResult.launch(Intent(Settings.ACTION_SOUND_SETTINGS))
        }

        setContentView(binding?.root)

        binding?.initUIItems()

    }

    private fun ActivitySettingsBinding.initUIItems(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            volume.min = MIN_VOLUME
        }
        volume.max = MAX_VOLUME

        volume.setOnSeekBarChangeListener(
            VolumeSeekbarChangeListener()
        )

        volume.progress = viewModel.volume

        raiseVolume.setOnCheckedChangeListener { _, isChecked ->
            viewModel.raiseVolumeGradually = isChecked
        }
        raiseVolume.isChecked = viewModel.raiseVolumeGradually

        snoozeDuration.progress = viewModel.snoozeDuration
        snoozeDuration.max = MAX_SNOOZE_DURATION
        snoozeDuration.setOnSeekBarChangeListener(
            SnoozeDurationSeekbarChangeListener()
        )

        binding?.snoozeDurationHeader?.text = getString(R.string.snooze_duration, viewModel.snoozeDuration.toString())

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
            var snoozeDuration = progress - progress%STEP_SNOOZE_DURATION
            if (snoozeDuration < MIN_SNOOZE_DURATION) snoozeDuration = MIN_SNOOZE_DURATION

            binding?.snoozeDuration?.progress = snoozeDuration
            binding?.snoozeDurationHeader?.text = getString(R.string.snooze_duration, snoozeDuration.toString())
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            Timber.i("Moin.onStartTrackingTouch SnoozeDuration--> ${seekBar?.progress}")
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            Timber.i("Moin.onStopTrackingTouch SnoozeDuration--> ${seekBar?.progress}")
            viewModel.snoozeDuration = seekBar?.progress?: DEF_SNOOZE_DURATION
        }

    }
}