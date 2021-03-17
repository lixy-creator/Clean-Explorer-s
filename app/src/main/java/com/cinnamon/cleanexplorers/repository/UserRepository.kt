package com.cinnamon.cleanexplorers.repository

import androidx.annotation.WorkerThread
import com.cinnamon.cleanexplorers.database.dao.UserDao
import com.cinnamon.cleanexplorers.database.entity.LocalUser
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {


    val localUser: Flow<LocalUser?> = userDao.getUser()

    @WorkerThread
    suspend fun insert(localUser: LocalUser) {
        userDao.insert(localUser)
    }

    suspend fun deleteAll(){
        userDao.deleteAll()
    }
}