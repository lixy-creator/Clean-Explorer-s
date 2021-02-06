package com.cinnamon.cleanexplorers.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cinnamon.cleanexplorers.activity.MainActivity
import com.cinnamon.cleanexplorers.databinding.FragmentLoginBinding
import com.cinnamon.cleanexplorers.manager.CleanFragmentManager
import com.cinnamon.cleanexplorers.model.User
import com.facebook.AccessToken
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import org.json.JSONException


@Suppress("JAVA_CLASS_ON_COMPANION")
class LoginFragment : Fragment() {


    companion object {
        val TAG: String = javaClass.simpleName
        private const val RC_SIGN_IN = 1
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedpreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        sharedpreferences =
            requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)




        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageView()
        initListener()
    }

    private fun manageView() {
        binding.facebookButton.setReadPermissions(listOf("email", "public_profile"))

        binding.facebookButton.registerCallback(
            (requireContext() as MainActivity).callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                    getUserProfile(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.e(TAG, "Cancel")
                }

                override fun onError(exception: FacebookException?) {
                    // App code
                    Log.e(TAG, "Error " + exception!!.message)
                }
            })
    }

    private fun initListener() {
        binding.googleButton.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(activity as MainActivity, gso)
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.w(TAG, "RequestCode : $requestCode")
        if (requestCode == RC_SIGN_IN) {
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(result)

            if (result.isSuccessful) {
                Toast.makeText(activity, "Sign is success", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity, "Sign is cancel", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            val user = User(account!!.id, account.givenName, account.email, account.idToken)
            addSharePreference(user)
            goToHome()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    fun signInFacebook() {

    }

    private fun getUserProfile(currentAccessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            currentAccessToken
        ) { obj, _ ->
            Log.d("TAG", obj.toString())
            try {
                val pseudo: String = obj.getString("first_name")
                val email: String = obj.getString("email")
                val id: String = obj.getString("id")
                val user = User(id, pseudo, email, currentAccessToken.token)
                addSharePreference(user)
                goToHome()

            } catch (e: JSONException) {
                Log.e(TAG, "Error JSON Parse : " + e.message)
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

    private fun addSharePreference(user: User) {
        val prefsEditor: SharedPreferences.Editor = sharedpreferences.edit()
        val gson = Gson()
        val json: String = gson.toJson(user)
        prefsEditor.putString("user", json)
        prefsEditor.apply()
    }
}