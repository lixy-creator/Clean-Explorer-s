package com.cinnamon.cleanexplorers.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cinnamon.cleanexplorers.CleanExplorersApplication
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.databinding.FragmentHomeBinding
import com.cinnamon.cleanexplorers.manager.CleanExplorersLocationManager
import com.cinnamon.cleanexplorers.model.User
import com.cinnamon.cleanexplorers.viewmodel.UserViewModel
import com.cinnamon.cleanexplorers.viewmodel.UserViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var map: GoogleMap
    var isOpen = false
    private val REQUEST_CODE_ASK_PERMISSIONS = 123
    private var lastLocation: Location? = null
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((requireContext().applicationContext as CleanExplorersApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView(savedInstanceState)
        setListener()
        setViewModel()
    }


    private fun setView(bundle: Bundle?) {
        getLastLocation()
        binding.map.onCreate(bundle)
        binding.map.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    private fun setViewModel() {
        userViewModel.user.observe(viewLifecycleOwner) {
            it?.let { user ->
                binding.dashboardLayout.homeTitle.text =
                    getString(R.string.home_title, user.firstName)
            }
        }
    }

    private fun setListener() {
        binding.fabMenu.setOnClickListener {
            binding.dashboardLayout.layoutTool.isVisible = true
            binding.fabMenu.isVisible = false

        }
        binding.dashboardLayout.buttonClose.setOnClickListener {
            binding.dashboardLayout.layoutTool.isVisible = false
            binding.fabMenu.isVisible = true
        }
        binding.dashboardLayout.accountButton.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToAccountFragment()
            this.findNavController().navigate(direction)
        }
        binding.dashboardLayout.settingsButton.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToSettingFragment()
            this.findNavController().navigate(direction)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val locationResult = object : CleanExplorersLocationManager.Companion.LocationResult() {
            override fun gotLocation(location: Location?) {
                lastLocation = location
            }

        }

        CleanExplorersLocationManager().getLocation(requireContext(), locationResult)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        googleMap.isBuildingsEnabled = true
        googleMap.setMinZoomPreference(19f)
        googleMap.setMaxZoomPreference(50f)

        if (lastLocation != null) {
            val myLatLng = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
            map.addMarker(
                MarkerOptions()
                    .position(myLatLng)
                    .title("My Position")
            )
            val cameraPosition = CameraPosition.Builder()
                .target(myLatLng) // Sets the center of the map to
                .zoom(25f) // Sets the zoom
                .bearing(50f) // Sets the orientation of the camera to east
                .tilt(30f) // Sets the tilt of the camera to 30 degrees
                .build()
            // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 20.0f))
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        getLastLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        getLastLocation()
                        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDestroy()
        _binding = null

    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.map.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        getLastLocation()
    }
}