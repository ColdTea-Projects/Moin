package de.coldtea.moin.extensions

import android.location.Address

fun Address.getCityName(): String? = when {
    locality != null -> locality
    adminArea != null -> adminArea
    subLocality != null -> subLocality
    else -> subAdminArea
}