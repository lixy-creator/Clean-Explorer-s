package com.cinnamon.cleanexplorers.api.datasource

import com.cinnamon.cleanexplorers.api.infrastructure.UserAPI
import com.cinnamon.cleanexplorers.api.model.Email
import com.cinnamon.cleanexplorers.api.model.NewUser
import com.cinnamon.cleanexplorers.api.model.RequestLogin
import com.cinnamon.cleanexplorers.api.model.User
import com.cinnamon.cleanexplorers.api.service.ApiClient
import com.cinnamon.cleanexplorers.api.service.safeApiCall

class UserDataSource(client: ApiClient) {

    val api: UserAPI = client.createService(UserAPI::class.java)

    suspend fun createUser(newUser: NewUser)= safeApiCall {
        api.createUser(newUser)
    }


    suspend fun getUser(requestLogin: RequestLogin)= safeApiCall {
        api.getUser(requestLogin)
    }

    suspend fun checkEmail(email:Email)= safeApiCall {
        api.checkEmail(email)
    }
}