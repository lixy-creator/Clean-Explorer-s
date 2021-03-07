package com.cinnamon.cleanexplorers.model


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Created by Fanny on 11/11/2020.
 **/
@Entity(tableName = "user")
data class LocalUser(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val email: String,
    val token: String?,
    val type: String,
)
