package com.alexzh.rijksmuseum.ui.feature.artobjectdetails

import com.alexzh.rijksmuseum.domain.model.ArtObjectDetails

data class ArtObjectDetailsUiState(
    val isLoading: Boolean = false,
    val data: ArtObjectDetails? = null,
    val error: ArtObjectDetailsError? = null
)