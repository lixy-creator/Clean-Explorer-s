package com.cinnamon.cleanexplorers.api.infrastructure

import com.cinnamon.cleanexplorers.api.model.Email
import com.cinnamon.cleanexplorers.api.model.NewUser
import com.cinnamon.cleanexplorers.api.model.RequestLogin
import com.cinnamon.cleanexplorers.api.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("create_user.php")
    suspend fun createUser(@Body newUser: NewUser)

    @POST("user.php")
    suspend fun getUser(@Body requestLogin: RequestLogin): User

    @POST("check_email.php")
    suspend fun checkEmail(@Body email: Email)
}