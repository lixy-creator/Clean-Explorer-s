package com.cinnamon.cleanexplorers.api.datasource

import com.cinnamon.cleanexplorers.api.infrastructure.UserAPI
import com.cinnamon.cleanexplorers.api.model.User
import com.cinnamon.cleanexplorers.api.service.ApiClient
import com.cinnamon.cleanexplorers.api.service.safeApiCall

class UserDataSource(client: ApiClient) {

    val api: UserAPI = client.createService(UserAPI::class.java)

    suspend fun createUser(user: User)= safeApiCall {
        api.createUser(user)
    }


    suspend fun getUser(login:String, password:String)= safeApiCall {
        api.getUser(login, password)
    }
}