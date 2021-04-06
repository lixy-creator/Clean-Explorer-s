package com.cinnamon.cleanexplorers.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mission")
data class MissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val missionId: Long,
    val userId: String,
    val name: String,
    @ColumnInfo(name = "nbr_time")
    val nbrTime: Int,
    @ColumnInfo(name = "mission_point")
    val point: Int,
    val valid: Boolean = false
)
