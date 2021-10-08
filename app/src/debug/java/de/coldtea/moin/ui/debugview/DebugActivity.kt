package de.coldtea.moin.ui.debugview

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ActivityDebugBinding
import de.coldtea.moin.domain.services.SpotifyService.REDIRECT_URI_ROOT
import de.coldtea.moin.extensions.convertToAuthorizationResponse
import de.coldtea.moin.services.model.*
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

        if(!city.isNullOrEmpty())
        {
            binding?.city?.text = city
            binding?.search?.setOnClickListener { onSearchClicked() }
            debugViewModel.getWeatherForecast(location)
        }

        binding?.play?.setOnClickListener {
            debugViewModel.playPlaylist()
        }

        //This part does not work from here anymore, please use actual use-case to get spotify authorization TODO:Remove authorization
        val data: Uri? = intent.data

        if (data != null && !TextUtils.isEmpty(data.scheme)) {
            if (REDIRECT_URI_ROOT == data.scheme) {
                binding?.spotify?.text = data.toString()
                val authorizationResponse = data.toString().convertToAuthorizationResponse()
                debugViewModel.registerAuthorizationCode(authorizationResponse)
                debugViewModel.getAccessToken(authorizationResponse?.code)
            }
        }else if(!debugViewModel.refreshTokenExist){
            startActivity(debugViewModel.getAuthorizationIntent())
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

    private fun onSearchClicked(){
        debugViewModel.getAccessTokenByRefreshToken()
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