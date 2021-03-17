package com.cinnamon.cleanexplorers.api.infrastructure

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductAPI {

    @GET("product/{barcode}")
    suspend fun getProduct(@Path("barcode") barcode:String): ResponseBody
}