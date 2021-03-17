package com.cinnamon.cleanexplorers.api.datasource

import com.cinnamon.cleanexplorers.api.infrastructure.ProductAPI
import com.cinnamon.cleanexplorers.api.service.ApiClient
import com.cinnamon.cleanexplorers.api.service.safeApiCall

class ProductDataSource(client: ApiClient) {
   private val api: ProductAPI = client.createService(ProductAPI::class.java)

    suspend fun getProduct(barcode: String) = safeApiCall { api.getProduct(barcode) }
}

