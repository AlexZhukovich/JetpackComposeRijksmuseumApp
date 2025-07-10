package com.alexzh.rijksmuseum.ui.feature.artobjectdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexzh.rijksmuseum.domain.ArtObjectsRepository
import com.alexzh.rijksmuseum.domain.Result
import com.alexzh.rijksmuseum.domain.exception.ArtObjectNotFoundException
import com.alexzh.rijksmuseum.domain.exception.NetworkException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArtObjectDetailsViewModel(
    private val repository: ArtObjectsRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val objectNumber: String = savedStateHandle.get<String>("artObjectNumber") ?: ""

    private val _uiState = MutableStateFlow(ArtObjectDetailsUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: ArtObjectDetailsEvent) {
        when (event) {
            ArtObjectDetailsEvent.Load -> fetchArtObjectDetails(objectNumber)
        }
    }

    private fun fetchArtObjectDetails(objectNumber: String) {
        if (objectNumber.isEmpty()) {
            _uiState.update { it.copy(error = ArtObjectDetailsError.LOADING_ERROR) }
            return
        }

        viewModelScope.launch {
            repository.getArtObjectDetails(objectNumber)
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.update { it.copy(isLoading = true, error = null, data = null) }
                        }
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = mapExceptionToArtObjectDetailsError(result.cause)
                                )
                            }
                        }
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(isLoading = false, data = result.data, error = null)
                            }
                        }
                    }
                }
        }
    }

    private fun mapExceptionToArtObjectDetailsError(exception: Exception): ArtObjectDetailsError {
        return when (exception) {
            is ArtObjectNotFoundException -> ArtObjectDetailsError.NOT_FOUND
            is NetworkException -> ArtObjectDetailsError.CONNECTIVITY_ERROR
            else -> ArtObjectDetailsError.LOADING_ERROR
        }
    }
}