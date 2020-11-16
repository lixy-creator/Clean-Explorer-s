package blooom.cinnamon.cleanexplorers.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import blooom.cinnamon.cleanexplorers.R
import blooom.cinnamon.cleanexplorers.activity.MainActivity
import blooom.cinnamon.cleanexplorers.manager.CleanFragmentManager
import blooom.cinnamon.cleanexplorers.model.User
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.json.JSONException


@Suppress("JAVA_CLASS_ON_COMPANION")
class LoginFragment : Fragment() {


    companion object {
        val TAG: String = javaClass.simpleName
        private const val RC_SIGN_IN = 1
    }

    @BindView(R.id.sign_facebook)
    lateinit var btnFacebook: LoginButton

    // private lateinit var callbackManager: CallbackManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_login, container, false)
        ButterKnife.bind(this, view)

        val activity = activity as MainActivity
        btnFacebook.setReadPermissions(listOf("email", "public_profile"))

        btnFacebook.registerCallback(
            activity.callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                    val profile: Profile? = Profile.getCurrentProfile()


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


        return view
    }

    @OnClick(R.id.sign_google)
    fun signInGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(activity as MainActivity, gso)
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
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
            val user = User(account!!.id, account.givenName, account.familyName, account.email)
            CleanFragmentManager.displayHome(activity as MainActivity, user)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    @OnClick(R.id.sign_facebook)
    fun signInFacebook() {

    }

    private fun getUserProfile(currentAccessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            currentAccessToken
        ) { obj, _ ->
            Log.d("TAG", obj.toString())
            try {
                val first_name: String = obj.getString("first_name")
                val last_name : String= obj.getString("last_name")
                val email: String = obj.getString("email")
                val id: String = obj.getString("id")
                val user = User(id, first_name, last_name, email)
                CleanFragmentManager.displayHome(activity as MainActivity, user)

            } catch (e: JSONException) {
                Log.e(TAG, "Error JSON Parse : " + e.message)
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email,id")
        request.parameters = parameters
        request.executeAsync()
    }
}