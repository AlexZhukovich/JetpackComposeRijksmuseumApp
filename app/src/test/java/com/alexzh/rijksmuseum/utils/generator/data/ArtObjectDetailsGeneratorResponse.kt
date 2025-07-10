package com.alexzh.rijksmuseum.utils.generator.data

import com.alexzh.rijksmuseum.data.remote.model.ArtObjectDetailInformationResponse
import com.alexzh.rijksmuseum.data.remote.model.ArtObjectDetailsResponse

fun generateArtObjectDetailsResponse(
    artObject: ArtObjectDetailInformationResponse = generateArtObjectDetailInformationResponse()
) = ArtObjectDetailsResponse(
    artObject = artObject
)