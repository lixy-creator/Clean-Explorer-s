package com.cinnamon.cleanexplorers.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cinnamon.cleanexplorers.CleanExplorersApplication
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.api.model.CleanStop
import com.cinnamon.cleanexplorers.databinding.FragmentHomeBinding
import com.cinnamon.cleanexplorers.model.Location
import com.cinnamon.cleanexplorers.service.manager.GpsManager
import com.cinnamon.cleanexplorers.utils.Constant.GPS_REQUEST
import com.cinnamon.cleanexplorers.view.adapter.MapInfoWindowAdapter
import com.cinnamon.cleanexplorers.view.viewmodel.HomeViewModel
import com.cinnamon.cleanexplorers.view.viewmodel.HomeViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*


class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveCanceledListener,
    GoogleMap.OnCameraIdleListener,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener {

    companion object {
        const val LOCATION_REQUEST = 100
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var map: GoogleMap
    var markerLocation: Marker? = null
    var isFirstLocation = true
    private var myLocation: Location? = null

    var isGPSEnabled = false

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            (requireContext().applicationContext as CleanExplorersApplication).userRepository,
            (requireContext().applicationContext as CleanExplorersApplication).productRepository,
            (requireActivity().applicationContext as CleanExplorersApplication).cleanStopRepository
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
        initGps()
        setView(savedInstanceState)
        setListener()
        setViewModel()
    }


    private fun setView(bundle: Bundle?) {
        binding.map.onCreate(bundle)
        binding.map.getMapAsync(this)
        binding.centerButton.isVisible = false

    }

    private fun setViewModel() {
        viewModel.localUser.observe(viewLifecycleOwner) {
            it?.let { user ->
                binding.dashboardLayout.homeTitle.text =
                    getString(R.string.home_title, user.firstName)
            }
        }

        viewModel.garbageNbr.observe(viewLifecycleOwner) {
            binding.dashboardLayout.textRecap.text =
                resources.getQuantityString(R.plurals.countdown_result, it ?: 0, it ?: 0)
        }

    }

    private fun addCleanStop() {
        viewModel.listCleanStop.observe(viewLifecycleOwner) {
            it?.forEach { cleanStop ->
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(cleanStop.latitude, cleanStop.longitude))
                        .title(cleanStop.name)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_cleanstop))
                )
                marker.tag = cleanStop
            }
        }
    }

    private fun setListener() {
        binding.fabMenu.setOnClickListener {
            binding.dashboardLayout.layoutTool.isVisible = true
            binding.fabMenu.isVisible = false
            binding.scannerButton.isVisible = false
            map.uiSettings.setAllGesturesEnabled(false)
        }

        binding.dashboardLayout.buttonClose.setOnClickListener {
            binding.dashboardLayout.layoutTool.isVisible = false
            binding.fabMenu.isVisible = true
            binding.scannerButton.isVisible = true
            map.uiSettings.setAllGesturesEnabled(true)
        }

        binding.centerButton.setOnClickListener {
            myLocation?.let { location ->
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            location.latitude,
                            location.longitude
                        ), 20.0f
                    )
                )
                binding.centerButton.isVisible = false
            }
        }

        binding.dashboardLayout.accountButton.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToAccountFragment()
            this.findNavController().navigate(direction)
        }
        binding.dashboardLayout.settingsButton.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToSettingFragment()
            this.findNavController().navigate(direction)
        }
        binding.dashboardLayout.taskButton.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToMissionFragment()
            this.findNavController().navigate(direction)
        }

        binding.scannerButton.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToScannerFragment()
            this.findNavController().navigate(direction)
        }
    }


    private fun initGps() {
        GpsManager(requireContext()).turnGPSOn(object : GpsManager.OnGpsListener {

            override fun gpsStatus(isGPSEnable: Boolean) {
                isGPSEnabled = isGPSEnable
            }
        })
    }


    private fun invokeLocationAction() {
        when {
            !isGPSEnabled -> Unit

            isPermissionsGranted() -> startLocationUpdate()

            shouldShowRequestPermissionRationale() -> getAlertDialog()

            else -> ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST
            )
        }
    }

    private fun startLocationUpdate() {
        viewModel.getLocationData(requireContext()).observe(viewLifecycleOwner) {
            markerLocation?.remove()
            myLocation = it
            val latLng = LatLng(it.latitude, it.longitude)
            val markerOptions = MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_person))
            markerLocation = map.addMarker(markerOptions)
            if (isFirstLocation) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20.0f))
                isFirstLocation = false
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.isBuildingsEnabled = true
        googleMap.setMinZoomPreference(19f)
        googleMap.setMaxZoomPreference(1000f)
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                context, R.raw.map
            )
        )
        googleMap.setOnCameraIdleListener(this)
        googleMap.setOnCameraMoveStartedListener(this)
        googleMap.setOnCameraMoveListener(this)
        googleMap.setOnCameraMoveCanceledListener(this)
        googleMap.uiSettings.isCompassEnabled = false

        var latLng = LatLng(48.8716954, 2.3185572)
        myLocation?.let {
            latLng = LatLng(it.latitude, it.longitude)
        }

        val cameraPosition = CameraPosition.Builder()
            .target(latLng) // Sets the center of the map to
            .zoom(15f) // Sets the zoom
            .bearing(90f) // Sets the orientation of the camera to east
            .tilt(90f) // Sets the tilt of the camera to 30 degrees
            .build()
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        addCleanStop()
        map.setInfoWindowAdapter(MapInfoWindowAdapter(requireContext()))
        map.setOnMarkerClickListener(this)
        map.setOnInfoWindowClickListener(this)
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
                    LOCATION_REQUEST
                )
                dialog.dismiss()
            }
            .create().show()
    }

    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        } else
            getAlertDialog()
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
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

    override fun onCameraMoveStarted(reason: Int) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            binding.centerButton.isVisible = true
        }
    }

    override fun onCameraMove() {}

    override fun onCameraMoveCanceled() {}

    override fun onCameraIdle() {}

    override fun onMarkerClick(marker: Marker?): Boolean {
        if (marker?.tag !is CleanStop) return true
        return false
    }

    override fun onInfoWindowClick(marker: Marker?) {
        val cleanStop = marker?.tag as? CleanStop ?: return
        viewModel.updateNbrCleanStopSeen()
        val destination =
            HomeFragmentDirections.actionHomeFragmentToDetailCleanStopFragment(cleanStop)
        findNavController().navigate(destination)
    }

}