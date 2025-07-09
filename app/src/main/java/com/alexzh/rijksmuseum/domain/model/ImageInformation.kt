package com.alexzh.rijksmuseum.domain.model

data class ImageInformation(
    val id: String,
    val width: Int,
    val height: Int,
    val url: String
) {
    val aspectRatio: Float
        get() = width.toFloat() / height.toFloat()
}