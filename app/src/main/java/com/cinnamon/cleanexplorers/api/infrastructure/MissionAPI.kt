package com.cinnamon.cleanexplorers.api.infrastructure

import com.cinnamon.cleanexplorers.api.model.Mission
import retrofit2.http.GET

interface MissionAPI {

    @GET("categories_specifique.php")
    suspend fun getSpecialMission():List<Mission>

    @GET("categories_global.php")
    suspend fun getGlobalMission():List<Mission>
}