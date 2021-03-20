package com.cinnamon.cleanexplorers.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class User(
    @SerialName("id")
    val id: Int,
    @SerialName("firstName")
    val firstName: String,
    @SerialName("email")
    val email: String,
    @SerialName("dateBirth")
    val dateBirth: String
)