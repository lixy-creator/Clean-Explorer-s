package com.cinnamon.cleanexplorers.view.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.database.entity.LocalUser
import com.cinnamon.cleanexplorers.repository.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    val errorOnLogin = MutableLiveData<String?>()
    val errorOnPassword = MutableLiveData<String?>()
    val intentGoogle = MutableLiveData<Intent?>()
    val isLoading = MutableLiveData<Boolean>(false)

    fun insert(localUser: LocalUser) = viewModelScope.launch {
        userRepository.insert(localUser)
    }

    fun validateField(context: Context, login: String, password: String) {
        errorOnLogin.postValue(null)
        errorOnPassword.postValue(null)
        if (login.isBlank()) {
            errorOnLogin.postValue(context.getString(R.string.error_field_empty))
            return
        }

        if (password.isBlank()) {
            errorOnPassword.postValue(context.getString(R.string.error_field_empty))
            return
        }
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.getUserApi(
                login = login,
                password = password
            )
        }
    }

    fun onGoogleClicked(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
        intentGoogle.value = mGoogleSignInClient.signInIntent
    }
}

class LoginViewModelFactory(private val userRepository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}