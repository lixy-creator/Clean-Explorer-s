package com.cinnamon.cleanexplorers.repository

import com.cinnamon.cleanexplorers.api.datasource.ProductDataSource
import com.cinnamon.cleanexplorers.api.exception.TechnicalException
import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.Result
import com.cinnamon.cleanexplorers.api.model.Success
import com.cinnamon.cleanexplorers.api.service.ApiClientImpl
import com.cinnamon.cleanexplorers.database.dao.GarbageDao
import com.cinnamon.cleanexplorers.database.entity.Garbage
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient

class ProductRepository(private val garbageDao: GarbageDao) {
    private val dataSource = ProductDataSource(ApiClientImpl(okHttpClient = OkHttpClient(), true))

    suspend fun getProduct(barcode: String): Result<String, Throwable> {
        return when (val response = dataSource.getProduct(barcode)) {
            is Success -> {
                Success(value = response.value.run { this.string() })
            }
            is Failure -> {
                Failure(TechnicalException(response.reason))
            }
        }
    }

    suspend fun insertGarbage(garbage: Garbage) {
        garbageDao.insert(garbage)
    }

     fun getAllGarbage(): Flow<Int> {
        return garbageDao.getAllGarbage()
    }
}