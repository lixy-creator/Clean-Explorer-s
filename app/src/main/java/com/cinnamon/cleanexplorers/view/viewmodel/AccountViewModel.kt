package com.cinnamon.cleanexplorers.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.cinnamon.cleanexplorers.database.entity.LocalUser
import com.cinnamon.cleanexplorers.repository.ProductRepository
import com.cinnamon.cleanexplorers.repository.UserRepository

class AccountViewModel(userRepository: UserRepository, productRepository: ProductRepository) :
    ViewModel() {

    val user: LiveData<LocalUser?> = userRepository.localUser.asLiveData()
    val garbageNbr = productRepository.getAllGarbage().asLiveData()
}

class AccountViewModelFactory(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(userRepository, productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}