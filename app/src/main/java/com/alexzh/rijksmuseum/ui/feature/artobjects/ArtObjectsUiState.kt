package com.alexzh.rijksmuseum.ui.feature.artobjects

import com.alexzh.rijksmuseum.domain.model.ArtObject

data class ArtObjectsUiState(
    val isLoading: Boolean = false,
    val items: List<ArtObject> = emptyList(),
    val canLoadMore: Boolean = true,
    val error: ArtObjectsError? = null,
) {
    fun isInitialLoading() = isLoading && items.isEmpty()

    fun isLoadingMoreItems() = isLoading && items.isNotEmpty()

    fun initialLoadingError(): ArtObjectsError? {
        return if (items.isEmpty() && error != null) error else null
    }

    fun loadMoreError(): ArtObjectsError? {
        return if (items.isNotEmpty() && error != null) error else null
    }
}