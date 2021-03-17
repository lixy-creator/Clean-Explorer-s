package com.cinnamon.cleanexplorers.api.service

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit


class ApiClientImpl(var okHttpClient: OkHttpClient, isOpenApi:Boolean) : ApiClient {

    private val baseUrl =if(isOpenApi)
        OFF_API_BASE_ULR
    else
        MY_API_BASE_URL

    override fun <S> createService(serviceClass: Class<S>): S {
        val contentType: MediaType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(okHttpClient).build()
            .create(serviceClass)

    }

    companion object {
        private const val MY_API_BASE_URL= "https://cleanexplorers.jeprojet.com/script/"
        private const val OFF_API_BASE_ULR="https://world.openfoodfacts.org/api/v0/"
    }
}