package com.cinnamon.cleanexplorers.repository

import com.cinnamon.cleanexplorers.api.datasource.MissionDataSource
import com.cinnamon.cleanexplorers.api.exception.TechnicalException
import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.Mission
import com.cinnamon.cleanexplorers.api.model.Result
import com.cinnamon.cleanexplorers.api.model.Success
import com.cinnamon.cleanexplorers.api.service.ApiClientImpl
import okhttp3.OkHttpClient

class MissionRepository {

    private val dataSource = MissionDataSource(ApiClientImpl(okHttpClient = OkHttpClient(), false))

    suspend fun getGlobalMission(): Result<List<Mission>, Throwable> {
        return when (val response = dataSource.getGlobalMission()) {
            is Success -> {
                Success(value = response.value.run { this })
            }
            is Failure -> {
                Failure(TechnicalException(response.reason))
            }
        }
    }

    suspend fun getSpecialMission(): Result<List<Mission>, Throwable> {
        return when (val response = dataSource.getSpecialMission()) {
            is Success -> {
                Success(value = response.value.run { this })
            }
            is Failure -> {
                Failure(TechnicalException(response.reason))
            }
        }
    }
}