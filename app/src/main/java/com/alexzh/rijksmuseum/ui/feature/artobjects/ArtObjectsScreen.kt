package com.alexzh.rijksmuseum.ui.feature.artobjects

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexzh.rijksmuseum.R
import com.alexzh.rijksmuseum.domain.model.ArtObject
import com.alexzh.rijksmuseum.ui.components.Error
import com.alexzh.rijksmuseum.ui.components.Loading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtObjectsScreen(
    viewModel: ArtObjectsViewModel,
    onNavigateToArtObjectDetails: (artObjectNumber: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (uiState.items.isEmpty() && !uiState.isLoading) {
            viewModel.onEvent(ArtObjectsEvent.Load)
        }
    }

    ArtObjectsScreenContent(
        uiState = uiState,
        onLoad = { viewModel.onEvent(ArtObjectsEvent.Load) },
        onNavigateToArtObjectDetails = onNavigateToArtObjectDetails
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtObjectsScreenContent(
    uiState: ArtObjectsUiState,
    onLoad: () -> Unit,
    onNavigateToArtObjectDetails: (artObjectNumber: String) -> Unit
) {
    val gridState = rememberLazyGridState()

    Scaffold(
        topBar = { ArtObjectsScreenTopAppBar() },
    ) { innerPadding ->
        val initialError = uiState.initialLoadingError()

        when {
            uiState.isInitialLoading() -> {
                Loading(text = stringResource(R.string.artObjectsScreen_initialLoading_label))
            }
            initialError != null -> {
                Error(
                    title = stringResource(initialError.localizedMessage),
                    onRetry = onLoad
                )
            }
            else -> {
                ArtObjectsGird(
                    items = uiState.items,
                    gridState = gridState,
                    modifier = Modifier.padding(innerPadding),
                    onItemClick = onNavigateToArtObjectDetails,
                    isLoadingMore = uiState.isLoadingMoreItems(),
                    loadMoreError = uiState.loadMoreError(),
                    onLoadMore = onLoad
                )
            }
        }
    }

    LoadMoreOnScroll(
        gridState = gridState,
        uiState = uiState,
        onLoad = onLoad
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtObjectsScreenTopAppBar() {
    TopAppBar(
        title = { Text(stringResource(R.string.artObjectsScreen_header_label)) }
    )
}

@Composable
private fun ArtObjectsGird(
    modifier: Modifier = Modifier,
    items: List<ArtObject>,
    gridState: LazyGridState,
    onItemClick: (String) -> Unit,
    isLoadingMore: Boolean,
    loadMoreError: ArtObjectsError?,
    onLoadMore: () -> Unit,
    gridCells: GridCells = GridCells.Fixed(2),
) {
    LazyVerticalGrid(
        columns = gridCells,
        state = gridState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(
            items = items,
            key = { it.id }
        ) { artObject ->
            ArtObjectItem(
                artObject = artObject,
                onItemClick = onItemClick
            )
        }

        when {
            isLoadingMore -> {
                item(span = { GridItemSpan(2) }) {
                    LoadingItem()
                }
            }
            loadMoreError != null -> {
                item(span = { GridItemSpan(2) }) {
                    ErrorItem(
                        message = stringResource(loadMoreError.localizedMessage),
                        onRetry = onLoadMore
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ArtObjectItem(
    artObject: ArtObject,
    onItemClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onItemClick(artObject.objectNumber) }
    ) {
        Column {
            val imageUrl = artObject.webImage?.url ?: artObject.headerImage?.url
            imageUrl?.let {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = artObject.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.FillWidth,
                    onError = { error ->
                        Log.e("ImageDebug", "Image failed to load: $imageUrl", error.result.throwable)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = artObject.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ErrorItem(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1.0f)
            )
            Button(
                onClick = onRetry,
            ) {
                Text(
                    text = stringResource(R.string.common_tryAgainButton_label)
                )
            }
        }
    }
}

@Composable
private fun LoadMoreOnScroll(
    gridState: LazyGridState,
    uiState: ArtObjectsUiState,
    onLoad: () -> Unit,
    loadMoreThreshold: Int = 3
) {
    LaunchedEffect(gridState, uiState.items.size) {
        snapshotFlow {
            gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect { lastVisibleItemIndex ->
            if (uiState.items.isNotEmpty() &&
                lastVisibleItemIndex != null &&
                lastVisibleItemIndex >= uiState.items.size - loadMoreThreshold &&
                !uiState.isLoading &&
                uiState.canLoadMore
            ) {
                onLoad()
            }
        }
    }
}
