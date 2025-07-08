package com.alexzh.rijksmuseum.domain.exception

class NetworkException(
    cause: Throwable? = null
): Exception("Network connection failed", cause)