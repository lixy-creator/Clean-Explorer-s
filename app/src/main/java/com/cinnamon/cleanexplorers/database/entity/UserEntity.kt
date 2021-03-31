package com.cinnamon.cleanexplorers.database.entity


import androidx.room.*
import java.util.*

/**
 * Created by Fanny on 11/11/2020.
 **/
@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val firstName: String,
    val email: String,
    val token: String? = null,
    val type: String,
    val birthday: Date? = null,
    @ColumnInfo(name = "create_date")
    val createDate: Date? = null,
    val point: Int? = 0,
    @ColumnInfo(name = "nbr_spot_seen")
    val nbrCleanStopSeen: Int? = 0,
    @ColumnInfo(name = "last_connexion_date")
    val lastConnexionDate: Date = Date(),
    @ColumnInfo(name = "nbr_connexion")
    val nbrConnexion: Int = 0,
    val connected: Boolean = false
)

data class UserWithGarbage(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "garbageId"
    )
    val listGarbage: List<Garbage>
)

data class UserWithMission(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "missionId"
    )
    val missions: List<MissionEntity>
)
