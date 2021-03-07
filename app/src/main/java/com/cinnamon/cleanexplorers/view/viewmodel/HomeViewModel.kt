package com.cinnamon.cleanexplorers.view.viewmodel

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.cinnamon.cleanexplorers.api.datasource.MissionDataSource
import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.Success
import com.cinnamon.cleanexplorers.manager.CleanExplorersLocationManager
import com.cinnamon.cleanexplorers.model.LocalUser
import com.cinnamon.cleanexplorers.repository.MissionRepository
import com.cinnamon.cleanexplorers.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val userRepository: UserRepository,
    private val missionRepository: MissionRepository
) : ViewModel() {


    val localUser: LiveData<LocalUser?> = userRepository.localUser.asLiveData()
    val mLocation = MutableLiveData<Location>()

    init {
        getMissions()
    }

    fun getLastLocation(context: Context) {
        val locationResult = object : CleanExplorersLocationManager.Companion.LocationResult() {
            override fun gotLocation(location: Location?) {
                mLocation.postValue(location)
            }
        }
        CleanExplorersLocationManager().getLocation(context, locationResult)
    }

    private fun getMissions() {
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                missionRepository.getGlobalMission()
            withContext(Dispatchers.Main) {
                when (response) {
                    is Success -> {
                        val value =response.value
                        Log.e("Success", "YES I'M DONE ${value.toString()}")
                    }
                    is Failure -> {
                        Log.e("Error Mission", "Mission not Found")
                    }
                }
            }
        }
    }


}

class HomeViewModelFactory(
    private val userRepository: UserRepository,
    private val missionRepository: MissionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(userRepository, missionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}