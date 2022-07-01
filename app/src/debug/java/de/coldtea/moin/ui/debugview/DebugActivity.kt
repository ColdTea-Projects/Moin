package de.coldtea.moin.ui.debugview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import de.coldtea.moin.databinding.ActivityDebugBinding
import de.coldtea.moin.ui.debugview.mp3.Mp3Activity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class DebugActivity : AppCompatActivity() {

    // region properties
    private val debugViewModel: DebugViewModel by viewModel()

    var binding: ActivityDebugBinding? = null
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebugBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initCurrentResponse()
        initWeatherResponse()

        lifecycleScope.launch {
            val city = debugViewModel.getCity()
            val location = debugViewModel.getLocation()

            if(!city.isNullOrEmpty())
            {
                binding?.city?.text = city
                debugViewModel.getWeatherForecast(location)
            }
        }

        binding?.mp3?.setOnClickListener {
            val intent = Intent(this, Mp3Activity::class.java)
            startActivity(intent)
        }
    }

    private fun initCurrentResponse() = lifecycleScope.launchWhenResumed {
        debugViewModel.currentResponse.collect{
            binding?.dailyWeather?.text = "Current weather : $it"
        }
    }

    private fun initWeatherResponse() = lifecycleScope.launchWhenResumed {
        var weatherText = "Weather for 3 days : \n"

        debugViewModel.weatherResponse.collect{
            it?.map { forecast ->

                val date = SimpleDateFormat("HH:mm dd.MM", Locale.GERMAN)
                    .format(
                        Date( forecast.timeEpoch * 1000L )
                    )

                weatherText += "{$date:${forecast.conditionText}:${forecast.tempC}}\n"
            }
            binding?.weatherText?.text = weatherText
        }

    }
}