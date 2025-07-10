package com.alexzh.rijksmuseum.utils.generator.domain

import com.alexzh.rijksmuseum.domain.model.ImageInformation
import java.util.UUID
import kotlin.random.Random

fun generateImageInformation(
    id: String? = UUID.randomUUID().toString(),
    width: Int = Random.nextInt(2_000),
    height: Int = Random.nextInt(2_000),
    url: String? = "https://example.com/${UUID.randomUUID()}"
) = ImageInformation(
    id = id,
    width = width,
    height = height,
    url = url
)