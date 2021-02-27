package com.cinnamon.cleanexplorers

import android.app.Application
import com.cinnamon.cleanexplorers.manager.CleanExplorersDatabase
import com.cinnamon.cleanexplorers.repository.UserRepository

class CleanExplorersApplication : Application() {

    val database by lazy { CleanExplorersDatabase.getDatabase(this) }
    val repository by lazy { UserRepository(database.userDao()) }
}