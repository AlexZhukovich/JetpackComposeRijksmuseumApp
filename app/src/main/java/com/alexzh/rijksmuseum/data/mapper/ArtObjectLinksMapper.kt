package com.alexzh.rijksmuseum.data.mapper

import com.alexzh.rijksmuseum.data.remote.model.LinksResponse
import com.alexzh.rijksmuseum.domain.model.ArtObjectLinks

fun LinksResponse.toArtObjectLinks(): ArtObjectLinks {
    return ArtObjectLinks(
        self = this.self,
        web = this.web
    )
}