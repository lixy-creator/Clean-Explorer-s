package com.cinnamon.cleanexplorers.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cinnamon.cleanexplorers.databinding.FragmentSubscribeBinding
import com.google.android.material.datepicker.MaterialDatePicker


class SubscribeFragment : Fragment() {
    private var _binding: FragmentSubscribeBinding? = null
    private val binding get() = _binding!!

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
        setListener()
    }

    private fun setView() {
        binding.editTextDate.keyListener = null
    }

    private fun setListener() {
        binding.connectText.setOnClickListener {
            val direction = SubscribeFragmentDirections.actionSubscribeFragmentToLoginFragment()
            findNavController().navigate(direction)
        }

        binding.textInputLayoutDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selectionner une date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                binding.editTextDate.setText(datePicker.headerText)
            }
            datePicker.show(requireParentFragment().parentFragmentManager, "Subscribe")

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}