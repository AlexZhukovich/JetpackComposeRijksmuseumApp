package com.alexzh.rijksmuseum.ui.feature.artobjectdetails

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.alexzh.rijksmuseum.domain.ArtObjectsRepository
import com.alexzh.rijksmuseum.domain.Result
import com.alexzh.rijksmuseum.domain.exception.ApiException
import com.alexzh.rijksmuseum.domain.exception.ArtObjectNotFoundException
import com.alexzh.rijksmuseum.domain.exception.NetworkException
import com.alexzh.rijksmuseum.domain.exception.UnauthorizedException
import com.alexzh.rijksmuseum.utils.StandardDispatcherRule
import com.alexzh.rijksmuseum.utils.generator.domain.generateArtObjectDetails
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ArtObjectDetailsViewModelTest {
    companion object {
        private const val TEST_ART_OBJECT_NUMBER = "TEST_ART_OBJECT_NUMBER"
    }

    @get:Rule
    val dispatcherRule = StandardDispatcherRule()

    private val repository = mockk<ArtObjectsRepository>()
    private val savedStateHandle = SavedStateHandle().apply {
        set("artObjectNumber", TEST_ART_OBJECT_NUMBER)
    }
    private val viewModel = ArtObjectDetailsViewModel(repository, savedStateHandle)

    @Test
    fun `should emit loading and data states when data is available`() = runTest {
        val artObjectDetails = generateArtObjectDetails()

        every { repository.getArtObjectDetails(TEST_ART_OBJECT_NUMBER) } returns flowOf(
            Result.Loading(),
            Result.Success(artObjectDetails)
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState())
            viewModel.onEvent(ArtObjectDetailsEvent.Load)

            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState(isLoading = true))
            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState(data = artObjectDetails))
        }
    }

    @Test
    fun `should emit loading and item not found error states when data the objectNumber is not available`() = runTest {
        every { repository.getArtObjectDetails(TEST_ART_OBJECT_NUMBER) } returns flowOf(
            Result.Loading(),
            Result.Error(ArtObjectNotFoundException())
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState())
            viewModel.onEvent(ArtObjectDetailsEvent.Load)

            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState(isLoading = true))
            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState(error = ArtObjectDetailsError.NOT_FOUND))
        }
    }

    @Test
    fun `should emit loading and connectivity error states when server is not available`() = runTest {
        every { repository.getArtObjectDetails(TEST_ART_OBJECT_NUMBER) } returns flowOf(
            Result.Loading(),
            Result.Error(NetworkException())
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState())
            viewModel.onEvent(ArtObjectDetailsEvent.Load)

            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState(isLoading = true))
            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState(error = ArtObjectDetailsError.CONNECTIVITY_ERROR))
        }
    }

    @Test
    fun `should emit loading and loading error states when api key is incorrect`() = runTest {
        every { repository.getArtObjectDetails(TEST_ART_OBJECT_NUMBER) } returns flowOf(
            Result.Loading(),
            Result.Error(UnauthorizedException())
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState())
            viewModel.onEvent(ArtObjectDetailsEvent.Load)

            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState(isLoading = true))
            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState(error = ArtObjectDetailsError.LOADING_ERROR))
        }
    }

    @Test
    fun `should emit loading and loading error states when api returns error`() = runTest {
        every { repository.getArtObjectDetails(TEST_ART_OBJECT_NUMBER) } returns flowOf(
            Result.Loading(),
            Result.Error(ApiException())
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState())
            viewModel.onEvent(ArtObjectDetailsEvent.Load)

            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState(isLoading = true))
            assertThat(awaitItem()).isEqualTo(ArtObjectDetailsUiState(error = ArtObjectDetailsError.LOADING_ERROR))
        }
    }
}
