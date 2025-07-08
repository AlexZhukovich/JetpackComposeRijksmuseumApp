package com.alexzh.rijksmuseum.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.alexzh.rijksmuseum.domain.exception.ApiException
import com.alexzh.rijksmuseum.domain.exception.NetworkException
import com.alexzh.rijksmuseum.domain.exception.UnauthorizedException
import com.alexzh.rijksmuseum.data.mapper.toArtObject
import com.alexzh.rijksmuseum.domain.model.ArtObject
import okio.IOException
import retrofit2.HttpException

class ArtObjectsPagingSource(
    private val api: RijksmuseumApi
) : PagingSource<Int, ArtObject>() {

    companion object {
        // Rijksmuseum API limit: 10,000
        private const val MAX_ITEMS_LIMIT = 10_000
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArtObject> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            val response = api.getArtObjects(page = page, pageSize = pageSize)
            val artObjects = response.artObjects.map { it.toArtObject() }

            val totalItems = response.count
            val currentOffset = (page - 1) * pageSize
            val loadedItems = currentOffset + artObjects.size

            val isEndOfList = when {
                artObjects.isEmpty() -> true
                loadedItems >= totalItems -> true
                currentOffset >= MAX_ITEMS_LIMIT -> true
                else -> false
            }

            LoadResult.Page(
                data = artObjects,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (isEndOfList) null else page + 1
            )
        } catch (httpException: HttpException) {
            val error = when (httpException.code()) {
                401 -> UnauthorizedException()
                else -> ApiException(httpException.cause)
            }
            LoadResult.Error(error)
        } catch (ioException: IOException) {
            LoadResult.Error(NetworkException(ioException))
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArtObject>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}