package com.mews.guestroom.core.common.result

sealed interface DataResult<out T> {
    data class Success<T>(
        val value: T,
    ) : DataResult<T>

    data class Error(
        val message: String,
        val cause: Throwable? = null,
    ) : DataResult<Nothing>
}
