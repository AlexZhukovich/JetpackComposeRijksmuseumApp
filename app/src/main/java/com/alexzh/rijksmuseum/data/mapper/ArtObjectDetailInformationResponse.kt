package com.alexzh.rijksmuseum.data.mapper

import com.alexzh.rijksmuseum.data.remote.model.ArtObjectDetailInformationResponse
import com.alexzh.rijksmuseum.domain.model.ArtObjectDetails

fun ArtObjectDetailInformationResponse.toArtObjectDetails(): ArtObjectDetails {
    return ArtObjectDetails(
        id = this.id,
        objectNumber = this.objectNumber,
        title = this.title,
        longTitle = this.longTitle,
        webImage = this.webImage?.toImageInformation(),
        plaqueDescriptionEnglish = this.plaqueDescriptionEnglish,
    )
}