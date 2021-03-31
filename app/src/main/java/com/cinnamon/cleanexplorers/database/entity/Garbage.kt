package com.cinnamon.cleanexplorers.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "garbage")
data class Garbage(
    @PrimaryKey(autoGenerate = true)
    val garbageId: Int = 0,
    val userId: String,
    val name: String,
    val packaging: String,
    val quantity: Int = 0,
    @ColumnInfo(name = "create_date")
    val createDate: Date,
)
