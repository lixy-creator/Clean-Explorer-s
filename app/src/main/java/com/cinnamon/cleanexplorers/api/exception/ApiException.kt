package com.cinnamon.cleanexplorers.api.exception

import java.lang.Exception

sealed class ApiException(message:String, cause:Throwable?):Exception(message,cause)

data class TechnicalException(override val cause: Throwable?) :
    ApiException(message = "An unknown problem occurred", cause = cause)
