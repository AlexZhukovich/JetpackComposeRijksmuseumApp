package com.alexzh.rijksmuseum.ui.feature.artobjectdetails

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class ArtObjectDetailsDestination(
    val artObjectNumber: String
)

fun NavGraphBuilder.artObjectDetailsScreen(
    onNavigateToArtObjects: () -> Unit
) {
    composable<ArtObjectDetailsDestination> {
        ArtObjectDetailsScreen(
            viewModel = koinViewModel<ArtObjectDetailsViewModel>(),
            onNavigateToArtObjects = onNavigateToArtObjects
        )
    }
}

fun NavController.navigateToArtObjectDetails(
    artObjectNumber: String
) {
    navigate(
        ArtObjectDetailsDestination(
            artObjectNumber = artObjectNumber
        )
    )
}