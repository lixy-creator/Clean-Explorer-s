package com.cinnamon.cleanexplorers.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cinnamon.cleanexplorers.model.LocalUser
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(localUser: LocalUser)

    @Query("DELETE FROM user")
    suspend fun deleteAll()

    @Query("SELECT * FROM user")
    fun getUser(): Flow<LocalUser?>
}