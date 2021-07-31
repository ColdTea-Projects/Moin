package de.coldtea.moin.services.model

import com.spotify.protocol.types.PlayerState

sealed class SpotifyState

object ConnectionSuccess: SpotifyState()
object ConnectionFailed: SpotifyState()

data class Play(val playerState: PlayerState): SpotifyState()