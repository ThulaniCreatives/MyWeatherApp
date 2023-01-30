package thulanicreatives.com.myweatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import thulanicreatives.com.myweatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


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
}