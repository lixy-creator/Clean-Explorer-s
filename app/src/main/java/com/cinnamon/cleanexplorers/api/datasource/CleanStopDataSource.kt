package com.cinnamon.cleanexplorers.api.datasource

import com.cinnamon.cleanexplorers.api.infrastructure.CleanStopAPI
import com.cinnamon.cleanexplorers.api.infrastructure.UserAPI
import com.cinnamon.cleanexplorers.api.model.NewUser
import com.cinnamon.cleanexplorers.api.service.ApiClient
import com.cinnamon.cleanexplorers.api.service.safeApiCall

class CleanStopDataSource(client: ApiClient) {
    val api: CleanStopAPI = client.createService(CleanStopAPI::class.java)

    suspend fun getListCleanStop()= safeApiCall {
        api.getCleanStop()
    }
}