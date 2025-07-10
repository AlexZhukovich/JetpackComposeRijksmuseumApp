package com.alexzh.rijksmuseum.utils.generator.data

import com.alexzh.rijksmuseum.data.remote.model.ArtObjectDetailInformationResponse
import com.alexzh.rijksmuseum.data.remote.model.ImageInformationResponse
import java.util.UUID

fun generateArtObjectDetailInformationResponse(
    id: String = UUID.randomUUID().toString(),
    objectNumber: String = UUID.randomUUID().toString(),
    title: String = UUID.randomUUID().toString(),
    longTitle: String = UUID.randomUUID().toString(),
    webImage: ImageInformationResponse = generateImageInformationResponse(),
    plaqueDescriptionEnglish: String = UUID.randomUUID().toString()
) = ArtObjectDetailInformationResponse(
    id = id,
    objectNumber = objectNumber,
    title = title,
    longTitle = longTitle,
    webImage = webImage,
    plaqueDescriptionEnglish = plaqueDescriptionEnglish
)