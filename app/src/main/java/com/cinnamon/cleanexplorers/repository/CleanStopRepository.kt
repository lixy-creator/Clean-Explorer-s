package com.cinnamon.cleanexplorers.repository

import com.cinnamon.cleanexplorers.api.datasource.CleanStopDataSource
import com.cinnamon.cleanexplorers.api.exception.TechnicalException
import com.cinnamon.cleanexplorers.api.model.CleanStop
import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.Result
import com.cinnamon.cleanexplorers.api.model.Success
import com.cinnamon.cleanexplorers.api.service.ApiClientImpl
import okhttp3.OkHttpClient

class CleanStopRepository {
    private val dataSource =
        CleanStopDataSource(ApiClientImpl(okHttpClient = OkHttpClient(), false))

    suspend fun getListCleanStop(): Result<List<CleanStop>, Throwable> {
        return when (val response = dataSource.getListCleanStop()) {
            is Success -> {
                Success(value = response.value.run { this })
            }
            is Failure -> {
                Failure(TechnicalException(response.reason))
            }
        }
    }

}