package thulanicreatives.com.myweatherapp.weather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.sdb.retail.mobile.transaction.history.ForecastWeatherAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import thulanicreatives.com.myweatherapp.R
import thulanicreatives.com.myweatherapp.databinding.FragmentMapsBinding
import thulanicreatives.com.myweatherapp.weather.domain.utils.WeatherStateFlow
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherData
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback, OnMapClickListener {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var map: GoogleMap

    private var manageState: WeatherStateFlow? = null

    private val locationState = MutableStateFlow<LatLng>(LatLng(latitude, longitude))
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private var latitude: Double = 0.0
        private var longitude: Double = 0.0
        const val MAP_ZOOM_LEVEL = 14f
        const val CITY_NOT_FOUND = "City Not Found"
        const val firstElement = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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

        setUpObservers()

        val mBottomSheetBehaviour = BottomSheetBehavior.from(binding.bottomSheetParent.root)
        mBottomSheetBehaviour.isHideable = false
        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED
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

    private fun setUpObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                observeGetAccountDataResult()
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                observeAccountDataState()
            }
        }
    }

    private suspend fun observeAccountDataState() {

        viewModel.weatherDate.collectLatest { data ->
           val todayData =  data.weatherDataPerDay.values.toList().first()
            with(binding.bottomSheetParent) {
                binding.bottomSheetParent
                txtDate.text = getString(R.string.today)
                txtCurrentDay.text = ""
                txtTemp.text = "${todayData[firstElement].temperatureCelsius}"
                locationObservers()
                Glide.with(imgWeatherType.context).load(todayData[firstElement].weatherType.iconRes).into(imgWeatherType)
            }

            showForecast(data.weatherDataPerDay)

        }
    }

    private fun convertDate(date: LocalDateTime): String {
        val localDateTime = LocalDateTime.parse(date.toString())
        val formatter = DateTimeFormatter.ofPattern("E")
        return formatter.format(localDateTime)
    }

    private fun showForecast(forecastData: Map<Int, List<WeatherData>>) {

        var filteredList: MutableList<WeatherData> = mutableListOf()

        for (list in forecastData.values) {
                filteredList.addAll(list)
        }
        with(binding.bottomSheetParent) {
            if (recyclerViewForecast.adapter == null) {
                recyclerViewForecast.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    adapter = ForecastWeatherAdapter(filteredList)
                }
            } else {
                val transactionHistoryAdapter = recyclerViewForecast.adapter as ForecastWeatherAdapter
                transactionHistoryAdapter.onDataUpdate(filteredList)
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

    private suspend fun observeGetAccountDataResult() {
        viewModel.stateFlow.collectLatest { result ->
            when (result) {
                is WeatherStateFlow.Error -> showError()
                is WeatherStateFlow.Loading -> showLoading()

                else -> {}
            }
        }
    }

    private fun showLoading() {
        binding.bottomSheetParent.progressBar.isVisible = false
    }

    private fun showError() {
        val builder = AlertDialog.Builder(activity)
        with(builder)
        {
            setTitle("Network Connection")
            setMessage(getString(R.string.errorNetwork))
            show()
        }
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireActivity().applicationContext, Locale.getDefault())
        val address: Address?
        var addressText = ""

        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                address = addresses[firstElement]

                addressText = if(address.locality.isNullOrEmpty()) {
                    CITY_NOT_FOUND
                } else {
                    address.locality
                }
            } else {
                addressText = CITY_NOT_FOUND
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
                        latitude = location.latitude
                        longitude = location.longitude

                        viewModel.getWeatherInfo(location.latitude, location.longitude)
                        locationState.value = LatLng(latitude, longitude)
                        val city = getAddress(location.latitude, location.longitude)
                        val currentLocation = LatLng(latitude, longitude)
                        map.addMarker(MarkerOptions().position(currentLocation).title(city))
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLocation, MAP_ZOOM_LEVEL
                            )
                        )

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

    private fun checkState(): String {
        val city = when (manageState) {
            WeatherStateFlow.Success -> {
                getAddress(latitude, latitude)
            }
            else -> {
                CITY_NOT_FOUND
            }
        }
        return city

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
        val location = LatLng(location.latitude, location.longitude)
        val city = getAddress(location.latitude, location.longitude)
        map.addMarker(MarkerOptions().position(location).title(city))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, MAP_ZOOM_LEVEL))
        map.minZoomLevel
        locationState.value = LatLng(location.latitude, location.longitude)
        //send new LatLong
        viewModel.getWeatherInfo(location.latitude, location.longitude)
    }

}