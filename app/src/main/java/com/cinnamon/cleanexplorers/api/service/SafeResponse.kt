package com.cinnamon.cleanexplorers.api.service

import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.Result
import com.cinnamon.cleanexplorers.api.model.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> T
): Result<T, Throwable> {
    return withContext(dispatcher) {
        try {
            Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            Failure(throwable)
        }
    }
}