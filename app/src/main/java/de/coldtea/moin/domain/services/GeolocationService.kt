package de.coldtea.moin.domain.services

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.domain.model.alarm.LatLong
import de.coldtea.moin.extensions.getCityName
import timber.log.Timber
import java.util.*

class GeolocationService(
    private val context: Context,
    private val sharedPreferencesRepository: SharedPreferencesRepository
    ) {
    private val geocoder = Geocoder(context, Locale.getDefault())
    private val locationManager: LocationManager by lazy { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    val permitted: Boolean
        get() = ActivityCompat.checkSelfPermission(
            context,
            ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            context,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    fun getCityName(): String? {
        val longLat = getLatLong() ?: return ""

        Timber.i("Moin --> context: $context")
        Timber.i("Moin --> geocoder: $geocoder")
        val addresses = geocoder.getFromLocation(longLat.lat, longLat.long, 1)

        Timber.i("Moin --> address: $addresses")
        sharedPreferencesRepository.lastVisitedCity = addresses.first().getCityName()

        return sharedPreferencesRepository.lastVisitedCity
    }

    @SuppressLint("MissingPermission")
    fun getLatLong(): LatLong? {
        val location = if (permitted) {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } else {

            Timber.i("Moin --> permission problem")
            null
        }

        if (location == null) return null

        return LatLong(location.latitude, location.longitude)
    }

    fun requestLocationPermit(activity: AppCompatActivity) =
        activity.requestPermissions(
            REQUESTED_LOCATION_PERMISSIONS,
            LOCATION_PERMIT_REQUEST_CODE
        )

    companion object {
        const val LOCATION_PERMIT_REQUEST_CODE = 255
        val REQUESTED_LOCATION_PERMISSIONS = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
    }

}