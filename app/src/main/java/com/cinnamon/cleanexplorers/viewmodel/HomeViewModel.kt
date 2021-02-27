package com.cinnamon.cleanexplorers.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.*
import com.cinnamon.cleanexplorers.manager.CleanExplorersLocationManager
import com.cinnamon.cleanexplorers.model.User
import com.cinnamon.cleanexplorers.repository.UserRepository

class HomeViewModel(private val repository: UserRepository) : ViewModel() {


    val user: LiveData<User?> = repository.user.asLiveData()
    val mLocation = MutableLiveData<Location>()

     fun getLastLocation(context: Context) {
        val locationResult = object : CleanExplorersLocationManager.Companion.LocationResult() {
            override fun gotLocation(location: Location?) {
                mLocation.postValue(location)
            }
        }
        CleanExplorersLocationManager().getLocation(context, locationResult)
    }
}

class HomeViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}