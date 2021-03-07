package com.cinnamon.cleanexplorers.view.fragment

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cinnamon.cleanexplorers.CleanExplorersApplication
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.databinding.FragmentHomeBinding
import com.cinnamon.cleanexplorers.view.viewmodel.HomeViewModel
import com.cinnamon.cleanexplorers.view.viewmodel.HomeViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class HomeFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val REQUEST_LOCATION_PERMISSIONS = 10
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var map: GoogleMap

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            (requireContext().applicationContext as CleanExplorersApplication).userRepository,
            (requireContext().applicationContext as CleanExplorersApplication).missionRepository
        )
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
        checkLocationPermission()
        setView(savedInstanceState)
        setListener()
        setViewModel()
    }


    private fun setView(bundle: Bundle?) {
        binding.map.onCreate(bundle)
        binding.map.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    private fun setViewModel() {
        viewModel.localUser.observe(viewLifecycleOwner) {
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
            map.uiSettings.setAllGesturesEnabled(false)
        }

        binding.dashboardLayout.buttonClose.setOnClickListener {
            binding.dashboardLayout.layoutTool.isVisible = false
            binding.fabMenu.isVisible = true
            map.uiSettings.setAllGesturesEnabled(true)
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


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.getLastLocation(requireContext())
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            getAlertDialog()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.isBuildingsEnabled = true
        googleMap.setMinZoomPreference(19f)
        googleMap.setMaxZoomPreference(50f)

        viewModel.mLocation.observe(viewLifecycleOwner) {
            val myLatLng = LatLng(it.latitude, it.longitude)
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.getLastLocation(requireContext())
            } else {

                getAlertDialog()
            }
        }
    }

    private fun getAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.error_location_title)
            .setMessage(
                R.string.error_permission
            )
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { dialog: DialogInterface, _: Int ->
                this.requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSIONS
                )
                dialog.dismiss()
            }
            .create().show()
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

}