package com.alexzh.rijksmuseum.domain

import com.alexzh.rijksmuseum.data.remote.model.ArtObjectsPage
import com.alexzh.rijksmuseum.domain.model.ArtObjectDetails
import kotlinx.coroutines.flow.Flow

interface ArtObjectsRepository {

    fun getArtObjects(page: Int, pageSize: Int): Flow<Result<ArtObjectsPage>>

    fun getArtObjectDetails(objectNumber: String): Flow<Result<ArtObjectDetails>>
}