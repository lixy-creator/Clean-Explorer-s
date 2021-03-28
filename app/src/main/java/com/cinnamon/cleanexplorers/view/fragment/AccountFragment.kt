package com.cinnamon.cleanexplorers.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cinnamon.cleanexplorers.CleanExplorersApplication
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.databinding.FragmentAccountBinding
import com.cinnamon.cleanexplorers.utils.extension.getAge
import com.cinnamon.cleanexplorers.view.viewmodel.AccountViewModel
import com.cinnamon.cleanexplorers.view.viewmodel.AccountViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by viewModels {
        AccountViewModelFactory(
            (requireContext().applicationContext as CleanExplorersApplication).userRepository,
            (requireContext().applicationContext as CleanExplorersApplication).productRepository,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        setListener()
    }

    private fun setViewModel() {
        viewModel.user.observe(viewLifecycleOwner) {
            it?.let { user ->
                binding.firstNameText.text = user.firstName
                val age = user.dateBirth?.getAge() ?: 0
                binding.ageText.text = getString(R.string.info_age, age)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.dateCreateText.text =
                    getString(R.string.info_date_create, format.format(user.createDate))
                binding.numberPointText.text = user.point.toString()
            }
        }

        viewModel.garbageNbr.observe(viewLifecycleOwner){
            binding.numberScanText.text=it?.toString()?:"0"
        }
    }

    private fun setListener() {
        binding.accountCloseButton.setOnClickListener {
            val direction = AccountFragmentDirections.actionAccountFragmentToHomeFragment()
            this.findNavController().navigate(direction)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}