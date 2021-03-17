package com.cinnamon.cleanexplorers.view.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.Success
import com.cinnamon.cleanexplorers.database.entity.Garbage
import com.cinnamon.cleanexplorers.model.Product
import com.cinnamon.cleanexplorers.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class ScanBarcodeViewModel(private val productRepository: ProductRepository) : ViewModel() {

    val barcode = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)
    val product = MutableLiveData<Product?>(null)

    fun loadBarcode(barcodeResult: String) {
        isLoading.postValue(true)
        barcode.postValue(barcodeResult)
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = productRepository.getProduct(barcodeResult)) {
                is Success -> {
                    Log.w("This Product", response.value)
                    val json = JSONObject(response.value)
                    val status = json.getString("status_verbose") ?: return@launch
                    if (status == "product found") {
                        val pdt = json.getJSONObject("product")
                        product.postValue(
                            Product(
                                pdt.getString("product_name"),
                                pdt.getString("packaging"),
                                pdt.getString("image_thumb_url")
                            )
                        )
                    }
                    isLoading.postValue(false)
                }
                is Failure -> {
                    Log.e("This Product", "Failed")

                }
            }
        }
    }

    fun insertGarbage(name: String, packaging: String, quantity: Int) = viewModelScope.launch {
        productRepository.insertGarbage(
            Garbage(
                name = name,
                packaging = packaging,
                quantity = quantity,
                createDate = Date(),
            )
        )
    }
}


class ScanBarcodeViewModelFactory(
    private val productRepository: ProductRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanBarcodeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScanBarcodeViewModel(productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}