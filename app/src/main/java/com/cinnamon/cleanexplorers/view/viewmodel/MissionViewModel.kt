package com.cinnamon.cleanexplorers.view.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.Mission
import com.cinnamon.cleanexplorers.api.model.Success
import com.cinnamon.cleanexplorers.repository.MissionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MissionViewModel ( private val missionRepository: MissionRepository): ViewModel() {

    val listMissionsGlobal=MutableLiveData<List<Mission>>()

    init {
        getMissionGlobal()
    }

    private fun getMissionGlobal(){
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                missionRepository.getGlobalMission()
                when (response) {
                    is Success -> {
                        listMissionsGlobal.postValue(response.value)
                    }
                    is Failure -> {
                        Log.e("Error Mission", "Mission not Found")
                    }
                }
            }

    }


}

class MissionViewModelFactory(
    private val missionRepository: MissionRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MissionViewModel(missionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}