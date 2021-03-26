package com.cinnamon.cleanexplorers.view.viewmodel

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.api.model.Email
import com.cinnamon.cleanexplorers.api.model.Failure
import com.cinnamon.cleanexplorers.api.model.NewUser
import com.cinnamon.cleanexplorers.api.model.Success
import com.cinnamon.cleanexplorers.database.entity.LocalUser
import com.cinnamon.cleanexplorers.repository.UserRepository
import com.cinnamon.cleanexplorers.utils.Constant
import com.cinnamon.cleanexplorers.utils.extension.getAge
import com.cinnamon.cleanexplorers.view.fragment.SubscribeFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class SubscribeViewModel(
    private val userRepository: UserRepository,
    private val fragment: Fragment
) : ViewModel() {


    private val navigationController by lazy { fragment.findNavController() }
    private val formatDateInput = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val formatDateOutput = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    val isLoading = MutableLiveData(false)
    val errorFistName = MutableLiveData<String?>()
    val errorEmail = MutableLiveData<String?>()
    val errorDateBirth = MutableLiveData<String?>()
    val errorPassword = MutableLiveData<String?>()
    val errorGlobal = MutableLiveData<String?>()

    fun createUser(context: Context, newUser: NewUser, passwordConf: String) {
        errorFistName.postValue(null)
        errorEmail.postValue(null)
        errorDateBirth.postValue(null)
        errorPassword.postValue(null)
        if (newUser.firstName.isBlank()) {
            errorFistName.postValue(context.getString(R.string.error_field_empty))
            return
        }
        if (newUser.email.isBlank()) {
            errorEmail.postValue(context.getString(R.string.error_field_empty))
            return
        }

        if (!newUser.email.isEmailValid()) {
            errorEmail.postValue(context.getString(R.string.error_email))
            return
        }

        if (newUser.dateBirth.isBlank()) {
            errorDateBirth.postValue(context.getString(R.string.error_field_empty))
            return
        }
        val age = formatDateOutput.parse(newUser.dateBirth)?.getAge() ?: 0
        if (age < 5 || age > 110) {
            errorDateBirth.postValue(context.getString(R.string.error_date_birth))
            return
        }

        if (newUser.password.isBlank()) {
            errorPassword.postValue(context.getString(R.string.error_field_empty))
            return
        }
        if (passwordConf.isBlank()) {
            errorPassword.postValue(context.getString(R.string.error_field_empty))
            return
        }
        if (newUser.password != passwordConf) {
            errorPassword.postValue(context.getString(R.string.error_password_confirmation))
            return
        }
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val emailResponse = userRepository.checkEmail(Email(newUser.email))
            if (emailResponse is Success) {
                errorEmail.postValue(context.getString(R.string.error_email_existed))
                isLoading.postValue(false)
                return@launch
            }
            when (userRepository.createUser(newUser)) {
                is Success -> {
                    isLoading.postValue(false)
                    val responseUser = userRepository.getUserApi(newUser.email, newUser.password)
                    if (responseUser is Success) {
                        val user = responseUser.value
                        userRepository.insert(
                            LocalUser(
                                id = user.id.toString(),
                                firstName = user.firstName,
                                email = user.email,
                                type = Constant.DATABASE,
                                dateBirth = formatDateInput.parse(user.dateBirth),
                                createDate = formatDateInput.parse(user.dateCreate),
                            )
                        )
                        goToHome()
                    } else {
                        errorGlobal.postValue(context.getString(R.string.error_unknown))
                    }
                }
                is Failure -> {
                    errorGlobal.postValue(context.getString(R.string.error_unknown))
                }
            }
        }
    }


    private fun String.isEmailValid(): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(this)
        return matcher.matches()
    }

    private fun goToHome() {
        val direction = SubscribeFragmentDirections.actionSubscribeFragmentToHomeFragment()
        navigationController.navigate(direction)
    }
}

class SubscribeViewModelFactory(
    private val userRepository: UserRepository,
    private val fragment: Fragment,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubscribeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubscribeViewModel(userRepository, fragment) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}