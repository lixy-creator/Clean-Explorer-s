package com.cinnamon.cleanexplorers.view.fragment

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.CATEGORY_BROWSABLE
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.cinnamon.cleanexplorers.CleanExplorersApplication
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.utils.Constant
import com.cinnamon.cleanexplorers.view.viewmodel.UserViewModel
import com.cinnamon.cleanexplorers.view.viewmodel.UserViewModelFactory
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory((requireContext().applicationContext as CleanExplorersApplication).userRepository)
    }

    private val args: SettingsFragmentArgs by navArgs()
    private lateinit var userId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = args.userId
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val preferenceLogout: Preference? =
            findPreference(getString(R.string.pref_logout_key))
        preferenceLogout?.setOnPreferenceClickListener {
            onDisconnectClicked()
            true
        }

        val socialPreference: Preference? =
            findPreference(getString(R.string.pref_social_key))
        socialPreference?.setOnPreferenceClickListener {
            goToWebSite("https://www.instagram.com/cleanexplorers/")
            true
        }
        val sitePreference: Preference? =
            findPreference(getString(R.string.pref_site_key))

        sitePreference?.setOnPreferenceClickListener {
            goToWebSite("https://cleanexplorers.jeprojet.com/")
            true
        }
    }

    private fun onDisconnectClicked() {
        viewModel.getUserLocal(userId).observe(viewLifecycleOwner) {
            val user = it ?: return@observe
            when (user.type) {
                Constant.FACEBOOK -> signOutFacebook()
                Constant.GOOGLE -> signOutGoogle()
                Constant.DATABASE -> Unit
            }
        }
        viewModel.disconnectUser(userId)
        val direction = SettingsFragmentDirections.actionGlobalRoutingFragment()
        findNavController().navigate(direction)
    }

    private fun goToWebSite(url: String) {
        val browserIntent = Intent(CATEGORY_BROWSABLE, Uri.parse(url))
        browserIntent.action = ACTION_VIEW
        startActivity(browserIntent)
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