package de.coldtea.moin.ui.debugview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ActivityDebugBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DebugActivity : AppCompatActivity() {

    // region properties
    private val debugViewModel: DebugViewModel by viewModel()

    var binding: ActivityDebugBinding? = null
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_debug)

        lifecycleScope.launch {
            debugViewModel.weatherResponse.collect{
                binding?.weatherText?.text = it.toString()
            }
        }
        val city = debugViewModel.getCity(this)

        if(city == "") return
        binding?.city?.text = city
        debugViewModel.getWeatherForecast(city)

    }
}