package com.cinnamon.cleanexplorers.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.cinnamon.cleanexplorers.CleanExplorersApplication
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.activity.MainActivity
import com.cinnamon.cleanexplorers.utils.Constant
import com.cinnamon.cleanexplorers.viewmodel.UserViewModel
import com.cinnamon.cleanexplorers.viewmodel.UserViewModelFactory
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


class SettingsFragment : PreferenceFragmentCompat() {

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((requireContext().applicationContext as CleanExplorersApplication).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val volumePreference: ListPreference? =
            findPreference(getString(R.string.pref_volume_key))

        val preferenceLogout: Preference? =
            findPreference(getString(R.string.pref_logout_key))
        preferenceLogout?.setOnPreferenceClickListener {
            onDisconnectClicked()
            true
        }
    }

    private fun onDisconnectClicked() {
        userViewModel.user.observe(viewLifecycleOwner) {
            val user = it ?: return@observe
            when (user.type) {
                Constant.FACEBOOK -> signOutFacebook()
                Constant.GOOGLE ->
                    signOutGoogle()
                Constant.DATABASE -> Unit
            }
        }
        userViewModel.delete()
        val direction = SettingsFragmentDirections.actionGlobalRoutingFragment()
        findNavController().navigate(direction)
    }

    private fun signOutGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        mGoogleSignInClient.signOut()

    }

    private fun signOutFacebook() {
        LoginManager.getInstance().logOut()
    }

}