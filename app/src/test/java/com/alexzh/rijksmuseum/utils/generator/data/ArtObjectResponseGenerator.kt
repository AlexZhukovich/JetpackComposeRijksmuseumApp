package com.alexzh.rijksmuseum.utils.generator.data

import com.alexzh.rijksmuseum.data.remote.model.ArtObjectResponse
import com.alexzh.rijksmuseum.data.remote.model.ImageInformationResponse
import java.util.UUID

fun generateArtObjectResponse(
    id: String = UUID.randomUUID().toString(),
    objectNumber: String = UUID.randomUUID().toString(),
    title: String = UUID.randomUUID().toString(),
    webImage: ImageInformationResponse? = generateImageInformationResponse(),
    headerImage: ImageInformationResponse? = generateImageInformationResponse(),
) = ArtObjectResponse(
    id = id,
    objectNumber = objectNumber,
    title = title,
    webImage = webImage,
    headerImage = headerImage
)