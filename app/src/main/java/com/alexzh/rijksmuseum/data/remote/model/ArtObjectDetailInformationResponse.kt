package com.alexzh.rijksmuseum.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtObjectDetailInformationResponse(
    @SerialName("id") val id: String,
    @SerialName("objectNumber") val objectNumber: String,
    @SerialName("title") val title: String,
    @SerialName("longTitle") val longTitle: String,
    @SerialName("webImage") val webImage: ImageInformationResponse? = null,
    @SerialName("plaqueDescriptionEnglish") val plaqueDescriptionEnglish: String? = null
)