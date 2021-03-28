package com.cinnamon.cleanexplorers.view.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.cinnamon.cleanexplorers.api.model.CleanStop
import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.Success
import com.cinnamon.cleanexplorers.database.entity.LocalUser
import com.cinnamon.cleanexplorers.repository.CleanStopRepository
import com.cinnamon.cleanexplorers.repository.ProductRepository
import com.cinnamon.cleanexplorers.repository.UserRepository
import com.cinnamon.cleanexplorers.service.LocationLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val userRepository: UserRepository,
    productRepository: ProductRepository,
    private val cleanStopRepository: CleanStopRepository,
) : ViewModel() {


    val localUser: LiveData<LocalUser?> = userRepository.localUser.asLiveData()
    val garbageNbr = productRepository.getAllGarbage().asLiveData()
    val listCleanStop = MutableLiveData<List<CleanStop>?>(null)

    init {
        getCleanStops()
    }

    fun getLocationData(context: Context) = LocationLiveData(context)

    private fun getCleanStops() {
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                cleanStopRepository.getListCleanStop()
            withContext(Dispatchers.Main) {
                when (response) {
                    is Success -> {
                        listCleanStop.postValue(response.value)
                    }
                    is Failure -> {
                        Log.e("Error Mission", "Mission not Found")
                    }
                }
            }
        }
    }

    fun updateNbrCleanStopSeen() {
        viewModelScope.launch {
            userRepository.updateNbrCleanStop()
        }
    }


}

class HomeViewModelFactory(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val cleanStopRepository: CleanStopRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(userRepository, productRepository, cleanStopRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}