package com.alexzh.rijksmuseum.domain.model

data class ArtObject(
    val id: String,
    val objectNumber: String,
    val title: String,
    val longTitle: String,
    val links: ArtObjectLinks,
    val webImage: ImageInformation? = null,
    val headerImage: ImageInformation? = null,
    val productionPlaces: List<String>
)