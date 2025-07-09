package com.alexzh.rijksmuseum.data.remote

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
    private val repository = RemoteArtObjectsRepository(
        api = api
    )

    @Test
    fun `getArtObjects should emit data when data is available`() = runTest {
        val id = UUID.randomUUID().toString()
        val objectNumber = UUID.randomUUID().toString()
        val title = UUID.randomUUID().toString()
        val longTitle = UUID.randomUUID().toString()
        val response = ArtObjectsResponse(
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

        coEvery { api.getArtObjects(any(), any()) } returns response

        repository.getArtObjects(1, 20).test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)

            val success = awaitItem() as Result.Success
            assertThat(success.data.items.size).isEqualTo(1)
            assertThat(success.data.items.first().id).isEqualTo(id)
            assertThat(success.data.items.first().objectNumber).isEqualTo(objectNumber)
            assertThat(success.data.items.first().title).isEqualTo(title)
            assertThat(success.data.items.first().longTitle).isEqualTo(longTitle)
            assertThat(success.data.currentPage).isEqualTo(1)
            assertThat(success.data.totalCount).isEqualTo(1)

            awaitComplete()
        }
    }



    @Test
    fun `getArtObjects should emit Loading and UnauthorizedException when api key is not valid`() = runTest {
        val response = mockk<Response<ArtObjectsResponse>>(relaxed = true) {
            every { code() } returns 401
        }

        val expectedError = UnauthorizedException()
        coEvery { api.getArtObjects(any(), any()) } throws HttpException(response)

        repository.getArtObjects(1, 20).test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)

            val error = awaitItem() as Result.Error
            assertThat(error.cause.message).isEqualTo(expectedError.message)

            awaitComplete()
        }
    }

    @Test
    fun `getArtObjects should emit Loading and ApiException when server is unavailable`() = runTest {
        val response = mockk<Response<ArtObjectsResponse>>(relaxed = true) {
            every { code() } returns 500
        }

        val expectedError = ApiException()
        coEvery { api.getArtObjects(any(), any()) } throws HttpException(response)

        repository.getArtObjects(1, 20).test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)

            val error = awaitItem() as Result.Error
            assertThat(error.cause.message).isEqualTo(expectedError.message)

            awaitComplete()
        }
    }

    @Test
    fun `getArtObjects should emit error when no internet connection`() = runTest {
        val expectedError = NetworkException()

        coEvery { api.getArtObjects(any(), any()) } throws IOException()

        repository.getArtObjects(1, 20).test {
            assertThat(awaitItem()).isInstanceOf(Result.Loading::class.java)

            val error = awaitItem() as Result.Error
            assertThat(error.cause.message).isEqualTo(expectedError.message)

            awaitComplete()
        }
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