package de.coldtea.moin.services.model

sealed class SpotifyState

object ConnectionSuccess: SpotifyState()
object ConnectionFailed: SpotifyState()
