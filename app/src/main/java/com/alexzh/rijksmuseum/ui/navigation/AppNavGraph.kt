package com.alexzh.rijksmuseum.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.alexzh.rijksmuseum.ui.feature.artobjectdetails.artObjectDetailsScreen
import com.alexzh.rijksmuseum.ui.feature.artobjectdetails.navigateToArtObjectDetails
import com.alexzh.rijksmuseum.ui.feature.artobjects.ArtObjectsDestination
import com.alexzh.rijksmuseum.ui.feature.artobjects.artObjectsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: Any = ArtObjectsDestination
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        artObjectsScreen(
           onNavigateToArtObjectDetails = { navController.navigateToArtObjectDetails(it) }
        )
        artObjectDetailsScreen(
            onNavigateToArtObjects = { navController.navigateUp() }
        )
    }
}