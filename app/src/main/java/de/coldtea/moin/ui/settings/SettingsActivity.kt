package de.coldtea.moin.ui.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import de.coldtea.moin.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SettingsActivity: AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        val registerActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        binding.pickDefaultAlarm.setOnClickListener {
            registerActivityResult.launch(Intent(Settings.ACTION_SOUND_SETTINGS))
        }

        setContentView(binding.root)

        binding.initUIItems()

    }

    private fun ActivitySettingsBinding.initUIItems(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            volume.min = 10
        }
        volume.max = 100

        volume.setOnSeekBarChangeListener(
            SettingSeekbarChangeListener()
        )

        volume.progress = viewModel.volume

        raiseVolume.setOnCheckedChangeListener { _, isChecked ->
            viewModel.raiseVolumeGradually = isChecked
        }
        raiseVolume.isChecked = viewModel.raiseVolumeGradually

    }

    inner class SettingSeekbarChangeListener: SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            Timber.i("Moin.onProgressChanged --> $progress")
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            Timber.i("Moin.onStartTrackingTouch --> ${seekBar?.progress}")
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            Timber.i("Moin.onStopTrackingTouch --> ${seekBar?.progress}")
            viewModel.volume = seekBar?.progress?:100
        }

    }
}