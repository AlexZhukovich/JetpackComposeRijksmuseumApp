package com.alexzh.rijksmuseum.utils.generator.domain

import com.alexzh.rijksmuseum.domain.model.ArtObjectDetails
import com.alexzh.rijksmuseum.domain.model.ImageInformation
import java.util.UUID

fun generateArtObjectDetails(
    id: String = UUID.randomUUID().toString(),
    objectNumber: String = UUID.randomUUID().toString(),
    title: String = UUID.randomUUID().toString(),
    longTitle: String = UUID.randomUUID().toString(),
    webImage: ImageInformation = generateImageInformation(),
    plaqueDescriptionEnglish: String = UUID.randomUUID().toString()
) = ArtObjectDetails(
    id = id,
    objectNumber = objectNumber,
    title = title,
    longTitle = longTitle,
    webImage = webImage,
    plaqueDescriptionEnglish = plaqueDescriptionEnglish
)