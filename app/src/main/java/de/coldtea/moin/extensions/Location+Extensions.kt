package de.coldtea.moin.extensions

import android.location.Address

fun Address.getCityName(): String? =
    if (locality != null) locality
    else if (adminArea != null) adminArea
    else if (subLocality != null) subLocality
    else subAdminArea