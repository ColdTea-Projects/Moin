package de.coldtea.moin.data.network.forecast.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Condition(
    val code: Int?,
    val text: String?
)