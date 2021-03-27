package com.cinnamon.cleanexplorers.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.cinnamon.cleanexplorers.databinding.FragmentDetailCleanStopBinding


class DetailCleanStopFragment : Fragment() {

    private var _binding: FragmentDetailCleanStopBinding? = null
    private val binding get() = _binding!!
    val args: DetailCleanStopFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailCleanStopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
    }

    private fun setView() {
        val cleanStop = args.cleanStop
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}