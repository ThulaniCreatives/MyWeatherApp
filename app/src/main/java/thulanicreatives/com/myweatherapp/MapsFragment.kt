package thulanicreatives.com.myweatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import thulanicreatives.com.myweatherapp.databinding.FragmentMapsBinding
import timber.log.Timber
import java.util.*

class MapsFragment : Fragment(), OnMapReadyCallback, OnMapClickListener {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var map: GoogleMap
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = false

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val callback = OnMapReadyCallback { googleMap ->
        Timber.d("longithththhhtt location $longitude")
        val sydney = LatLng(latitude, longitude)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        googleMap.minZoomLevel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
        launchPermission()

        val mapFragment =
            childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        //getDeviceLocation(

//        val tv = TypedValue()
//        if (context?.theme?.resolveAttribute(androidx.transition.R.attr.actionBarSize, tv, true) == true) {
//            val actionBarHeight = TypedValue.complexToDimensionPixelSize(
//                tv.data,
//                resources.displayMetrics
//            )
//            bottomSheetBehavior.peekHeight = 500
//        }
    }

    private fun launchPermission() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            getDeviceLocation()
        }
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val location = task.result
                        Timber.d("last  known location $location")
                        latitude = location.latitude
                        longitude = location.longitude

                        val currentLocation = LatLng(latitude, longitude)
                        map.addMarker(MarkerOptions().position(currentLocation).title("You are here!"))
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14f))
                        map.minZoomLevel

                    } else {
                        Timber.e("Exception: %s", task.exception)
                    }
                } else {
                    Timber.d("Perm No Granted")
                }
            }

        } catch (e: SecurityException) {
            Timber.d("Exception: %s", e.message)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener(this)

    }

    override fun onMapClick(location: LatLng) {
        map.clear()
        val sydney = LatLng(location.latitude, location.longitude)
        map.addMarker(MarkerOptions().position(sydney).title("You are here!"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))
        map.minZoomLevel
    }

}