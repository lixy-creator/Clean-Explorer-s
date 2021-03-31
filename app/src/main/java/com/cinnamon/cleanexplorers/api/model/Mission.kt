package com.cinnamon.cleanexplorers.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Mission(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("nbr_time")
    val nbrTime: Int,
    @SerialName("category")
    val category: String,
    @SerialName("point")
    val point: Int,
)