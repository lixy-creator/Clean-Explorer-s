package com.cinnamon.cleanexplorers.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
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
    val description: String? = null,
    @SerialName("url")
    val url: String
) : Parcelable