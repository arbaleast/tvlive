package com.example.netflixtv.data

/**
 * Standardized error type across the app.
 */
sealed class AppError(message: String) : Throwable(message) {
    data class Network(override val message: String) : AppError(message)
    data class Parse(override val message: String) : AppError(message)
    data class NotFound(override val message: String) : AppError(message)
    data class Unknown(override val message: String) : AppError(message)

    companion object {
        fun fromThrowable(e: Throwable): AppError = when (e) {
            is AppError -> e
            else -> Unknown(e.message ?: "Unknown error")
        }
    }
}
