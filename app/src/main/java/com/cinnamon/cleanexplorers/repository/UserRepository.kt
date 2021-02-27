package com.cinnamon.cleanexplorers.repository

import androidx.annotation.WorkerThread
import com.cinnamon.cleanexplorers.dao.UserDao
import com.cinnamon.cleanexplorers.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {


    val user: Flow<User?> = userDao.getUser()

    @WorkerThread
    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun deleteAll(){
        userDao.deleteAll()
    }
}