package com.cinnamon.cleanexplorers.view.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.*
import com.cinnamon.cleanexplorers.database.entity.LocalUser
import com.cinnamon.cleanexplorers.service.LocationLiveData
import com.cinnamon.cleanexplorers.repository.ProductRepository
import com.cinnamon.cleanexplorers.repository.UserRepository

class HomeViewModel(
   userRepository: UserRepository,
    productRepository: ProductRepository
) : ViewModel() {


    val localUser: LiveData<LocalUser?> = userRepository.localUser.asLiveData()
    val garbageNbr = productRepository.getAllGarbage().asLiveData()


    fun getLocationData(context: Context) = LocationLiveData(context)

    /**private fun getMissions() {
    viewModelScope.launch(Dispatchers.IO) {
    val response =
    missionRepository.getGlobalMission()
    withContext(Dispatchers.Main) {
    when (response) {
    is Success -> {
    val value =response.value
    Log.w("Success", "YES I'M DONE ${value.toString()}")
    }
    is Failure -> {
    Log.e("Error Mission", "Mission not Found")
    }
    }
    }
    }
    }**/


}

class HomeViewModelFactory(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(userRepository, productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}