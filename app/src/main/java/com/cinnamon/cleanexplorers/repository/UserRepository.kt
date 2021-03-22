package com.cinnamon.cleanexplorers.repository

import androidx.annotation.WorkerThread
import com.cinnamon.cleanexplorers.api.datasource.UserDataSource
import com.cinnamon.cleanexplorers.api.exception.TechnicalException
import com.cinnamon.cleanexplorers.api.exception.WrongLoginOrPassword
import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.Result
import com.cinnamon.cleanexplorers.api.model.Success
import com.cinnamon.cleanexplorers.api.model.User
import com.cinnamon.cleanexplorers.api.service.ApiClientImpl
import com.cinnamon.cleanexplorers.database.dao.UserDao
import com.cinnamon.cleanexplorers.database.entity.LocalUser
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import retrofit2.HttpException

class UserRepository(private val userDao: UserDao) {

    private val dataSource = UserDataSource(ApiClientImpl(okHttpClient = OkHttpClient(), false))

    val localUser: Flow<LocalUser?> = userDao.getUser()

    suspend fun getUserApi(login: String, password: String): Result<User, Throwable> {
        return when (val response = dataSource.getUser(login, password)) {
            is Success -> {
                Success(value = response.value.run { this })
            }
            is Failure -> {
                Failure(TechnicalException(response.reason))
            }
        }
    }

    suspend fun createUser(login: String, password: String): Result<User, Throwable> {
        return when (val response = dataSource.getUser(login, password)) {
            is Success -> {
                Success(value = response.value.run { this })
            }
            is Failure -> {
                when ((response.reason as? HttpException)?.code()) {
                    401->Failure(WrongLoginOrPassword(response.reason))
                    else-> Failure(TechnicalException(response.reason))
                }
            }
        }
    }
    @WorkerThread
    suspend fun insert(localUser: LocalUser) {
        userDao.insert(localUser)
    }

    suspend fun deleteAll() {
        userDao.deleteAll()
    }
}