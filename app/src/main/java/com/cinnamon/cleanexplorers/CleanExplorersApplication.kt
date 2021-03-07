package com.cinnamon.cleanexplorers

import android.app.Application
import com.cinnamon.cleanexplorers.manager.CleanExplorersDatabase
import com.cinnamon.cleanexplorers.repository.MissionRepository
import com.cinnamon.cleanexplorers.repository.UserRepository

class CleanExplorersApplication : Application() {

    private val database by lazy { CleanExplorersDatabase.getDatabase(this) }

    val userRepository by lazy { UserRepository(database.userDao()) }
    val missionRepository by lazy { MissionRepository() }
}