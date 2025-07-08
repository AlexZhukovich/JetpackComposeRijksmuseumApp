package com.alexzh.rijksmuseum.data.remote

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import app.cash.turbine.test
import com.alexzh.rijksmuseum.data.remote.model.ArtObjectDetailsResponse
import com.alexzh.rijksmuseum.data.remote.model.ArtObjectResponse
import com.alexzh.rijksmuseum.data.remote.model.ArtObjectsResponse
import com.alexzh.rijksmuseum.data.remote.model.LinksResponse
import com.alexzh.rijksmuseum.domain.Result
import com.alexzh.rijksmuseum.domain.exception.ApiException
import com.alexzh.rijksmuseum.domain.exception.ArtObjectNotFoundException
import com.alexzh.rijksmuseum.domain.exception.NetworkException
import com.alexzh.rijksmuseum.domain.exception.UnauthorizedException
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.util.UUID

class RemoteArtObjectsRepositoryTest {

    private val api = mockk<RijksmuseumApi>()
    private val pagingConfig = PagingConfig(
        pageSize = 20
    )
    private val pagingSourceFactory: () -> ArtObjectsPagingSource = {
        ArtObjectsPagingSource(api)
    }
    private val repository = RemoteArtObjectsRepository(
        api = api,
        pagingConfig = pagingConfig,
        pagingSourceFactory = pagingSourceFactory
    )

    @Test
    fun `getArtObjects should emit data when data is available`() = runTest {
        val id = UUID.randomUUID().toString()
        val objectNumber = UUID.randomUUID().toString()
        val title = UUID.randomUUID().toString()
        val longTitle = UUID.randomUUID().toString()

        coEvery { api.getArtObjects(any(), any()) } returns ArtObjectsResponse(
            artObjects = listOf(
                ArtObjectResponse(
                    links = LinksResponse(),
                    id = id,
                    objectNumber = objectNumber,
                    title = title,
                    longTitle = longTitle,
                    productionPlaces = emptyList()
                )
            ),
            count = 1
        )

        val pager = TestPager(
            config = pagingConfig,
            pagingSource = pagingSourceFactory()
        )

        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertThat(result.data.map { it.id }).containsExactly(id)
        assertThat(result.data.map { it.objectNumber }).containsExactly(objectNumber)
        assertThat(result.data.map { it.title }).containsExactly(title)
        assertThat(result.data.map { it.longTitle }).containsExactly(longTitle)
    }



    @Test
    fun `getArtObjects should emit Loading and UnauthorizedException when api key is not valid`() = runTest {
        val response = mockk<Response<ArtObjectsResponse>>(relaxed = true) {
            every { code() } returns 401
        }

        val expectedError = UnauthorizedException()
        coEvery { api.getArtObjects(any(), any()) } throws HttpException(response)

        val pager = TestPager(
            config = pagingConfig,
            pagingSource = pagingSourceFactory()
        )

        val result = pager.refresh() as PagingSource.LoadResult.Error
        assertThat(result.throwable.message).isEqualTo(expectedError.message)
    }

    @Test
    fun `getArtObjects should emit Loading and ApiException when server is unavailable`() = runTest {
        val response = mockk<Response<ArtObjectsResponse>>(relaxed = true) {
            every { code() } returns 500
        }

        val expectedError = ApiException()
        coEvery { api.getArtObjects(any(), any()) } throws HttpException(response)

        val pager = TestPager(
            config = pagingConfig,
            pagingSource = pagingSourceFactory()
        )

        val result = pager.refresh() as PagingSource.LoadResult.Error
        assertThat(result.throwable.message).isEqualTo(expectedError.message)
    }

    @Test
    fun `getArtObjects should emit error when no internet connection`() = runTest {
        val expectedError = NetworkException()

        coEvery { api.getArtObjects(any(), any()) } throws IOException()

        val pager = TestPager(
            config = pagingConfig,
            pagingSource = pagingSourceFactory()
        )

        val result = pager.refresh() as PagingSource.LoadResult.Error
        assertThat(result.throwable.message).isEqualTo(expectedError.message)
    }

