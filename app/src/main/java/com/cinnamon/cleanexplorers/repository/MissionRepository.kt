package com.cinnamon.cleanexplorers.repository

import com.cinnamon.cleanexplorers.api.datasource.MissionDataSource
import com.cinnamon.cleanexplorers.api.service.ApiClientImpl
import com.cinnamon.cleanexplorers.api.service.safeApiCall
import okhttp3.OkHttpClient

class MissionRepository {

    private val dataSource = MissionDataSource(ApiClientImpl(okHttpClient = OkHttpClient(),false))

    suspend fun getGlobalMission() = safeApiCall {
        dataSource.getGlobalMission()
    }

    suspend fun getSpecialMission() = safeApiCall {
        dataSource.getSpecialMission()
    }
}