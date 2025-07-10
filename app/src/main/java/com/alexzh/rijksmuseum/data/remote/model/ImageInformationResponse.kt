package com.alexzh.rijksmuseum.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageInformationResponse(
    @SerialName("guid") val guid: String? = null,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("url") val url: String? = null
)