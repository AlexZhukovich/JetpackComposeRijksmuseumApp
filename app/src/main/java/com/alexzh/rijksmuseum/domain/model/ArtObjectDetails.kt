package com.alexzh.rijksmuseum.domain.model

data class ArtObjectDetails(
    val id: String,
    val objectNumber: String,
    val title: String,
    val longTitle: String,
    val webImage: ImageInformation? = null,
    val plaqueDescriptionEnglish: String,
)
