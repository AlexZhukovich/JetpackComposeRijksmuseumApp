package com.alexzh.rijksmuseum.utils.generator.data

import com.alexzh.rijksmuseum.data.remote.model.ImageInformationResponse
import java.util.UUID
import kotlin.random.Random

fun generateImageInformationResponse(
    guid: String? = UUID.randomUUID().toString(),
    width: Int = Random.nextInt(2_000),
    height: Int = Random.nextInt(2_000),
    url: String? = "https://example.com/${UUID.randomUUID()}"
) = ImageInformationResponse(
    guid = guid,
    width = width,
    height = height,
    url = url
)