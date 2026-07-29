package com.otprelay.app.core.network

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class HttpError(val code: Int, val message: String?) : ApiResult<Nothing>
    data class NetworkError(val exception: Throwable) : ApiResult<Nothing>
    data class SerializationError(val exception: Throwable) : ApiResult<Nothing>
    data class UnknownError(val exception: Throwable) : ApiResult<Nothing>
}
