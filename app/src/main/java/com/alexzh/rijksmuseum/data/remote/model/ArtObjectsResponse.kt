package com.alexzh.rijksmuseum.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtObjectsResponse(
    @SerialName("artObjects") val artObjects: List<ArtObjectResponse>,
    @SerialName("count") val count: Int
)