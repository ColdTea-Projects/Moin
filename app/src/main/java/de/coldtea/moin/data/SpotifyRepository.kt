package de.coldtea.moin.data

import de.coldtea.moin.data.network.spotify.SpotifyApi
import de.coldtea.moin.data.network.spotify.model.SearchResponse

class SpotifyRepository(
    private val spotifyApi: SpotifyApi,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {

    suspend fun search(
        query: String,
        accessToken: String
    ): SearchResponse? =
        spotifyApi.search(
            query,
            "track",
            "Bearer $accessToken"
        )

}