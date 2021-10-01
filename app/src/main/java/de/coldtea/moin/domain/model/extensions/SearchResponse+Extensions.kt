package de.coldtea.moin.domain.model.extensions

import de.coldtea.moin.data.network.spotify.model.*
import de.coldtea.moin.domain.model.spotify.*

fun SearchResponse.toSearchResult() =
    SearchResult(
        tracks.toTracks()
    )

fun TracksResponse?.toTracks() =
    Tracks(
        href = this?.href.orEmpty(),
        items = this?.items?.map {
            it.toItem()
        }?: listOf(),
        limit = this?.limit?:-1,
        next = this?.next.orEmpty(),
        offset = this?.offset?:0,
        previous = this?.previous?: Any(),
        total = this?.total?:0
    )

fun ItemResponse?.toItem() =
    Item(
        album = this?.album.toAlbum(),
        artists = this?.artists?.map { it.toArtist() }?: listOf(),
        availableMarkets = this?.availableMarkets?.map { it.orEmpty() }?: listOf(),
        discNumber = this?.discNumber?:0,
        durationMs = this?.durationMs?:0,
        explicit = this?.explicit?:false,
        externalIds = ExternalIds(this?.externalIds?.isrc.orEmpty()),
        externalUrls = ExternalUrls(this?.externalUrls?.spotify.orEmpty()),
        href = this?.href.orEmpty(),
        id = this?.id.orEmpty(),
        isLocal = this?.isLocal?:false,
        name = this?.name.orEmpty(),
        popularity = this?.popularity?:0,
        previewUrl = this?.previewUrl.orEmpty(),
        trackNumber = this?.trackNumber?:0,
        type = this?.type.orEmpty(),
        uri = this?.uri.orEmpty()
    )

fun AlbumResponse?.toAlbum() =
    Album(
        albumType = this?.albumType.orEmpty(),
        artists = this?.artists?.map { it.toArtist() }?: listOf(),
        availableMarkets = this?.availableMarkets?.map { it.orEmpty() }?: listOf(),
        externalUrls = ExternalUrls(this?.externalUrls?.spotify.orEmpty()),
        href = this?.href.orEmpty(),
        id = this?.id.orEmpty(),
        images = this?.images?.map { it?.toImage() }?: listOf(),
        name = this?.name.orEmpty(),
        releaseDate = this?.releaseDate.orEmpty(),
        releaseDatePrecision = this?.releaseDatePrecision.orEmpty(),
        totalTracks = this?.totalTracks?:0,
        type = this?.type.orEmpty(),
        uri = this?.uri.orEmpty()
    )

fun ArtistResponse?.toArtist() =
    Artist(
        externalUrls = ExternalUrls(this?.externalUrls?.spotify.orEmpty()),
        href = this?.href.orEmpty(),
        id = this?.id.orEmpty(),
        name = this?.name.orEmpty(),
        type = this?.type.orEmpty(),
        uri = this?.uri.orEmpty()
    )

fun ImageResponse?.toImage() =
    Image(
        height = this?.height?:0,
        url = this?.url.orEmpty(),
        width = this?.width?:0
    )