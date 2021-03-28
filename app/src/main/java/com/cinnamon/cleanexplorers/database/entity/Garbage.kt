package com.cinnamon.cleanexplorers.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "garbage")
data class Garbage(
    @PrimaryKey(autoGenerate = true)
    val id: Int?=0,
    val name: String,
    val packaging: String,
    val quantity: Int?=0,
    val createDate: Date,
)
