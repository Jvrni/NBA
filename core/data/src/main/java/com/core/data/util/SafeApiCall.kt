package com.core.data.util

import android.util.Log
import retrofit2.HttpException
import com.core.domain.result.AppResult
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val TAG = "SafeApiCall"

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): AppResult<T> {
    return try {
        AppResult.Success(apiCall())
    } catch (e: HttpException) {
        handleHttpException(e)
    } catch (e: UnknownHostException) {
        handleNetworkException("No internet connection", e)
    } catch (e: SocketTimeoutException) {
        handleNetworkException("Request timeout", e)
    } catch (e: IOException) {
        handleNetworkException("Network error", e)
    } catch (e: Exception) {
        handleUnknownException(e)
    }
}

private fun <T> handleHttpException(e: HttpException): AppResult<T> {
    val code = e.code()
    val message = when (code) {
        400 -> "Bad request"
        401 -> "Unauthorized. Please check your API key."
        403 -> "Forbidden"
        404 -> "Resource not found"
        408 -> "Request timeout"
        429 -> "Too many requests. Please try again later."
        in 500..599 -> "Server error. Please try again later."
        else -> e.message() ?: "HTTP error occurred"
    }

    Log.e(TAG, "HTTP error [$code]: $message", e)

    return AppResult.Error(
        message = message,
        code = code,
        throwable = e
    )
}

private fun <T> handleNetworkException(
    userMessage: String,
    e: IOException
): AppResult<T> {
    Log.e(TAG, "Network error: $userMessage", e)

    return AppResult.Error(
        message = "$userMessage. Please check your connection.",
        throwable = e
    )
}

private fun <T> handleUnknownException(e: Exception): AppResult<T> {
    val message = e.message ?: "An unexpected error occurred"
    Log.e(TAG, "Unexpected error: $message", e)

    return AppResult.Error(
        message = message,
        throwable = e
    )
}