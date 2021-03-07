package com.cinnamon.cleanexplorers.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CleanStop(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("image")
    val image: String,
    @SerialName("description")
    val description: String,
)