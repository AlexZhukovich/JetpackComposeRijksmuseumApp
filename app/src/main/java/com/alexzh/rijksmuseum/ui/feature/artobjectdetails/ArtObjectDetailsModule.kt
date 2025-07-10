package com.alexzh.rijksmuseum.ui.feature.artobjectdetails

import com.alexzh.rijksmuseum.domain.ArtObjectsRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val artObjectDetailsModule = module {

    viewModel<ArtObjectDetailsViewModel> { parameters ->
        ArtObjectDetailsViewModel(
            get<ArtObjectsRepository>(),
            savedStateHandle = get()
        )
    }
}