    @Test
    fun `getArtObjects should emit error when no error happened`() = runTest {
        val error = RuntimeException()

        coEvery { api.getArtObjects(any(), any()) } throws error

        val pager = TestPager(
            config = pagingConfig,
            pagingSource = pagingSourceFactory()
        )

        val result = pager.refresh() as PagingSource.LoadResult.Error
        assertThat(result.throwable).isEqualTo(error)
    }

    @Test
    fun `getArtObjectDetails should emit Loading and Success when data is available`() = runTest {
        val id = UUID.randomUUID().toString()
        val objectNumber = UUID.randomUUID().toString()
        val title = UUID.randomUUID().toString()
        val longTitle = UUID.randomUUID().toString()

        val response = ArtObjectDetailsResponse(
            artObject = ArtObjectResponse(
                links = LinksResponse(),
                id = id,
                objectNumber = objectNumber,
                title = title,
                longTitle = longTitle,
                productionPlaces = emptyList()
            )
        )

        coEvery { api.getArtObjectDetails(objectNumber) } returns response

        repository.getArtObjectDetails(objectNumber).test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)

            val success = awaitItem() as Result.Success
            assertThat(success.data.id).isEqualTo(id)
            assertThat(success.data.objectNumber).isEqualTo(objectNumber)
            assertThat(success.data.title).isEqualTo(title)
            assertThat(success.data.longTitle).isEqualTo(longTitle)

            awaitComplete()
        }
    }

    @Test
    fun `getArtObjectDetails should emit Loading and ArtObjectNotFoundException when response has no data`() = runTest {
        val objectNumber = UUID.randomUUID().toString()
        val response = ArtObjectDetailsResponse(
            artObject = null
        )
        val expectedError = ArtObjectNotFoundException()
        coEvery { api.getArtObjectDetails(objectNumber) } returns response

        repository.getArtObjectDetails(objectNumber).test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)

            val error = awaitItem() as Result.Error
            assertThat(error.cause.message).isEqualTo(expectedError.message)

            awaitComplete()
        }
    }

    @Test
    fun `getArtObjectDetails should emit Loading and UnauthorizedException when api key is not valid`() = runTest {
        val objectNumber = UUID.randomUUID().toString()
        val response = mockk<Response<ArtObjectResponse>>(relaxed = true) {
            every { code() } returns 401
        }

        val expectedError = UnauthorizedException()
        coEvery { api.getArtObjectDetails(objectNumber) } throws HttpException(response)

        repository.getArtObjectDetails(objectNumber).test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)

            val error = awaitItem() as Result.Error
            assertThat(error.cause.message).isEqualTo(expectedError.message)

            awaitComplete()
        }
    }

    @Test
    fun `getArtObjectDetails should emit Loading and ApiException when server is unavailable`() = runTest {
        val objectNumber = UUID.randomUUID().toString()
        val response = mockk<Response<ArtObjectResponse>>(relaxed = true) {
            every { code() } returns 500
        }

        val expectedError = ApiException()
        coEvery { api.getArtObjectDetails(objectNumber) } throws HttpException(response)

        repository.getArtObjectDetails(objectNumber).test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)

            val error = awaitItem() as Result.Error
            assertThat(error.cause.message).isEqualTo(expectedError.message)

            awaitComplete()
        }
    }

    @Test
    fun `getArtObjectDetails should emit Loading and NetworkException when no internet connection`() = runTest {
        val objectNumber = UUID.randomUUID().toString()

        val expectedError = NetworkException()
        coEvery { api.getArtObjectDetails(objectNumber) } throws IOException()

        repository.getArtObjectDetails(objectNumber).test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)

            val error = awaitItem() as Result.Error
            assertThat(error.cause.message).isEqualTo(expectedError.message)

            awaitComplete()
        }
    }
}