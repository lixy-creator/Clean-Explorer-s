package com.cinnamon.cleanexplorers.api.datasource

import com.cinnamon.cleanexplorers.api.infrastructure.MissionAPI
import com.cinnamon.cleanexplorers.api.model.Mission
import com.cinnamon.cleanexplorers.api.service.ApiClient
import com.cinnamon.cleanexplorers.api.service.safeApiCall
import com.cinnamon.cleanexplorers.api.model.Result


interface MissionDataSource {
    suspend fun getGlobalMission(): Result<List<Mission>, Throwable>
    suspend fun getSpecialMission(): Result<List<Mission>, Throwable>
}

class MissionDataSourceImpl(client: ApiClient) : MissionDataSource {
    val api: MissionAPI = client.createService(MissionAPI::class.java)

    override suspend fun getGlobalMission() = safeApiCall {
        api.getGlobalMission()
    }

    override suspend fun getSpecialMission() = safeApiCall {
        api.getSpecialMission()
    }

}