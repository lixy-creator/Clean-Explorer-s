package com.cinnamon.cleanexplorers.view.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.cinnamon.cleanexplorers.api.model.CleanStop
import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.Success
import com.cinnamon.cleanexplorers.database.entity.MissionEntity
import com.cinnamon.cleanexplorers.database.util.MapperMission
import com.cinnamon.cleanexplorers.repository.CleanStopRepository
import com.cinnamon.cleanexplorers.repository.MissionRepository
import com.cinnamon.cleanexplorers.repository.ProductRepository
import com.cinnamon.cleanexplorers.repository.UserRepository
import com.cinnamon.cleanexplorers.service.LocationLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val cleanStopRepository: CleanStopRepository,
    private val missionRepository: MissionRepository,
    private val userId: String,
) : ViewModel() {

    val listCleanStop = MutableLiveData<List<CleanStop>?>(null)
    fun getGarbageNbr(): LiveData<Int> =
        productRepository.getAllGarbage(userId).asLiveData()

    fun getNbrCleanStopSeen() = userRepository.getNbrCleanStop(userId).asLiveData()

    fun setNbrConnexion() =
        viewModelScope.launch {
            userRepository.setNbrConnexion(userId)
        }


    init {
        getCleanStops()

    }

    val listMissionCleanStop: LiveData<Pair<Int, List<MissionEntity>>> =
        object : MediatorLiveData<Pair<Int, List<MissionEntity>>>() {
            var number: Int? = null
            var missions: List<MissionEntity>? = null

            init {
                addSource(getNbrCleanStopSeen()) { cleanStopSeen ->
                    this.number = cleanStopSeen
                    missions?.let { value = cleanStopSeen to it }
                }
                addSource(getMissionsLocal()) { listMissions ->
                    this.missions = listMissions
                    number?.let { value = it to listMissions }
                }
                missions = missions?.filter { it.name.contains("Clean-stop") }
            }
        }

    val listMissionGarbage: LiveData<Pair<Int?, List<MissionEntity>>> =
        object : MediatorLiveData<Pair<Int?, List<MissionEntity>>>() {
            var number: Int? = null
            var missions: List<MissionEntity>? = null

            init {
                addSource(getGarbageNbr()) { garbageNbr ->
                    this.number = garbageNbr
                    missions?.let { value = garbageNbr to it }
                }
                addSource(getMissionsLocal()) { listMissions ->
                    this.missions = listMissions
                    number?.let { value = it to listMissions }
                }
                missions = missions?.filter { it.name.contains("Clean-stop") }
            }
        }

    fun getNbrConnexion() = userRepository.getNbrConnexion(userId)

    fun getUserLocal() = userRepository.getUserLocal(userId).asLiveData()

    fun getLocationData(context: Context) = LocationLiveData(context)

    fun addPointUser(point: Int) {
        viewModelScope.launch {
            userRepository.addPointUser(userId, point)
        }
    }

    fun getMissionsLocal() = missionRepository.getMissionByUser(userId).asLiveData()

    fun validateMission(missionId: Long) {
        viewModelScope.launch {
            missionRepository.validateMission(userId, missionId)
        }
    }

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

    fun setNbrCleanStopSeen() {
        viewModelScope.launch {
            userRepository.setNbrCleanStop(userId)
        }
    }

    fun getMissionGlobal() {
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                missionRepository.getGlobalMission()
            when (response) {
                is Success -> {
                    val missions = response.value
                    missionRepository.insertMission(MapperMission().mapperList(missions, userId))
                }
                is Failure -> {
                    Log.e("Error Mission", "Mission not Found")
                }
            }
        }
    }

}

class HomeViewModelFactory(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val cleanStopRepository: CleanStopRepository,
    private val missionRepository: MissionRepository,
    private val userId: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                userRepository,
                productRepository,
                cleanStopRepository,
                missionRepository,
                userId,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}