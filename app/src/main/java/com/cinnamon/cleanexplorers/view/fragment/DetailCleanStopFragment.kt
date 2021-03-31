package com.cinnamon.cleanexplorers.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.cinnamon.cleanexplorers.api.model.CleanStop
import com.cinnamon.cleanexplorers.databinding.FragmentDetailCleanStopBinding


class DetailCleanStopFragment : Fragment() {

    private var _binding: FragmentDetailCleanStopBinding? = null
    private val binding get() = _binding!!

    private val args: DetailCleanStopFragmentArgs by navArgs()
    private lateinit var cleanStop: CleanStop
    private lateinit var userId: String

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
        setListener()
    }

    private fun setView() {
        cleanStop = args.cleanStop
        userId = args.userId
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Glide
            .with(requireContext())
            .load(cleanStop.image)
            .centerCrop()
            .into(binding.imageCleanStop)

        binding.titleCleanStop.text = cleanStop.name
        binding.descriptionCleanStop.text =
            cleanStop.description ?: "Description indisponible"

    }

    private fun setListener() {
        binding.cleanStopCloseButton.setOnClickListener {
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            val destination =
                DetailCleanStopFragmentDirections.actionDetailCleanStopFragmentToHomeFragment(userId)
            findNavController().navigate(destination)
        }

        binding.goToButton.setOnClickListener {
            if (cleanStop.url.isEmpty()) {
                Toast.makeText(requireContext(), "Ce site web est introuvable", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            val browserIntent = Intent(Intent.CATEGORY_BROWSABLE, Uri.parse(cleanStop.url))
            browserIntent.action = Intent.ACTION_VIEW
            startActivity(browserIntent)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}