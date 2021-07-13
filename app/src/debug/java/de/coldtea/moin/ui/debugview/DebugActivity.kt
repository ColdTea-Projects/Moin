package de.coldtea.moin.ui.debugview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ActivityDebugBinding
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

        val city = debugViewModel.getCity()
        val location = debugViewModel.getLocation()

        if(city == "") return
        binding?.city?.text = city
        debugViewModel.getWeatherForecast(city, location)
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
}