package de.coldtea.moin.domain.model.forecast

import de.coldtea.moin.domain.model.playlist.PlaylistName

val SUNNY_WEATHER_BUNDLE = ForecastConditionBundle(
    PlaylistName.SUNNY,
    listOf(
        1000,
        1003
    )
)

val CLOUDY_WEATHER_BUNDLE = ForecastConditionBundle(
    PlaylistName.CLOUDY,
    listOf(
        1006,
        1009,
        1030,
        1087,
        1135,
        1147
    )
)

val RAINY_WEATHER_BUNDLE = ForecastConditionBundle(
    PlaylistName.RAINY,
    listOf(
        1063,
        1069,
        1072,
        1150,
        1153,
        1168,
        1171,
        1180,
        1183,
        1186,
        1189,
        1192,
        1195,
        1198,
        1201,
        1204,
        1207,
        1240,
        1243,
        1246,
        1249,
        1252,
        1273,
        1276
    )
)

val SNOWY_WEATHER_BUNDLE = ForecastConditionBundle(
    PlaylistName.SNOWY,
    listOf(
        1066,
        1114,
        1117,
        1210,
        1213,
        1216,
        1219,
        1222,
        1225,
        1237,
        1255,
        1258,
        1261,
        1264,
        1279,
        1282
    )
)