package de.coldtea.moin.domain.model.playlist

import de.coldtea.moin.extensions.isDayTime
import java.util.*

enum class PlaylistName(val key: String) {
    SUNNY("sunny"),
    RAINY("rainy"),
    CLOUDY("cloudy"),
    SNOWY("snowy")
}

fun PlaylistName.getTitle(): String =
    if (this == PlaylistName.SUNNY && !Calendar.getInstance().isDayTime()) {
        SUNNY_WHEN_SUN_IS_DOWN
    } else this.name

private const val SUNNY_WHEN_SUN_IS_DOWN = "CLEAR"
