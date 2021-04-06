package com.cinnamon.cleanexplorers.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cinnamon.cleanexplorers.database.entity.MissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(missions: List<MissionEntity>)

    @Query("SELECT * FROM mission WHERE userId=:userId")
    fun getMissionByUser(userId: String): Flow<List<MissionEntity>>

    @Query("UPDATE mission SET valid=1 WHERE userId=:userId AND missionId=:missionId")
    suspend fun validateMission(userId: String, missionId: Long)
}