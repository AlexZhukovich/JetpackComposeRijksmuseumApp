package com.alexzh.rijksmuseum.data.remote

import com.alexzh.rijksmuseum.domain.ArtObjectsRepository
import com.alexzh.rijksmuseum.domain.Result
import com.alexzh.rijksmuseum.domain.exception.ApiException
import com.alexzh.rijksmuseum.domain.exception.ArtObjectNotFoundException
import com.alexzh.rijksmuseum.domain.exception.NetworkException
import com.alexzh.rijksmuseum.domain.exception.UnauthorizedException
import com.alexzh.rijksmuseum.data.mapper.toArtObject
import com.alexzh.rijksmuseum.data.mapper.toArtObjectDetails
import com.alexzh.rijksmuseum.data.remote.model.ArtObjectsPage
import com.alexzh.rijksmuseum.domain.model.ArtObject
import com.alexzh.rijksmuseum.domain.model.ArtObjectDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException

class RemoteArtObjectsRepository(
    private val api: RijksmuseumApi
) : ArtObjectsRepository {

    companion object {
        // Rijksmuseum API limit: 10,000
        private const val MAX_ITEMS_LIMIT = 10_000
    }

    override fun getArtObjects(
        page: Int,
        pageSize: Int
    ): Flow<Result<ArtObjectsPage>> {
        return flow {
            emit(Result.Loading())

            try {
                val response = api.getArtObjects(page = page, pageSize = pageSize)
                val artObjects = response.artObjects.map { it.toArtObject() }

                val totalItems = response.count
                val currentOffset = (page - 1) * pageSize
                val loadedItems = currentOffset + artObjects.size

                val hasNextPage = when {
                    artObjects.isEmpty() -> false
                    loadedItems >= totalItems -> false
                    currentOffset >= MAX_ITEMS_LIMIT -> false
                    else -> true
                }

                val artObjectsPage = ArtObjectsPage(
                    items = artObjects,
                    totalCount = totalItems,
                    currentPage = page,
                    hasNextPage = hasNextPage
                )

                emit(Result.Success(artObjectsPage))
            } catch (httpException: HttpException) {
                val error = when (httpException.code()) {
                    401 -> UnauthorizedException()
                    else -> ApiException(httpException.cause)
                }
                emit(Result.Error(error))
            } catch (ioException: IOException) {
                emit(Result.Error(NetworkException(ioException)))
            }
        }
    }

    override fun getArtObjectDetails(objectNumber: String): Flow<Result<ArtObjectDetails>> {
        return flow {
            emit(Result.Loading())

            try {
                val data = api.getArtObjectDetails(objectNumber)
                if (data.artObject != null) {
                    emit(Result.Success(data.artObject.toArtObjectDetails()))
                } else {
                    emit(Result.Error(ArtObjectNotFoundException()))
                }
            } catch (httpException: HttpException) {
                val error = when (httpException.code()) {
                    401 -> UnauthorizedException()
                    else -> ApiException(httpException.cause)
                }
                emit(Result.Error(error))
            } catch (ioException: IOException) {
                emit(Result.Error(NetworkException(ioException)))
            }
        }
    }
}