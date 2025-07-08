package com.alexzh.rijksmuseum.data.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.alexzh.rijksmuseum.domain.ArtObjectsRepository
import com.alexzh.rijksmuseum.domain.Result
import com.alexzh.rijksmuseum.domain.exception.ApiException
import com.alexzh.rijksmuseum.domain.exception.ArtObjectNotFoundException
import com.alexzh.rijksmuseum.domain.exception.NetworkException
import com.alexzh.rijksmuseum.domain.exception.UnauthorizedException
import com.alexzh.rijksmuseum.data.mapper.toArtObject
import com.alexzh.rijksmuseum.domain.model.ArtObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException

class RemoteArtObjectsRepository(
    private val api: RijksmuseumApi,
    private val pagingConfig: PagingConfig,
    private val pagingSourceFactory: () -> ArtObjectsPagingSource
) : ArtObjectsRepository {

    override fun getArtObjects(): Flow<PagingData<ArtObject>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getArtObjectDetails(objectNumber: String): Flow<Result<ArtObject>> {
        return flow {
            emit(Result.Loading())

            try {
                val data = api.getArtObjectDetails(objectNumber)
                if (data.artObject != null) {
                    emit(Result.Success(data.artObject.toArtObject()))
                } else {
                    emit(Result.Error(ArtObjectNotFoundException()))
                }
            } catch (httpException: HttpException) {
                val error = when (httpException.code()) {
                    401 -> UnauthorizedException()
                    else -> ApiException(httpException.cause)
                }
                emit(
                    Result.Error(error)
                )
            } catch (ioException: IOException) {
                emit(Result.Error(NetworkException(ioException)))
            }
        }
    }
}