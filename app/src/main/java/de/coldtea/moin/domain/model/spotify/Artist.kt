package de.coldtea.moin.domain.model.spotify


data class Artist(
    val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)