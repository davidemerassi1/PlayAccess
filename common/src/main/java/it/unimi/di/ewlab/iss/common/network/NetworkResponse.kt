package it.unimi.di.ewlab.iss.common.network

import java.io.IOException



sealed class NetworkResponse<out T : Any> {
    /**
     * Success response with body
     */
    data class Success<T : Any>(val body: T) : NetworkResponse<T>()

    /**
     * Network error
     */
    data class NetworkError(val error: IOException) : NetworkResponse<Nothing>()

    /**
     * For example, json parsing error
     */
    data class UnknownError(val error: Throwable, val code: Int) : NetworkResponse<Nothing>()
}
