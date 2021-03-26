package com.cinnamon.cleanexplorers.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Email(
    @SerialName("email")
    val email: String
)
