package com.cinnamon.cleanexplorers.api.datasource

import com.cinnamon.cleanexplorers.api.infrastructure.MissionAPI
import com.cinnamon.cleanexplorers.api.model.Mission
import com.cinnamon.cleanexplorers.api.service.ApiClient
import com.cinnamon.cleanexplorers.api.service.safeApiCall
import com.cinnamon.cleanexplorers.api.model.Result


class MissionDataSource(client: ApiClient)  {
   private val api: MissionAPI = client.createService(MissionAPI::class.java)

     suspend fun getGlobalMission() = safeApiCall {
        api.getGlobalMission()
    }

     suspend fun getSpecialMission() = safeApiCall {
        api.getSpecialMission()
    }

}