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
import androidx.navigation.fragment.findNavController
import com.cinnamon.cleanexplorers.CleanExplorersApplication
import com.cinnamon.cleanexplorers.databinding.FragmentLoginBinding
import com.cinnamon.cleanexplorers.model.LocalUser
import com.cinnamon.cleanexplorers.utils.Constant
import com.cinnamon.cleanexplorers.view.viewmodel.UserViewModel
import com.cinnamon.cleanexplorers.view.viewmodel.UserViewModelFactory
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.json.JSONException


class LoginFragment : Fragment() {


    companion object {
        const val TAG: String = "LoginFragment"
        private const val RC_SIGN_IN = 1
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((requireContext().applicationContext as CleanExplorersApplication).userRepository)
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
                    getUserProfileFacebook(loginResult.accessToken)
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

    private fun initListener() {
        binding.googleButton.setOnClickListener {
            binding.loadingLayout.loading.isVisible = true
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        binding.facebookButton.setOnClickListener {
            binding.loadingLayout.loading.isVisible = true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
        Log.w(TAG, "RequestCode : $requestCode")
        if (requestCode == RC_SIGN_IN) {
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(result)

            if (result.isSuccessful) {
                Toast.makeText(requireContext(), "Sign is success", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Sign is cancel", Toast.LENGTH_LONG).show()
                binding.loadingLayout.loading.isVisible = false
            }

        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            val user =
                LocalUser(
                    account.id!!,
                    account.givenName!!,
                    account.email!!,
                    account.idToken,
                    Constant.GOOGLE
                )
            userViewModel.insert(user)
            goToHome()
        } catch (e: ApiException) {
            Log.e(TAG, "signInResult:failed code= ${e.statusCode}")
        }
    }


    private fun getUserProfileFacebook(currentAccessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            currentAccessToken
        ) { obj, _ ->
            Log.w(TAG, obj.toString())
            try {
                val pseudo: String = obj.getString("first_name")
                val email: String = obj.getString("email")
                val id: String = obj.getString("id")
                val user = LocalUser(id, pseudo, email, currentAccessToken.token, Constant.FACEBOOK)
                userViewModel.insert(user)
                goToHome()

            } catch (e: JSONException) {
                Log.e(TAG, "Error JSON Parse : ${e.message}")
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "first_name,email,id")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun goToHome() {
        val direction = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        findNavController().navigate(direction)
    }

}