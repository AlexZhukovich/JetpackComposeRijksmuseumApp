package com.alexzh.rijksmuseum.ui.feature.artobjects

sealed class ArtObjectsEvent {
    object Load : ArtObjectsEvent()
}