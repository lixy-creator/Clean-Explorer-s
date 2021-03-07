package com.cinnamon.cleanexplorers.manager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cinnamon.cleanexplorers.dao.UserDao
import com.cinnamon.cleanexplorers.model.LocalUser

@Database(entities = [LocalUser::class], version = 1, exportSchema = false)
abstract class CleanExplorersDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

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