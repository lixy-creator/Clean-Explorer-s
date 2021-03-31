package com.cinnamon.cleanexplorers.view.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.api.exception.WrongLoginOrPassword
import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.Success
import com.cinnamon.cleanexplorers.database.entity.UserEntity
import com.cinnamon.cleanexplorers.repository.UserRepository
import com.cinnamon.cleanexplorers.utils.Constant
import com.cinnamon.cleanexplorers.view.fragment.LoginFragment
import com.cinnamon.cleanexplorers.view.fragment.LoginFragmentDirections
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import java.text.SimpleDateFormat

class LoginViewModel(private val userRepository: UserRepository, private val fragment: Fragment) :
    ViewModel() {

    private val navigationController by lazy { fragment.findNavController() }

    val errorOnLogin = MutableLiveData<String?>()
    val errorOnPassword = MutableLiveData<String?>()
    val errorGlobal = MutableLiveData<String?>()

    val intentGoogle = MutableLiveData<Intent?>()
    val isLoading = MutableLiveData(false)
    private val format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val userWantLogin = MutableLiveData<Pair<Boolean, UserEntity?>>()

    fun isExistsUser(userId: String) = userRepository.isExistsUser(userId).asLiveData()

    private fun checkUser(userEntity: UserEntity) {
        userWantLogin.postValue(Pair(true, userEntity))
    }

    fun setNbrConnexion(id: String) {
        viewModelScope.launch {
            userRepository.setNbrConnexion(id)
        }
    }

    fun insertUser(localUser: UserEntity) {
        viewModelScope.launch {
            userRepository.insert(localUser)
        }
        goToHome(localUser.userId, true)
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
            isLoading.postValue(false)
            when (response) {
                is Success -> {
                    val user = response.value
                    checkUser(
                        UserEntity(
                            userId = user.id.toString(),
                            firstName = user.firstName,
                            email = user.email,
                            type = Constant.DATABASE,
                            birthday = format.parse(user.dateBirth),
                            createDate = format.parse(user.dateCreate),
                            connected = true,
                        )
                    )
                }
                is Failure -> {
                    if (response.reason is WrongLoginOrPassword) {
                        errorGlobal.postValue(context.getString(R.string.error_wrong_login))
                    }
                }
            }
        }
    }

    fun onGoogleClicked(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
        intentGoogle.value = mGoogleSignInClient.signInIntent
    }

    fun handleSignInResultGoogle(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            val user =
                UserEntity(
                    userId = account.id!!,
                    firstName = account.givenName!!,
                    email = account.email!!,
                    token = account.idToken,
                    type = Constant.GOOGLE,
                    connected = true,
                )
            checkUser(user)
        } catch (e: ApiException) {
            Log.e(LoginFragment.TAG, "signInResult:failed code= ${e.statusCode}")
        }
    }

    fun getUserProfileFacebook(currentAccessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            currentAccessToken
        ) { obj, _ ->
            Log.w(LoginFragment.TAG, obj.toString())
            try {
                val pseudo: String = obj.getString("first_name")
                val email: String = obj.getString("email")
                val id: String = obj.getString("id")
                val user = UserEntity(
                    userId = id,
                    firstName = pseudo,
                    email = email,
                    token = currentAccessToken.token,
                    type = Constant.FACEBOOK,
                    connected = true,
                )
                checkUser(user)

            } catch (e: JSONException) {
                Log.e(LoginFragment.TAG, "Error JSON Parse : ${e.message}")
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "first_name,email,id")
        request.parameters = parameters
        request.executeAsync()
    }

    fun goToHome(userId: String, newConnect: Boolean = false) {
        connectUser(userId)
        val direction =
            LoginFragmentDirections.actionLoginFragmentToHomeFragment(userId, newConnect)
        navigationController.navigate(direction)
    }

    private fun connectUser(userId: String) =
        viewModelScope.launch { userRepository.setConnectUser(userId, true) }

    fun goToSubscribe() {
        val direction = LoginFragmentDirections.actionLoginFragmentToSubscribeFragment()
        navigationController.navigate(direction)
    }
}

class LoginViewModelFactory(
    private val userRepository: UserRepository,
    private val fragment: Fragment
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository, fragment) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}