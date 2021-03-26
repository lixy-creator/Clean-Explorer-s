package com.cinnamon.cleanexplorers.view.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cinnamon.cleanexplorers.CleanExplorersApplication
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.api.model.NewUser
import com.cinnamon.cleanexplorers.databinding.FragmentSubscribeBinding
import com.cinnamon.cleanexplorers.view.viewmodel.SubscribeViewModel
import com.cinnamon.cleanexplorers.view.viewmodel.SubscribeViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class SubscribeFragment : Fragment() {
    private var _binding: FragmentSubscribeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubscribeViewModel by viewModels {
        SubscribeViewModelFactory(
            (requireContext().applicationContext as CleanExplorersApplication).userRepository,
            this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscribeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
        setViewModel()
        setListener()
    }

    private fun setView() {
        binding.editTextDate.keyListener = null
    }

    private fun setViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.loadingLayout.loading.isVisible = it
        }
        viewModel.errorFistName.observe(viewLifecycleOwner) {
            binding.textInputLayoutFirstName.error = it
            binding.textInputLayoutFirstName.isErrorEnabled = it != null
        }
        viewModel.errorEmail.observe(viewLifecycleOwner) {
            binding.textInputLayoutEmail.error = it
            binding.textInputLayoutEmail.isErrorEnabled = it != null
        }
        viewModel.errorDateBirth.observe(viewLifecycleOwner) {
            binding.textInputLayoutDate.error = it
            binding.textInputLayoutDate.isErrorEnabled = it != null
        }
        viewModel.errorPassword.observe(viewLifecycleOwner) {
            binding.textInputLayoutPassword.error = it
            binding.textInputLayoutPassword.isErrorEnabled = it != null

            binding.textInputLayoutPassConf.error = it
            binding.textInputLayoutPassConf.isErrorEnabled = it != null
        }
        viewModel.errorGlobal.observe(viewLifecycleOwner) {
            it?.let {
                AlertDialog.Builder(requireContext())
                    .setMessage(it)
                    .setPositiveButton(R.string.ok) { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                    }
                    .create().show()
            }
        }
    }

    private fun setListener() {
        binding.connectText.setOnClickListener {
            val direction = SubscribeFragmentDirections.actionSubscribeFragmentToLoginFragment()
            findNavController().navigate(direction)
        }

        binding.editTextDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selectionner une date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.editTextDate.setText(format.format(Date(it)))

            }
            datePicker.show(requireParentFragment().parentFragmentManager, "Subscribe")

        }
        binding.subscribeButton.setOnClickListener {
            viewModel.createUser(requireContext(), getInput().first, getInput().second)
        }
    }

    private fun getInput(): Pair<NewUser, String> {
        val firstName = binding.editTextFirstName.text.toString()
        val email = binding.editTextEmail.text.toString()
        val datBirth = binding.editTextDate.text.toString().replace("/","-")
        val password = binding.editTextPassword.text.toString()
        val passwordConf = binding.editTextPassConf.text.toString()

        return Pair(
            NewUser(
                firstName = firstName,
                email = email,
                dateBirth = datBirth,
                password = password
            ), passwordConf
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}