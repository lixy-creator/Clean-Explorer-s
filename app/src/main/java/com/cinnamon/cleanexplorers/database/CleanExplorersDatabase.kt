package com.cinnamon.cleanexplorers.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cinnamon.cleanexplorers.database.dao.GarbageDao
import com.cinnamon.cleanexplorers.database.dao.MissionDao
import com.cinnamon.cleanexplorers.database.dao.UserDao
import com.cinnamon.cleanexplorers.database.entity.Garbage
import com.cinnamon.cleanexplorers.database.entity.MissionEntity
import com.cinnamon.cleanexplorers.database.entity.UserEntity
import com.cinnamon.cleanexplorers.database.util.Converters

@Database(
    entities = [UserEntity::class, Garbage::class, MissionEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CleanExplorersDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun garbageDao(): GarbageDao
    abstract fun missionDao(): MissionDao

    companion object {
        @Volatile
        private var INSTANCE: CleanExplorersDatabase? = null

        fun getDatabase(context: Context): CleanExplorersDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CleanExplorersDatabase::class.java,
                    "clean_explorers_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}