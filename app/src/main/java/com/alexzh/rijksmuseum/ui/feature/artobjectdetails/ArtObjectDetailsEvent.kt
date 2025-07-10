package com.alexzh.rijksmuseum.ui.feature.artobjectdetails

sealed class ArtObjectDetailsEvent {
    object Load : ArtObjectDetailsEvent()
}