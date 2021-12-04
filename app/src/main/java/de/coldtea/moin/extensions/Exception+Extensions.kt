package de.coldtea.moin.extensions

import de.coldtea.moin.data.network.spotify.model.SpotifyErrorResponse
import de.coldtea.moin.di.json
import kotlinx.serialization.ExperimentalSerializationApi
import retrofit2.HttpException

@OptIn(ExperimentalSerializationApi::class)
fun HttpException.getSpotifyErrorResponse(): SpotifyErrorResponse? = try {
    val errorBody = response()?.errorBody()?.string()
    json.decodeFromString(
        SpotifyErrorResponse.serializer(),
        errorBody.orEmpty()
    )
} catch (exception: Exception) {
    null
}