package com.alexzh.rijksmuseum.ui.feature.artobjectdetails

import com.alexzh.rijksmuseum.R

enum class ArtObjectDetailsError(
    val localizedMessage: Int
) {
    NOT_FOUND(R.string.error_artObjectDetailsNotFound_label),
    LOADING_ERROR(R.string.error_artObjectDetailsLoadingError_label),
    CONNECTIVITY_ERROR(R.string.error_connectivityError_label)
}