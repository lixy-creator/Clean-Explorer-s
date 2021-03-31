package com.cinnamon.cleanexplorers.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cinnamon.cleanexplorers.CleanExplorersApplication
import com.cinnamon.cleanexplorers.databinding.FragmentRoutingBinding
import com.cinnamon.cleanexplorers.view.viewmodel.UserViewModel
import com.cinnamon.cleanexplorers.view.viewmodel.UserViewModelFactory


class RoutingFragment : Fragment() {

    private var _binding: FragmentRoutingBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((requireContext().applicationContext as CleanExplorersApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        redirectionManager()
    }

    private fun redirectionManager() {
        userViewModel.userConnected.observe(viewLifecycleOwner) { user ->
            if (user != null && user.connected) {
                val direction =
                    RoutingFragmentDirections.actionRoutingFragmentToHomeFragment(user.userId)
                findNavController().navigate(direction)
            } else {
                goToLogin()
            }
        }
    }

    private fun goToLogin() {
        val direction = RoutingFragmentDirections.actionRoutingFragmentToLoginFragment()
        findNavController().navigate(direction)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}