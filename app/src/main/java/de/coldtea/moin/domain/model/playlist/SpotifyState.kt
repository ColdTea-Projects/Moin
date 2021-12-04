package de.coldtea.moin.services.model

import com.spotify.protocol.types.PlayerState
import de.coldtea.moin.data.network.spotify.model.TokenResponse
import de.coldtea.moin.domain.model.spotify.SearchResult

sealed class SpotifyState

object ConnectionSuccess: SpotifyState()
object ConnectionFailed: SpotifyState()
object AccessTokenFailed: SpotifyState()

data class Play(val playerState: PlayerState): SpotifyState()
data class AccessTokenReceived(val tokenResponse: TokenResponse?): SpotifyState()
data class SearchResultReceived(val searchResult: SearchResult?): SpotifyState()