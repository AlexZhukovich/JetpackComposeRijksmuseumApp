package com.alexzh.rijksmuseum.utils.generator.domain

import com.alexzh.rijksmuseum.domain.model.ArtObject
import com.alexzh.rijksmuseum.domain.model.ImageInformation
import java.util.UUID

fun generateArtObject(
    id: String = UUID.randomUUID().toString(),
    objectNumber: String = UUID.randomUUID().toString(),
    title: String = UUID.randomUUID().toString(),
    webImage: ImageInformation? = generateImageInformation(),
    headerImage: ImageInformation? = generateImageInformation(),
) = ArtObject(
    id = id,
    objectNumber = objectNumber,
    title = title,
    webImage = webImage,
    headerImage = headerImage
)