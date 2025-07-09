package com.alexzh.rijksmuseum.data.mapper

import com.alexzh.rijksmuseum.data.remote.model.ArtObjectResponse
import com.alexzh.rijksmuseum.domain.model.ArtObject

fun ArtObjectResponse.toArtObject(): ArtObject {
    return ArtObject(
        id = this.id,
        objectNumber = this.objectNumber,
        title = this.title,
        webImage = this.webImage?.toImageInformation(),
        headerImage = this.headerImage?.toImageInformation()
    )
}