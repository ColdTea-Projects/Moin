package de.coldtea.moin.services

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import de.coldtea.moin.services.model.LongLat
import java.util.*

class GeolocationService(val context: Context) {
    val REQUEST_CODE = 5

    val geocoder = Geocoder(context, Locale.getDefault())
    private val locationManager: LocationManager by lazy { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager}

    fun getCityName(activity: Activity): String {
        val longLat = activity.getLongLat()?:return ""

        val addresses = geocoder.getFromLocation(longLat.lat, longLat.long, 1)

        return addresses.first().locality
    }

    private fun Activity.getLongLat(): LongLat?{
        val location = if (ActivityCompat.checkSelfPermission(
                context,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), REQUEST_CODE)
            null
        }else{
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }

        if(location == null) return null

        return LongLat(location.longitude, location.latitude)
    }
}