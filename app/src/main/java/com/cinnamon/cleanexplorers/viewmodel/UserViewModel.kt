package com.cinnamon.cleanexplorers.viewmodel;

import androidx.lifecycle.*
import com.cinnamon.cleanexplorers.model.User
import com.cinnamon.cleanexplorers.repository.UserRepository;
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {


    val user: LiveData<User?> = repository.user.asLiveData()

    fun insert(user: User) = viewModelScope.launch {
        repository.insert(user)
    }

    fun delete() = viewModelScope.launch {
        repository.deleteAll()
    }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}