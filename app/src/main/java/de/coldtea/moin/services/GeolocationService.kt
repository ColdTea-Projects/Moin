package de.coldtea.moin.services

import android.Manifest.permission.*
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import de.coldtea.moin.extensions.getCityName
import de.coldtea.moin.services.model.LatLong
import timber.log.Timber
import java.util.*

class GeolocationService(val context: Context) {
    private val geocoder = Geocoder(context, Locale.getDefault())
    private val locationManager: LocationManager by lazy { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    fun getCityName(): String? {
        val longLat = getLatLong() ?: return ""


        Timber.i("Moin --> context: $context")
        Timber.i("Moin --> geocoder: $geocoder")
        val addresses = geocoder.getFromLocation(longLat.lat, longLat.long, 1)

        Timber.i("Moin --> address: $addresses")
        return addresses.first().getCityName()
    }

    fun getLatLong(): LatLong? {
        val location = if (ActivityCompat.checkSelfPermission(
                context,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.i("Moin --> permission problem")
            null
        } else {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)//TODO: Make sure GPS is open otherwise warn!
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        if (location == null) return null

        return LatLong(location.latitude, location.longitude)
    }

    val isNotPermitted: Boolean
        @RequiresApi(Build.VERSION_CODES.Q)
        get() = ActivityCompat.checkSelfPermission(
            context,
            ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            ACCESS_BACKGROUND_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

}