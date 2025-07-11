package com.alexzh.rijksmuseum.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtObjectDetailsResponse(
    @SerialName("artObject") val artObject: ArtObjectDetailInformationResponse?
)