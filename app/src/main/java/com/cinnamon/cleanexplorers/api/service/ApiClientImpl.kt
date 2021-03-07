package com.cinnamon.cleanexplorers.api.service

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit


class ApiClientImpl(var okHttpClient: OkHttpClient) : ApiClient {

    override fun <S> createService(serviceClass: Class<S>): S {
        val contentType: MediaType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(okHttpClient).build()
            .create(serviceClass)

    }

    companion object {
        private const val BASE_URL = "https://cleanexplorers.jeprojet.com/script/"
    }
}