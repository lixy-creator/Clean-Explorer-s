package com.cinnamon.cleanexplorers.repository

import androidx.annotation.WorkerThread
import com.cinnamon.cleanexplorers.api.datasource.UserDataSource
import com.cinnamon.cleanexplorers.api.exception.TechnicalException
import com.cinnamon.cleanexplorers.api.exception.WrongLoginOrPassword
import com.cinnamon.cleanexplorers.api.model.*
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
        val requestLogin = RequestLogin(login, password)
        return when (val response = dataSource.getUser(requestLogin)) {
            is Success -> {
                Success(value = response.value.run { this })
            }
            is Failure -> {
                when ((response.reason as? HttpException)?.code()) {
                    404 -> Failure(WrongLoginOrPassword(response.reason))
                    else -> Failure(TechnicalException(response.reason))
                }
            }
        }
    }

    suspend fun createUser(newUser: NewUser): Result<Unit, Throwable> {
        return when (val response = dataSource.createUser(newUser)) {
            is Success -> {
                Success(value = response.value)
            }
            is Failure -> {
                when ((response.reason as? HttpException)?.code()) {
                    else -> Failure(TechnicalException(response.reason))
                }
            }
        }
    }

    suspend fun checkEmail(email: Email): Result<Unit, Throwable> {
        return when (val response = dataSource.checkEmail(email)) {
            is Success -> {
                Success(value = response.value)
            }
            is Failure -> {
                Failure(TechnicalException(response.reason))
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