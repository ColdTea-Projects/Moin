package de.coldtea.moin.domain.model.playlist

data class Song (
    val localId: Int,
    val trackId: String,
    val name: String,
    val artistName: String,
    val imageUrl: String,
    val mediaType: Int,
    val source: String,
    val playlist: Int
)