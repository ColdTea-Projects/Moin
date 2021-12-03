package de.coldtea.moin.ui.debugview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import de.coldtea.moin.databinding.ActivityDebugBinding
import de.coldtea.moin.services.model.*
import de.coldtea.moin.ui.debugview.mp3.Mp3Activity
import kotlinx.coroutines.Dispatchers
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
        initSpotify()

        lifecycleScope.launch(Dispatchers.IO) {
            val city = debugViewModel.getCity()
            val location = debugViewModel.getLocation()

            if(!city.isNullOrEmpty())
            {
                binding?.city?.text = city
                debugViewModel.getWeatherForecast(location)
            }
        }

        binding?.play?.setOnClickListener {
            debugViewModel.playPlaylist()
        }

        binding?.search?.setOnClickListener { onSearchClicked() }

        binding?.mp3?.setOnClickListener {
            val intent = Intent(this, Mp3Activity::class.java)
            startActivity(intent)
        }

        //This part does not work from here anymore, please use actual use-case to get spotify authorization TODO:Remove authorization
//        val data: Uri? = intent.data
//
//        if (data != null && !TextUtils.isEmpty(data.scheme)) {
//            if (REDIRECT_URI_ROOT == data.scheme) {
//                binding?.spotify?.text = data.toString()
//                val authorizationResponse = data.toString().convertToAuthorizationResponse()
//                debugViewModel.registerAuthorizationCode(authorizationResponse)
//                debugViewModel.getAccessToken(authorizationResponse?.code)
//            }
//        }else if(!debugViewModel.refreshTokenExist){
//            startActivity(debugViewModel.getAuthorizationIntent())
//        }
    }

    override fun onStart() {
        super.onStart()
        debugViewModel.connectSpotify(this)
    }

    override fun onStop() {
        super.onStop()
        debugViewModel.disconnectSpotify()
    }

    private fun onSearchClicked(){
        debugViewModel.getAccessTokenByRefreshToken()
    }

    private fun initCurrentResponse() = lifecycleScope.launchWhenResumed {
        debugViewModel.currentResponse.collect{
            binding?.dailyWeather?.text = "Current weather : $it"
        }
    }

    private fun initWeatherResponse() = lifecycleScope.launchWhenResumed {
        var weatherText: String = "Weather for 3 days : \n"

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

    private fun initSpotify() = lifecycleScope.launchWhenResumed {
        debugViewModel.spotifyState.collect{
            when(it){
                ConnectionSuccess -> binding?.play?.isEnabled = true
                ConnectionFailed -> binding?.play?.isEnabled = false
                is Play -> binding?.spotify?.text = it.playerState.toString()
                is AccessTokenReceived -> {
                    binding?.spotify?.text = it.tokenResponse.toString()

                    val keyword = binding?.keyword?.text.toString()
                    if (keyword.isNotEmpty() && it.tokenResponse?.accessToken != null){
                        debugViewModel.search(it.tokenResponse.accessToken, keyword)
                    }
                }
                is SearchResultReceived ->  binding?.spotify?.text = it.searchResult.toString()
            }
        }
    }
}