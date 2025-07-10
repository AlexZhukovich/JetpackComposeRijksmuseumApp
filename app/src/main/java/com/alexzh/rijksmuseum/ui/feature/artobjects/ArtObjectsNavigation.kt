package com.alexzh.rijksmuseum.ui.feature.artobjects

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object ArtObjectsDestination

fun NavGraphBuilder.artObjectsScreen(
    onNavigateToArtObjectDetails: (artObjectNumber: String) -> Unit
) {
    composable<ArtObjectsDestination> {
        ArtObjectsScreen(
            viewModel = koinViewModel<ArtObjectsViewModel>(),
            onNavigateToArtObjectDetails = onNavigateToArtObjectDetails
        )
    }
}