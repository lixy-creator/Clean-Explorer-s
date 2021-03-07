package com.cinnamon.cleanexplorers.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class User(
    @SerialName("id")
    val id: Int,
    @SerialName("lastName")
    val lastName: String,
    val firstname: String,
    val email: String,
    val dateBirth: String
)