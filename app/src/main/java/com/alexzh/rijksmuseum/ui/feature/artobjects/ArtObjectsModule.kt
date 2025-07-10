package com.alexzh.rijksmuseum.ui.feature.artobjects

import com.alexzh.rijksmuseum.domain.ArtObjectsRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val artObjectsModule = module {

    viewModel<ArtObjectsViewModel> {
        ArtObjectsViewModel(
            repository = get<ArtObjectsRepository>()
        )
    }
}