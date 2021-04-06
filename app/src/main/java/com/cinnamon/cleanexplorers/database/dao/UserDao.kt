package com.cinnamon.cleanexplorers.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cinnamon.cleanexplorers.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.*


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(localUser: UserEntity)

    @Query("DELETE FROM user")
    suspend fun deleteAll()

    @Query("SELECT * FROM user WHERE connected=1")
    fun getUserConnected(): Flow<UserEntity>

    @Query("SELECT * FROM user WHERE userId=:id")
    fun getUser(id: String): Flow<UserEntity?>

    @Query("SELECT EXISTS (SELECT 1 FROM user WHERE userId = :userId)")
    fun exists(userId: String): Flow<Boolean>

    @Query("UPDATE user SET nbr_spot_seen=nbr_spot_seen+1 WHERE userId=:userId")
    suspend fun setNbrCleanStop(userId: String)

    @Query("SELECT nbr_spot_seen FROM user WHERE userId=:userId")
    fun getNbrCleanStop(userId: String): Flow<Int>

    @Query("UPDATE user SET connected=:connected WHERE userId=:userId")
    suspend fun setConnectUser(userId: String, connected: Boolean)

    @Query("UPDATE user SET connected=1, nbr_connexion=nbr_connexion+1, last_connexion_date=:date WHERE userId=:userId")
    suspend fun setNbrConnexion(userId: String, date: Date)

    @Query("SELECT nbr_connexion FROM user WHERE userId=:userId")
    fun getNbrConnexion(userId: String): LiveData<Int>

    @Query("UPDATE user SET point=point+:point WHERE userId=:userId")
    suspend fun addPointUser(userId: String, point: Int)
}