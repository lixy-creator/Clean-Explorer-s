package com.cinnamon.cleanexplorers.api.exception

sealed class ApiException(message: String, cause: Throwable?) : Exception(message, cause)

data class TechnicalException(override val cause: Throwable?) :
    ApiException(message = "An unknown problem occurred", cause = cause)

data class WrongLoginOrPassword(override val cause: Throwable?) :
    ApiException(message = "Wrong account or password", cause = cause)