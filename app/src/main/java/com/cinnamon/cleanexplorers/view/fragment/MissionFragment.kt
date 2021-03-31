package com.cinnamon.cleanexplorers.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cinnamon.cleanexplorers.CleanExplorersApplication
import com.cinnamon.cleanexplorers.databinding.FragmentMissionBinding
import com.cinnamon.cleanexplorers.view.adapter.MissionAdapter
import com.cinnamon.cleanexplorers.view.viewmodel.MissionViewModel
import com.cinnamon.cleanexplorers.view.viewmodel.MissionViewModelFactory


class MissionFragment : Fragment() {

    private var _binding: FragmentMissionBinding? = null
    private val binding get() = _binding!!

    private val args: MissionFragmentArgs by navArgs()
    private lateinit var userId: String

    private val viewModel: MissionViewModel by viewModels {
        MissionViewModelFactory((requireContext().applicationContext as CleanExplorersApplication).missionRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
        setViewModel()
        setListener()
    }

    private fun setView() {
        userId = args.userId
        val layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.listTask.layoutManager = layoutManager
        binding.listTask.itemAnimator = DefaultItemAnimator()
    }

    private fun setViewModel() {
        viewModel.getMissionByUser(userId).observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                viewModel.getMissionByUser(userId)
            }
            binding.listTask.adapter = MissionAdapter(requireContext(), it)
        }
    }

    private fun setListener() {
       binding.taskCloseButton.setOnClickListener {
           val direction = MissionFragmentDirections.actionMissionFragmentToHomeFragment(userId)
            this.findNavController().navigate(direction)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}