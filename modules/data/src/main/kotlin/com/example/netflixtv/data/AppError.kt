package com.example.netflixtv.data

sealed class AppError(
    override val message: String,
    val userMessage: String = message,
    override val cause: Throwable? = null
) : Throwable(message, cause) {
    data class Network(
        override val message: String,
        val httpCode: Int? = null,
        override val cause: Throwable? = null
    ) : AppError(message = message, userMessage = "Network error. Please check your connection and try again.", cause = cause)

    data class Parse(
        override val message: String,
        override val cause: Throwable? = null
    ) : AppError(message = message, userMessage = "Unable to parse data. Please try again later.", cause = cause)

    data class NotFound(
        override val message: String
    ) : AppError(message = message, userMessage = "Content not found.")

    data class Player(
        override val message: String,
        val errorCode: Int? = null,
        override val cause: Throwable? = null
    ) : AppError(message = message, userMessage = "Playback error. Please try again.", cause = cause)

    companion object {
        fun fromThrowable(e: Throwable): AppError = when (e) {
            is AppError -> e
            is java.net.UnknownHostException -> Network(message = "No internet connection", cause = e)
            is java.net.SocketTimeoutException -> Network(message = "Connection timeout", cause = e)
            is java.io.IOException -> Network(message = "Network error: ${e.message ?: "IO error"}", cause = e)
            else -> Network(message = e.message ?: "Unknown error", cause = e)
        }

        fun fromPlaybackError(errorCode: Int, message: String): AppError =
            Player(message = "Playback error $errorCode: $message", errorCode = errorCode)
    }
}
