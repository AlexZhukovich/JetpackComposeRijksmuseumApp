package com.alexzh.rijksmuseum.ui.feature.artobjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexzh.rijksmuseum.domain.ArtObjectsRepository
import com.alexzh.rijksmuseum.domain.Result
import com.alexzh.rijksmuseum.domain.exception.NetworkException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class ArtObjectsViewModel(
    private val repository: ArtObjectsRepository
): ViewModel() {
    companion object {
        const val LOAD_PAGE_SIZE = 20
    }

    private val _uiState = MutableStateFlow(ArtObjectsUiState())
    val uiState = _uiState.asStateFlow()

    private var currentPage = 0

    fun onEvent(event: ArtObjectsEvent) {
        when (event) {
            ArtObjectsEvent.Load -> loadData()
        }
    }

    private fun loadData() {
        if (_uiState.value.isLoading || !_uiState.value.canLoadMore) {
            return
        }

        viewModelScope.launch {
            val newPageNumber = currentPage + 1
            repository.getArtObjects(newPageNumber, LOAD_PAGE_SIZE)
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = true,
                                    error = null
                                )
                            }
                        }
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    items = it.items + result.data.items,
                                    canLoadMore = result.data.hasNextPage,
                                    error = null
                                )
                            }
                            currentPage = newPageNumber
                        }
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = mapErrorToArtObjectsError(result.cause)
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun mapErrorToArtObjectsError(error: Throwable): ArtObjectsError {
        return when (error) {
            is NetworkException -> ArtObjectsError.CONNECTIVITY_ERROR
            else -> ArtObjectsError.LOADING_ERROR
        }
    }
}