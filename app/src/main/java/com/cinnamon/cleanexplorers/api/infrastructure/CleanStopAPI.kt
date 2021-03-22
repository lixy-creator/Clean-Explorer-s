package com.cinnamon.cleanexplorers.api.infrastructure

import com.cinnamon.cleanexplorers.api.model.CleanStop
import retrofit2.http.GET

interface CleanStopAPI {

    @GET("cleanstop.php")
    suspend fun getCleanStop(): List<CleanStop>
}