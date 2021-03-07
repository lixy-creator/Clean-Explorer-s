package com.cinnamon.cleanexplorers.repository

import com.cinnamon.cleanexplorers.api.datasource.MissionDataSourceImpl
import com.cinnamon.cleanexplorers.api.service.ApiClientImpl
import com.cinnamon.cleanexplorers.api.service.safeApiCall
import okhttp3.OkHttpClient

class MissionRepository {

    private val dataSource = MissionDataSourceImpl(ApiClientImpl(okHttpClient = OkHttpClient()))

    suspend fun getGlobalMission() = safeApiCall {
        dataSource.getGlobalMission()
    }

    suspend fun getSpecialMission() = safeApiCall {
        dataSource.getSpecialMission()
    }
}