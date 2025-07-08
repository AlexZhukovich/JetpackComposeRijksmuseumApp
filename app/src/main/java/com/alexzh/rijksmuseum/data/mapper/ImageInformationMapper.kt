package com.alexzh.rijksmuseum.data.mapper

import com.alexzh.rijksmuseum.data.remote.model.ImageInformationResponse
import com.alexzh.rijksmuseum.domain.model.ImageInformation

fun ImageInformationResponse.toImageInformation(): ImageInformation {
    return ImageInformation(
        id = this.guid,
        width = this.width,
        height = this.height,
        url = this.url
    )
}