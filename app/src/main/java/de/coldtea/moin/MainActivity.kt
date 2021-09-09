package de.coldtea.moin

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import de.coldtea.moin.databinding.ActivityMainBinding
import de.coldtea.moin.domain.workmanager.ForecastUpdateWorkManager
import de.coldtea.moin.ui.alarm.AlarmFragment
import de.coldtea.moin.ui.debugview.DebugActivity
import de.coldtea.moin.ui.playlist.PlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.host_fragment, AlarmFragment())
            .commit()

        binding.setupBottomNavigation()

        setSupportActionBar(binding.appToolbar)

        if(mainViewModel.geolocationService.isNotPermitted) ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), 225)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater

        inflater.inflate(R.menu.action_bar_menu, menu)
        menu.findItem(R.id.debug_menu)?.isVisible = BuildConfig.DEBUG

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.debug_menu -> {
                if (!BuildConfig.DEBUG) return false

                val intent = Intent(this, DebugActivity::class.java)
                startActivity(intent)

                true
            }
            else -> false
        }
    }

    override fun onResume() {
        super.onResume()
        if (!mainViewModel.geolocationService.isNotPermitted && !mainViewModel.sharedPreferencesRepository.didWorksStart) startWeatherForecastPeriodicalUpdateWork()
    }

    private fun ActivityMainBinding.setupBottomNavigation() =
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_alarms -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.host_fragment, AlarmFragment())
                        .commit()

                    true
                }

                R.id.navigation_playlists -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.host_fragment, PlaylistFragment())
                        .commit()

                    true
                }

                else -> false
            }
        }

    private fun startWeatherForecastPeriodicalUpdateWork() {
        ForecastUpdateWorkManager.startPeriodicalForecastUpdate(applicationContext)
        mainViewModel.sharedPreferencesRepository.didWorksStart = true
    }

}