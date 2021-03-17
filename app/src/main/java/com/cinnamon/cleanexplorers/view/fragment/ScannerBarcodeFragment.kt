package com.cinnamon.cleanexplorers.view.fragment

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cinnamon.cleanexplorers.CleanExplorersApplication
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.databinding.FragmentScannerBarcodeBinding
import com.cinnamon.cleanexplorers.manager.BarcodeAnalyzer
import com.cinnamon.cleanexplorers.view.viewmodel.ScanBarcodeViewModel
import com.cinnamon.cleanexplorers.view.viewmodel.ScanBarcodeViewModelFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

typealias BarcodeListener = (barcode: String) -> Unit

class ScannerBarcodeFragment : Fragment() {


    private var _binding: FragmentScannerBarcodeBinding? = null
    private val binding get() = _binding!!

    private var processingBarcode = AtomicBoolean(false)

    private lateinit var cameraExecutor: ExecutorService
    private val viewModel: ScanBarcodeViewModel by viewModels {
        ScanBarcodeViewModelFactory(
            (requireContext().applicationContext as CleanExplorersApplication).productRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBarcodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissionCamera()
        setViewModel()
        setListener()
    }

    private fun setViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.searchProgress.isVisible = it
        }

        viewModel.product.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.detailProductLayout.detailProduct.visibility = View.VISIBLE
                Glide
                    .with(requireContext())
                    .load(it.image)
                    .centerCrop()
                    .into(binding.detailProductLayout.imageProduct)
                binding.detailProductLayout.nameInfo.text = it.name
                binding.detailProductLayout.packagingInfo.text =
                    it.packaging ?: getString(R.string.not_defined)
            } else {
                binding.detailProductLayout.detailProduct.visibility = View.GONE
            }
        }
    }

    private fun setListener() {
        binding.detailProductLayout.buttonDownQuantity.setOnClickListener {
            val quantity = binding.detailProductLayout.quantityInfo.text.toString().toInt()
            if (quantity > 1)
                binding.detailProductLayout.quantityInfo.text = "${quantity - 1}"
        }

        binding.detailProductLayout.buttonUpQuantity.setOnClickListener {
            val quantity = binding.detailProductLayout.quantityInfo.text.toString().toInt()
            if (quantity < 99)
                binding.detailProductLayout.quantityInfo.text = "${quantity + 1}"

        }

        binding.detailProductLayout.validateButton.setOnClickListener {
            val name = binding.detailProductLayout.nameInfo.text.toString()
            val packaging = binding.detailProductLayout.packagingInfo.text.toString()
            val quantity = binding.detailProductLayout.quantityInfo.text.toString().toInt()
            viewModel.insertGarbage(name, packaging, quantity)
            val direction = ScannerBarcodeFragmentDirections.actionScannerFragmentToHomeFragment()
            this.findNavController().navigate(direction)
        }
    }

    private fun checkPermissionCamera() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun startCamera() {
        // Create an instance of the ProcessCameraProvider,
        // which will be used to bind the use cases to a lifecycle owner.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        // Add a listener to the cameraProviderFuture.
        // The first argument is a Runnable, which will be where the magic actually happens.
        // The second argument (way down below) is an Executor that runs on the main thread.
        cameraProviderFuture.addListener({
            // Add a ProcessCameraProvider, which binds the lifecycle of your camera to
            // the LifecycleOwner within the application's life.
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Initialize the Preview object, get a surface provider from your PreviewView,
            // and set it on the preview instance.
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(
                    binding.viewFinder.surfaceProvider
                )
            }
            // Setup the ImageAnalyzer for the ImageAnalysis use case
            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                        if (processingBarcode.compareAndSet(false, true))
                            viewModel.loadBarcode(barcode)
                    })
                }

            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Unbind any bound use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to lifecycleOwner
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                Log.e("PreviewUseCase", "Binding failed! :(", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        processingBarcode.set(false)
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 11
    }
}