package com.cinnamon.cleanexplorers.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.cinnamon.cleanexplorers.repository.MissionRepository

class MissionViewModel ( private val missionRepository: MissionRepository): ViewModel() {

    fun getMissionByUser(userId: String) = missionRepository.getMissionByUser(userId).asLiveData()


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