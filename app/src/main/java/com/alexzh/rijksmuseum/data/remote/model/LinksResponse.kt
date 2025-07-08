package com.alexzh.rijksmuseum.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinksResponse(
    @SerialName("self") val self: String? = null,
    @SerialName("web") val web: String? = null
)