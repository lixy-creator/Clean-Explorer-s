package com.cinnamon.cleanexplorers.api.model

sealed class Result<out SuccessType, out Failure : Throwable>

data class Success<out Success>(val value: Success) : Result<Success, Nothing>()
data class Failure<out Failure : Throwable>(val reason: Failure) : Result<Nothing, Failure>()
