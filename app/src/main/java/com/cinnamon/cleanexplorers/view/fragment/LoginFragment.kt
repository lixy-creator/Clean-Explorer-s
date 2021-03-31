package com.cinnamon.cleanexplorers.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cinnamon.cleanexplorers.CleanExplorersApplication
import com.cinnamon.cleanexplorers.database.entity.UserEntity
import com.cinnamon.cleanexplorers.databinding.FragmentLoginBinding
import com.cinnamon.cleanexplorers.utils.extension.isNowadays
import com.cinnamon.cleanexplorers.view.viewmodel.LoginViewModel
import com.cinnamon.cleanexplorers.view.viewmodel.LoginViewModelFactory
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn


class LoginFragment : Fragment() {


    companion object {
        const val TAG: String = "LoginFragment"
        private const val RC_SIGN_IN = 1
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var userEntity: UserEntity

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            (requireContext().applicationContext as CleanExplorersApplication).userRepository,
            this
        )
    }
    private lateinit var callbackManager: CallbackManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginFacebook()
        setViewModel()
        initListener()
    }

    private fun loginFacebook() {
        callbackManager = CallbackManager.Factory.create()
        val profile = Profile.getCurrentProfile()
        if (profile != null) {
            LoginManager.getInstance().logOut()
        }

        binding.facebookButton.setPermissions(listOf("email", "public_profile"))
        binding.facebookButton.fragment = this
        binding.facebookButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    viewModel.getUserProfileFacebook(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.e(TAG, "Cancel")
                    binding.loadingLayout.loading.isVisible = false
                }

                override fun onError(exception: FacebookException?) {
                    Log.e(TAG, "Error " + exception!!.message)
                    binding.loadingLayout.loading.isVisible = false
                }
            })
    }

    private fun setViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.loadingLayout.loading.isVisible = it
        }
        viewModel.intentGoogle.observe(viewLifecycleOwner) {
            it?.let {
                startActivityForResult(it, RC_SIGN_IN)
            }
        }
        viewModel.errorOnLogin.observe(viewLifecycleOwner) {
            binding.textInputLayoutLogin.error = it
            binding.textInputLayoutLogin.isErrorEnabled = it != null
        }
        viewModel.errorOnPassword.observe(viewLifecycleOwner) {
            binding.textInputLayoutPwd.error = it
            binding.textInputLayoutPwd.isErrorEnabled = it != null
        }
        viewModel.errorGlobal.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.userWantLogin.observe(viewLifecycleOwner) {
            if (it.first) {
                val user = it.second ?: return@observe
                viewModel.isExistsUser(user.userId).observe(viewLifecycleOwner) { isExists ->
                    if (isExists)
                        if (!user.lastConnexionDate.isNowadays()) {
                            viewModel.setNbrConnexion(user.userId)
                            viewModel.goToHome(user.userId, true)
                        } else
                            viewModel.goToHome(user.userId, false)
                    else viewModel.insertUser(user)
                }
            }
        }

    }

    private fun initListener() {
        binding.googleButton.setOnClickListener {
            binding.loadingLayout.loading.isVisible = true
            viewModel.onGoogleClicked(requireContext())
        }
        binding.facebookButton.setOnClickListener {
            binding.loadingLayout.loading.isVisible = true
        }

        binding.subscribeText.setOnClickListener {
            binding.loadingLayout.loading.isVisible = true
            viewModel.goToSubscribe()
        }

        binding.connexionButton.setOnClickListener {
            val login = binding.editTextLogin.text.toString()
            val password = binding.editTextPassword.text.toString()
            viewModel.validateField(requireContext(), login, password)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        Log.w(TAG, "RequestCode : $requestCode")
        if (requestCode == RC_SIGN_IN) {
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)
            viewModel.handleSignInResultGoogle(result)

            if (result.isSuccessful) {
                Toast.makeText(requireContext(), "Sign is success", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Sign is cancel", Toast.LENGTH_LONG).show()
                binding.loadingLayout.loading.isVisible = false
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}