package com.alexzh.rijksmuseum.utils.generator.domain

import com.alexzh.rijksmuseum.data.remote.model.ArtObjectsPage
import com.alexzh.rijksmuseum.domain.model.ArtObject

fun generateArtObjectsPage(
    items: List<ArtObject> = (1..20).map { generateArtObject() },
    totalCount: Int = 100,
    currentPage: Int = 1,
    hasNextPage: Boolean = true
) = ArtObjectsPage(
    items = items,
    totalCount = totalCount,
    currentPage = currentPage,
    hasNextPage = hasNextPage
)