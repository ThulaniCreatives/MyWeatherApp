package thulanicreatives.com.myweatherapp.weather.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import thulanicreatives.com.myweatherapp.weather.data.location.DefaultLocationTracker
import thulanicreatives.com.myweatherapp.weather.domain.repository.WeatherRepository
import thulanicreatives.com.myweatherapp.weather.domain.utils.Resource
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(

    private val repository: WeatherRepository,
    private val locationTracker: DefaultLocationTracker
) : ViewModel() {


    fun getWeatherInfo() {
        viewModelScope.launch {
            locationTracker.getCurrentLocation()?.let { location ->

                when (val result =
                    repository.getWeatherData(location.latitude, location.longitude)) {
                    is Resource.Success -> {
                        Log.i("ViewModel", "${result.data}")
                    }
                    is Resource.Error -> {

                    }
                }

            }
        }


    }
}