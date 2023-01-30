package thulanicreatives.com.myweatherapp

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import thulanicreatives.com.myweatherapp.databinding.FragmentMapsBinding

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(-34.0, 151.0)
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
        //bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


//        val tv = TypedValue()
//        if (context?.theme?.resolveAttribute(androidx.transition.R.attr.actionBarSize, tv, true) == true) {
//            val actionBarHeight = TypedValue.complexToDimensionPixelSize(
//                tv.data,
//                resources.displayMetrics
//            )
//            bottomSheetBehavior.peekHeight = 500
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}