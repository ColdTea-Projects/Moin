package de.coldtea.moin.data

import de.coldtea.moin.data.network.spotify.SpotifyApi
import de.coldtea.moin.domain.model.extensions.toSearchResult
import de.coldtea.moin.domain.model.spotify.SearchResult

class SpotifyRepository(
    private val spotifyApi: SpotifyApi
) {

    suspend fun search(
        query: String,
        accessToken: String
    ): SearchResult? =
        spotifyApi.search(
            query,
            "track",
            "Bearer $accessToken"
        )?.toSearchResult()

}