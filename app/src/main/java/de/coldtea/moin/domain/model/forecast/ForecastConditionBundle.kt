package de.coldtea.moin.domain.model.forecast

import de.coldtea.moin.domain.model.playlist.PlaylistName

data class ForecastConditionBundle(
    val playlistName: PlaylistName,
    val conditionCodes: List<Int>
)