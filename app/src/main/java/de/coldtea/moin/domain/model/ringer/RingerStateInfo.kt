package de.coldtea.moin.domain.model.ringer

sealed class RingerStateInfo

data class Randomized(val ringerScreenInfo: RingerScreenInfo?):RingerStateInfo()
data class Plays(val ringerScreenInfo: RingerScreenInfo?):RingerStateInfo()
object Stops: RingerStateInfo()