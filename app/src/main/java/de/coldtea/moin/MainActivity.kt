package de.coldtea.moin

import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.lifecycle.lifecycleScope
import de.coldtea.moin.databinding.ActivityMainBinding
import de.coldtea.moin.domain.services.GeolocationService.Companion.LOCATION_PERMIT_REQUEST_CODE
import de.coldtea.moin.domain.services.GeolocationService.Companion.REQUESTED_LOCATION_PERMISSIONS
import de.coldtea.moin.domain.workmanager.ForecastUpdateWorkManager
import de.coldtea.moin.ui.alarm.AlarmFragment
import de.coldtea.moin.ui.debugview.DebugActivity
import de.coldtea.moin.ui.playlists.PlaylistsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.host_fragment, AlarmFragment())
            .commit()

        binding.setupBottomNavigation()

        setSupportActionBar(binding.appToolbar)

        if(!mainViewModel.locationServicesPermited) {
            mainViewModel.requestLocationServicePermissions(this)
        }

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
        if(mainViewModel.locationServicesPermited && mainViewModel.didWorksStart) mainViewModel.updateForecastIfLocationChanged()

    }

    private fun ActivityMainBinding.setupBottomNavigation() =
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_alarms -> {

                    supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.host_fragment, AlarmFragment())
                        .commit()

                    true
                }

                R.id.navigation_playlists -> {

                    supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.host_fragment, PlaylistsFragment())
                        .commit()

                    true
                }

                else -> false
            }
        }

    private fun startWeatherForecastPeriodicalUpdateWork() {
        ForecastUpdateWorkManager.startPeriodicalForecastUpdate(applicationContext)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == LOCATION_PERMIT_REQUEST_CODE){
            REQUESTED_LOCATION_PERMISSIONS.map {
                if(!permissions.contains(it)) return
            }

            if(grantResults.contains(PERMISSION_DENIED)) return

            onLocationPermissionsGranted()
        }
    }

    private fun onLocationPermissionsGranted() = lifecycleScope.launch(Dispatchers.IO){
        mainViewModel.saveLocation()
        startWeatherForecastPeriodicalUpdateWork()
    }
}
