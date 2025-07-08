package com.alexzh.rijksmuseum.domain

import androidx.paging.PagingData
import com.alexzh.rijksmuseum.domain.model.ArtObject
import kotlinx.coroutines.flow.Flow

interface ArtObjectsRepository {

    fun getArtObjects(): Flow<PagingData<ArtObject>>

    fun getArtObjectDetails(objectNumber: String): Flow<Result<ArtObject>>
}