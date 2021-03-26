package com.cinnamon.cleanexplorers.view.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.api.exception.WrongLoginOrPassword
import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.Success
import com.cinnamon.cleanexplorers.database.entity.LocalUser
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

    private fun insert(localUser: LocalUser) = viewModelScope.launch {
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
            isLoading.postValue(false)
            when (response) {
                is Success -> {
                    val user = response.value
                    insert(
                        LocalUser(
                            id = user.id.toString(),
                            firstName = user.firstName,
                            email = user.email,
                            type = Constant.DATABASE,
                            dateBirth = format.parse(user.dateBirth),
                            createDate = format.parse(user.dateCreate),
                        )
                    )
                    goToHome()
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
                LocalUser(
                    account.id!!,
                    account.givenName!!,
                    account.email!!,
                    account.idToken,
                    Constant.GOOGLE,
                )
            insert(user)
            goToHome()
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
                val user = LocalUser(
                    id,
                    pseudo,
                    email,
                    currentAccessToken.token,
                    Constant.FACEBOOK
                )
                insert(user)
                goToHome()

            } catch (e: JSONException) {
                Log.e(LoginFragment.TAG, "Error JSON Parse : ${e.message}")
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "first_name,email,id")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun goToHome() {
        val direction = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        navigationController.navigate(direction)
    }

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