package thulanicreatives.com.myweatherapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import thulanicreatives.com.myweatherapp.databinding.ActivityMainBinding
import thulanicreatives.com.myweatherapp.weather.ui.WeatherViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    private var locationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)



        val bottomSheetParent = findViewById<ConstraintLayout>(R.id.bottom_sheet_parent)
        val mBottomSheetBehaviour = BottomSheetBehavior.from(binding.bottomSheetParent.root)
        val tv = TypedValue()
//        if (theme.resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)) {
//            val actionBarHeight = TypedValue.complexToDimensionPixelSize(
//                tv.data,
//                resources.displayMetrics
//            )
//            mBottomSheetBehaviour.peekHeight = actionBarHeight
//        }
    }

    private fun launchPermission() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            viewModel.getWeatherInfo()
        }
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ))
    }
}