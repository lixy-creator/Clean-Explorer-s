package com.cinnamon.cleanexplorers.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cinnamon.cleanexplorers.database.entity.Garbage
import kotlinx.coroutines.flow.Flow

@Dao
interface GarbageDao {
    @Insert
    suspend fun insert(garbage: Garbage)

    @Query("SELECT SUM(quantity) FROM garbage")
     fun getAllGarbage(): Flow<Int>

}