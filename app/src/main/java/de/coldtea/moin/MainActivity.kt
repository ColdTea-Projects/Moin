package de.coldtea.moin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import de.coldtea.moin.databinding.ActivityMainBinding
import de.coldtea.moin.services.SmplrAlarmService
import de.coldtea.moin.ui.alarm.AlarmFragment
import de.coldtea.moin.ui.playlist.PlaylistFragment
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.models.WeekDays
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

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
    }

    private fun ActivityMainBinding.setupBottomNavigation() =
        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_alarms -> {
                    supportFragmentManager
                        .beginTransaction()
                        .add(R.id.host_fragment, AlarmFragment())
                        .commit()

                    true
                }

                R.id.navigation_playlists -> {
                    supportFragmentManager
                        .beginTransaction()
                        .add(R.id.host_fragment, PlaylistFragment())
                        .commit()

                    true
                }

                else -> false
            }
        }


}