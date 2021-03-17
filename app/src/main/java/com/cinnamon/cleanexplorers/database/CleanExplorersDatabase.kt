package com.cinnamon.cleanexplorers.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cinnamon.cleanexplorers.database.dao.GarbageDao
import com.cinnamon.cleanexplorers.database.dao.UserDao
import com.cinnamon.cleanexplorers.database.entity.Garbage
import com.cinnamon.cleanexplorers.database.entity.LocalUser
import com.cinnamon.cleanexplorers.database.util.Converters

@Database(entities = [LocalUser::class, Garbage::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CleanExplorersDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun garbageDao():GarbageDao

    companion object {
        @Volatile
        private var INSTANCE: CleanExplorersDatabase? = null

        fun getDatabase(context: Context): CleanExplorersDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CleanExplorersDatabase::class.java,
                    "clean_explorers_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}