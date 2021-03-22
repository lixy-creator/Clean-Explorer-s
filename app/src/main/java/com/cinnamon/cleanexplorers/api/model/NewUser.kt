package com.cinnamon.cleanexplorers.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewUser(
    @SerialName("firstName")
    val firstName: String,
    @SerialName("email")
    val email: String,
    @SerialName("dateBirth")
    val dateBirth: String,
    @SerialName("password")
    val password:String,
)
