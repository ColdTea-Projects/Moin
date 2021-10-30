package de.coldtea.moin.domain.model.ringer

import de.coldtea.moin.domain.model.playlist.Song

data class RingerScreenInfo (
    val forecastText: String,
    val song: Song?,
    val tempC: Double,
    val tempF: Double
)