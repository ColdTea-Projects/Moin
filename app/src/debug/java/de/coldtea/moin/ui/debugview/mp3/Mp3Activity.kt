package de.coldtea.moin.ui.debugview.mp3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ActivityMp3Binding
import de.coldtea.moin.domain.services.FilePickerConverter
import de.coldtea.moin.domain.services.MP3PlayerService
import de.coldtea.moin.domain.services.MP3PlayerService.Companion.MP3_MIME_TYPE
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class Mp3Activity : AppCompatActivity() {

    // region properties
    private val mp3ViewModel: Mp3ViewModel by viewModel()

    var binding: ActivityMp3Binding? = null
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mp3)

        val registerActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResult(result)
        }

        binding?.picker?.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = MP3_MIME_TYPE
            registerActivityResult.launch(intent)
        }

        binding?.play?.setOnClickListener{
            mp3ViewModel.mP3PlayerService?.play()
        }

        binding?.stop?.setOnClickListener {
            mp3ViewModel.mP3PlayerService?.stop()
        }
    }

    fun onActivityResult(result: ActivityResult) {
        mp3ViewModel.mp3Uri = Uri.parse(result.data?.data.toString())

        mp3ViewModel.mp3Uri?.let { uri ->
            mp3ViewModel.mP3PlayerService = MP3PlayerService(applicationContext, uri)
            Timber.i("Moin --> $uri")

            val mP3Object = FilePickerConverter.getMP3Object(contentResolver, uri)

            binding?.displayName?.text = mP3Object.displayName
            binding?.play?.isEnabled = true
            binding?.stop?.isEnabled = true
        }

    }

}
