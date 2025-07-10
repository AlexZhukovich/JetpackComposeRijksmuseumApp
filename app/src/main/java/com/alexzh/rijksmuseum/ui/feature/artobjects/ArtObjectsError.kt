package com.alexzh.rijksmuseum.ui.feature.artobjects

import com.alexzh.rijksmuseum.R

enum class ArtObjectsError(
    val localizedMessage: Int
) {
    LOADING_ERROR(R.string.error_artObjectsLoadingError_label),
    CONNECTIVITY_ERROR(R.string.error_connectivityError_label)
}