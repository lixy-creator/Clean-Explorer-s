package blooom.cinnamon.cleanexplorers.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import blooom.cinnamon.cleanexplorers.R
import blooom.cinnamon.cleanexplorers.activity.MainActivity
import blooom.cinnamon.cleanexplorers.manager.CleanExplorersLocationManager
import blooom.cinnamon.cleanexplorers.model.User
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class HomeFragment : Fragment(), OnMapReadyCallback {


    @BindView(R.id.map)
    lateinit var mapView: MapView
    lateinit var map: GoogleMap

    lateinit var mActivity: MainActivity
    private val REQUEST_CODE_ASK_PERMISSIONS = 123
    private var lastLocation: Location? = null
    lateinit var fusedLocationClient:FusedLocationProviderClient

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        ButterKnife.bind(this, view)

        mActivity = activity as MainActivity
        getLastLocation()
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        val user: User? = arguments!!.getParcelable("user")


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity)
        return view
    }



    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
     val locationResult= object : CleanExplorersLocationManager.Companion.LocationResult(){
         override fun gotLocation(location: Location?) {
             lastLocation=location
         }

     }

        CleanExplorersLocationManager().getLocation(mActivity, locationResult)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        googleMap.isBuildingsEnabled=true
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
                            mActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        getLastLocation()
                        Toast.makeText(mActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume();
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        getLastLocation()
    }
}