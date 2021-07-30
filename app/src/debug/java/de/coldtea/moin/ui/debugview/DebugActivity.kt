package de.coldtea.moin.ui.debugview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ActivityDebugBinding
import de.coldtea.moin.services.model.ConnectionFailed
import de.coldtea.moin.services.model.ConnectionSuccess
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class DebugActivity : AppCompatActivity() {

    // region properties
    private val debugViewModel: DebugViewModel by viewModel()

    var binding: ActivityDebugBinding? = null
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_debug)

        initCurrentResponse()
        initWeatherResponse()
        initSpotify()

        val city = debugViewModel.getCity()
        val location = debugViewModel.getLocation()

        if(city == "") return
        binding?.city?.text = city
        debugViewModel.getWeatherForecast(city, location)

        binding?.play?.setOnClickListener {
            debugViewModel.playPlaylist()
        }
    }

    override fun onStart() {
        super.onStart()
        debugViewModel.connectSpotify(this)
    }

    override fun onStop() {
        super.onStop()
        debugViewModel.disconnectSpotify()
    }

    private fun initCurrentResponse() = lifecycleScope.launchWhenResumed {
        debugViewModel.currentResponse.collect{
            binding?.dailyWeather?.text = "Current weather : $it"
        }
    }

    private fun initWeatherResponse() = lifecycleScope.launchWhenResumed {
        debugViewModel.weatherResponse.collect{
            binding?.weatherText?.text = "Weather for 3 days : $it"
        }
    }

    private fun initSpotify() = lifecycleScope.launchWhenResumed {
        debugViewModel.spotifyState.collect{
            when(it){
                ConnectionSuccess -> binding?.play?.isEnabled = true
                ConnectionFailed -> binding?.play?.isEnabled = false
            }
        }
    }
}