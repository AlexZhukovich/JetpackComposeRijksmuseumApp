package com.alexzh.rijksmuseum.domain

sealed class Result<T> {
    class Loading<T>: Result<T>()
    class Success<T>(val data: T): Result<T>()
    class Error<T>(val cause: Exception): Result<T>()
}