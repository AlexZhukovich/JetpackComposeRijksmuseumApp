package com.alexzh.rijksmuseum.ui.feature.artobjects

import app.cash.turbine.test
import com.alexzh.rijksmuseum.domain.ArtObjectsRepository
import com.alexzh.rijksmuseum.domain.Result
import com.alexzh.rijksmuseum.domain.exception.ApiException
import com.alexzh.rijksmuseum.domain.exception.NetworkException
import com.alexzh.rijksmuseum.domain.exception.UnauthorizedException
import com.alexzh.rijksmuseum.utils.StandardDispatcherRule
import com.alexzh.rijksmuseum.utils.generator.domain.generateArtObjectsPage
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ArtObjectsViewModelTest {
    @get:Rule
    val dispatcherRule = StandardDispatcherRule()

    private val repository = mockk<ArtObjectsRepository>()
    private val viewModel = ArtObjectsViewModel(repository)

    @Test
    fun `should emit initial loading and and success when data is available`() = runTest {
        val artObjectsPage = generateArtObjectsPage()

        every { repository.getArtObjects(any(), any()) } returns flowOf(
            Result.Loading(),
            Result.Success(artObjectsPage)
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState())
            viewModel.onEvent(ArtObjectsEvent.Load)

            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState(isLoading = true))
            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState(items = artObjectsPage.items))
        }
    }

    @Test
    fun `should emit initial loading and connectivity error states when server is not available`() = runTest {
        every { repository.getArtObjects(any(), any()) } returns flowOf(
            Result.Loading(),
            Result.Error(NetworkException())
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState())
            viewModel.onEvent(ArtObjectsEvent.Load)

            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState(isLoading = true))
            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState(error = ArtObjectsError.CONNECTIVITY_ERROR))
        }
    }

    @Test
    fun `should emit initial loading and loading error states when api key is incorrect`() = runTest {
        every { repository.getArtObjects(any(), any()) } returns flowOf(
            Result.Loading(),
            Result.Error(UnauthorizedException())
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState())
            viewModel.onEvent(ArtObjectsEvent.Load)

            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState(isLoading = true))
            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState(error = ArtObjectsError.LOADING_ERROR))
        }
    }

    @Test
    fun `should emit initial loading and loading error states when api returns error`() = runTest {
        every { repository.getArtObjects(any(), any()) } returns flowOf(
            Result.Loading(),
            Result.Error(ApiException())
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState())
            viewModel.onEvent(ArtObjectsEvent.Load)

            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState(isLoading = true))
            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState(error = ArtObjectsError.LOADING_ERROR))
        }
    }

    @Test
    fun `should emit initial loading and success and load more loading and success`() = runTest {
        val artObjectsPage1 = generateArtObjectsPage(totalCount = 40)
        val artObjectsPage2 = generateArtObjectsPage(currentPage = 2, totalCount = 40, hasNextPage = false)

        every { repository.getArtObjects(any(), any()) } returnsMany listOf(
            flowOf(
                Result.Loading(),
                Result.Success(artObjectsPage1)
            ),
            flowOf(
                Result.Loading(),
                Result.Success(artObjectsPage2)
            )
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState())
            viewModel.onEvent(ArtObjectsEvent.Load)

            val initialLoading = awaitItem()
            assertThat(initialLoading).isEqualTo(ArtObjectsUiState(isLoading = true))
            assertThat(initialLoading.isInitialLoading()).isTrue()
            assertThat(initialLoading.isLoadingMoreItems()).isFalse()
            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState(items = artObjectsPage1.items))

            viewModel.onEvent(ArtObjectsEvent.Load)

            val loadMoreLoading = awaitItem()
            assertThat(loadMoreLoading).isEqualTo(
                ArtObjectsUiState(
                    isLoading = true,
                    items = artObjectsPage1.items
                )
            )
            assertThat(loadMoreLoading.isLoadingMoreItems()).isTrue()
            assertThat(loadMoreLoading.isInitialLoading()).isFalse()
            assertThat(awaitItem()).isEqualTo(
                ArtObjectsUiState(
                    items = artObjectsPage1.items + artObjectsPage2.items,
                    canLoadMore = false
                )
            )
        }
    }

    @Test
    fun `should emit initial loading and success and load more loading and connectivity error`() = runTest {
        val artObjectsPage = generateArtObjectsPage()

        every { repository.getArtObjects(any(), any()) } returnsMany listOf(
            flowOf(
                Result.Loading(),
                Result.Success(artObjectsPage)
            ),
            flowOf(
                Result.Loading(),
                Result.Error(NetworkException())
            )
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState())
            viewModel.onEvent(ArtObjectsEvent.Load)

            val initialLoading = awaitItem()
            assertThat(initialLoading).isEqualTo(ArtObjectsUiState(isLoading = true))
            assertThat(initialLoading.isInitialLoading()).isTrue()
            assertThat(initialLoading.isLoadingMoreItems()).isFalse()
            assertThat(awaitItem()).isEqualTo(ArtObjectsUiState(items = artObjectsPage.items))

            viewModel.onEvent(ArtObjectsEvent.Load)

            val loadMoreLoading = awaitItem()
            assertThat(loadMoreLoading).isEqualTo(
                ArtObjectsUiState(
                    isLoading = true,
                    items = artObjectsPage.items
                )
            )
            assertThat(loadMoreLoading.isLoadingMoreItems()).isTrue()
            assertThat(loadMoreLoading.isInitialLoading()).isFalse()
            assertThat(awaitItem()).isEqualTo(
                ArtObjectsUiState(
                    items = artObjectsPage.items,
                    error = ArtObjectsError.CONNECTIVITY_ERROR
                )
            )
        }
    }
}