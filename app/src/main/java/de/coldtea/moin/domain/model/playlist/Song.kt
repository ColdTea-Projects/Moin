package de.coldtea.moin.domain.model.playlist

data class Song (
    val songId: Int,
    val name: String,
    val albumName: String,
    val imageUrl: String,
    val mediaType: Int,
    val source: String,
    val playlist: Int
)