package com.cinnamon.cleanexplorers.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestLogin(
    @SerialName("login")
    val login: String,

    @SerialName("password")
    val password: String,
)