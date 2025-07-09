package com.alexzh.rijksmuseum.data.remote.model

import com.alexzh.rijksmuseum.domain.model.ArtObject

data class ArtObjectsPage(
    val items: List<ArtObject>,
    val totalCount: Int,
    val currentPage: Int,
    val hasNextPage: Boolean
)
