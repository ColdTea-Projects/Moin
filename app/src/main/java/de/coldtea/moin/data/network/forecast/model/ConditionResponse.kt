package de.coldtea.moin.data.network.forecast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConditionResponse(
    @SerialName("code")
    val code: Int?,
    @SerialName("text")
    val text: String?
)