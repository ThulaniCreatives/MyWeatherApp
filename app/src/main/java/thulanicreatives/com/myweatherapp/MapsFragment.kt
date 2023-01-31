package thulanicreatives.com.myweatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import thulanicreatives.com.myweatherapp.databinding.FragmentMapsBinding
import thulanicreatives.com.myweatherapp.weather.ui.WeatherViewModel
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback, OnMapClickListener {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>


    private val viewModel: WeatherViewModel by viewModels()

    private lateinit var map: GoogleMap
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val locationState = MutableStateFlow<LatLng>(LatLng(0.0,0.0))

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
        launchPermission()

        val mapFragment =
            childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setUpObservers()

        val mBottomSheetBehaviour = BottomSheetBehavior.from(binding.bottomSheetParent.root)
        mBottomSheetBehaviour.isHideable = false
        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
        val tv = TypedValue()

        activity?.theme.let {
            if (activity?.theme!!.resolveAttribute(
                    androidx.appcompat.R.attr.actionBarSize, tv, true
                )
            ) {
                val actionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, resources.displayMetrics
                )
                mBottomSheetBehaviour.peekHeight = actionBarHeight
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                observeAccountDataState()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun observeAccountDataState() {

        viewModel.weatherDate.collectLatest {
            Timber.d("${it.currentWeatherData}")

            with(binding.bottomSheetParent) {
                txtDate.text = "${it.currentWeatherData?.time}"
                txtCurrentDay.text = "Sat, 3 Aug"
                txtTemp.text = "${it.currentWeatherData?.temperatureCelsius}"
                locationObservers()
                Glide.with(imgWeatherType.context).load(R.drawable.ic_rainy).into(imgWeatherType)
            }

        }
    }

    private fun locationObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationIsUpdated()
            }
        }
    }

    private suspend fun locationIsUpdated() {
        locationState.collectLatest {
            binding.bottomSheetParent.txtLocation.text = getAddress(it.latitude, it.longitude)
        }
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireActivity().applicationContext, Locale.getDefault())
        val address: Address?
        var addressText = ""

        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                address = addresses[0]

                addressText = if(address.locality.isNullOrEmpty()) {
                    "City Not Found"
                } else {
                    address.locality
                }
            } else {
                addressText = "City Not Found"
            }
        }
        return addressText
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
                        locationState.value = LatLng(latitude, longitude)
                        val city = getAddress(location.latitude, location.longitude)
                        val currentLocation = LatLng(latitude, longitude)
                        map.addMarker(MarkerOptions().position(currentLocation).title(city))
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapClick(location: LatLng) {
        map.clear()
        val location = LatLng(location.latitude, location.longitude)
        val city = getAddress(location.latitude, location.longitude)
        map.addMarker(MarkerOptions().position(location).title(city))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
        map.minZoomLevel
        locationState.value = LatLng(location.latitude, location.longitude)
        //send new LatLong
        viewModel.getWeatherInfo(location.latitude, location.longitude)
    }

}