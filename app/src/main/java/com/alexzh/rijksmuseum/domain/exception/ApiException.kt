package com.alexzh.rijksmuseum.domain.exception

class ApiException(
    cause: Throwable? = null
): Exception("API exception", cause)