package de.coldtea.moin.domain.model.playlist

import androidx.annotation.IntegerRes
import de.coldtea.moin.R
import de.coldtea.moin.extensions.isDayTime
import java.util.*

enum class PlaylistName(val key: String, @IntegerRes val iconRes: Int) {
    SUNNY("sunny", R.drawable.ic_sunnny_icon),
    RAINY("rainy", R.drawable.ic_rainy_icon),
    CLOUDY("cloudy", R.drawable.ic_cloudy_icon),
    SNOWY("snowy", R.drawable.ic_snowy_icon)
}

fun PlaylistName.getTitle(): String =
    if (this == PlaylistName.SUNNY && !Calendar.getInstance().isDayTime()) {
        SUNNY_WHEN_SUN_IS_DOWN_TITLE
    } else this.name

@IntegerRes
fun PlaylistName.getIcon(): Int=
    if (this == PlaylistName.SUNNY && !Calendar.getInstance().isDayTime()) {
        SUNNY_WHEN_SUN_IS_DOWN_ICON
    } else this.iconRes

private const val SUNNY_WHEN_SUN_IS_DOWN_TITLE = "CLEAR"
private const val SUNNY_WHEN_SUN_IS_DOWN_ICON = R.drawable.ic_clear_icon
