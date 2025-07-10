package com.alexzh.rijksmuseum.utils.generator.data

import com.alexzh.rijksmuseum.data.remote.model.ArtObjectResponse
import com.alexzh.rijksmuseum.data.remote.model.ArtObjectsResponse

fun generateArtObjectsResponse(
    artObjects: List<ArtObjectResponse> = (1..20).map { generateArtObjectResponse() }
) = ArtObjectsResponse(
    artObjects = artObjects,
    count = 20
)