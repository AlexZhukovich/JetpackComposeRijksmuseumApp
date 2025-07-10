package com.alexzh.rijksmuseum.ui.feature.artobjectdetails

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexzh.rijksmuseum.R
import com.alexzh.rijksmuseum.domain.model.ArtObjectDetails
import com.alexzh.rijksmuseum.domain.model.ImageInformation
import com.alexzh.rijksmuseum.ui.components.Error
import com.alexzh.rijksmuseum.ui.components.Loading
import com.alexzh.rijksmuseum.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtObjectDetailsScreen(
    viewModel: ArtObjectDetailsViewModel,
    onNavigateToArtObjects: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (uiState.data == null && !uiState.isLoading) {
            viewModel.onEvent(ArtObjectDetailsEvent.Load)
        }
    }

    ArtObjectDetailsScreenContent(
        uiState = uiState,
        onNavigateToArtObjects = onNavigateToArtObjects,
        onRetryToFetchData = { viewModel.onEvent(ArtObjectDetailsEvent.Load) }
    )
}

@Composable
private fun ArtObjectDetailsScreenContent(
    uiState: ArtObjectDetailsUiState,
    onNavigateToArtObjects: () -> Unit,
    onRetryToFetchData: () -> Unit
) {
    Scaffold(
        topBar = {
            ArtObjectDetailsScreenTopAppBar(
                onNavigateToArtObjects = onNavigateToArtObjects
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> Loading(text = stringResource(R.string.artObjectDetailsScreen_loading_label))
            uiState.error != null -> {
                Error(
                    title = stringResource(uiState.error.localizedMessage),
                    onRetry = onRetryToFetchData
                )
            }
            uiState.data != null -> {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArtObjectImage(
                        url = uiState.data.webImage?.url ?: "",
                        contentDescription = uiState.data.title,
                    )

                    Text(
                        uiState.data.longTitle,
                        style = MaterialTheme.typography.titleMedium
                    )

                    uiState.data.plaqueDescriptionEnglish?.let {
                        Text(it)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtObjectDetailsScreenTopAppBar(
    onNavigateToArtObjects: () -> Unit
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(
                onClick = onNavigateToArtObjects
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.common_navigateUp_label)
                )
            }
        }
    )
}

@Composable
private fun ArtObjectImage(
    url: String,
    contentDescription: String,
) {
    if (url.isNotEmpty()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            contentScale = ContentScale.Fit,
            onError = { error ->
                Log.e("ImageDebug", "Image failed to load: $url", error.result.throwable)
            }
        )
    }
}

@PreviewLightDark
@Composable
fun Preview_ArtObjectDetailsScreenContentInLoadingState() {
    AppTheme {
        ArtObjectDetailsScreenContent(
            uiState = ArtObjectDetailsUiState(isLoading = true),
            onNavigateToArtObjects = { },
            onRetryToFetchData = { }
        )
    }
}

@PreviewLightDark
@Composable
fun Preview_ArtObjectDetailsScreenContentInErrorState() {
    AppTheme {
        ArtObjectDetailsScreenContent(
            uiState = ArtObjectDetailsUiState(error = ArtObjectDetailsError.LOADING_ERROR),
            onNavigateToArtObjects = { },
            onRetryToFetchData = { }
        )
    }
}

@PreviewLightDark
@Composable
fun Preview_ArtObjectDetailsScreenContentInSuccessfulStateWithAvailableImages() {
    val state = ArtObjectDetailsUiState(
        data = ArtObjectDetails(
            id = "en-BK-1975-81",
            objectNumber = "BK-1975-81",
            title = "Cupboard",
            longTitle = "Cupboard, Herman Doomer, c. 1635 - c. 1645",
            webImage = ImageInformation(
                id = "94d81c2d-603d-45ff-a40c-9d0d043ee64a",
                width = 5958,
                height = 6805,
                url = "https://lh3.googleusercontent.com/ZYQ7IcfJ45yQOPnmhzBkZK2mc2F_e7bUMDgKaY-miSl0f8y3o-Q--H3R81q-2q1cfqFqoDlDgyLDW3OHJqin_ugnB_KRIfZaV-9xX2Y=s0"
            ),
            plaqueDescriptionEnglish = "Among the possessions left by cabinetmaker Herman Doomer of Amsterdam was a costly ‘large ebony cupboard inlaid with mother-of-pearl’. It was probably this piece of furniture, which is veneered entirely with ebony and decorated with mother-of-pearl inlay. With its diagonally protruding corners and twisted columns, Doomer’s cupboard was far ahead of its time."
        )
    )

    AppTheme {
        ArtObjectDetailsScreenContent(
            uiState = state,
            onNavigateToArtObjects = { },
            onRetryToFetchData = { }
        )
    }
}