package com.cinnamon.cleanexplorers.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.cinnamon.cleanexplorers.repository.ProductRepository
import com.cinnamon.cleanexplorers.repository.UserRepository

class AccountViewModel(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) :
    ViewModel() {

    fun getUserLocal(userId: String) = userRepository.getUserLocal(userId).asLiveData()
    fun getGarbageNbr(userId: String) = productRepository.getAllGarbage(userId).asLiveData()
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