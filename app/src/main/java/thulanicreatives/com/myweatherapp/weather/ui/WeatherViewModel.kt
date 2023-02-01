package thulanicreatives.com.myweatherapp.weather.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import thulanicreatives.com.myweatherapp.R
import thulanicreatives.com.myweatherapp.weather.data.location.DefaultLocationTracker
import thulanicreatives.com.myweatherapp.weather.domain.repository.WeatherRepository
import thulanicreatives.com.myweatherapp.weather.domain.utils.Resource
import thulanicreatives.com.myweatherapp.weather.domain.utils.WeatherStateFlow
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherData
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherInfo
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherType
import thulanicreatives.com.myweatherapp.weather.ui.Constants.listofWeatherInfo
import thulanicreatives.com.myweatherapp.weather.ui.Constants.sampleData
import thulanicreatives.com.myweatherapp.weather.ui.Constants.weekData
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: DefaultLocationTracker
) : ViewModel() {




    // API Results
    private val _stateFlow = MutableSharedFlow<WeatherStateFlow>()
    val stateFlow = _stateFlow.asSharedFlow()

    private val _weatherInfo = MutableStateFlow(WeatherInfo(sampleData ,listofWeatherInfo))
    val weatherDate= _weatherInfo.asStateFlow()

    fun getWeatherInfo(latitude: Double = 0.0, longitude: Double = 0.0) {

        viewModelScope.launch {
            _stateFlow.emit(WeatherStateFlow.Loading)
            when (val result =
                repository.getWeatherData(latitude, longitude)) {
                is Resource.Success -> {

                    _stateFlow.emit(WeatherStateFlow.Success)
                    _weatherInfo.value = forecastMapper(result.data!!)
                }
                is Resource.Error -> {
                    _stateFlow.emit(WeatherStateFlow.Error(R.string.errorNetwork))
                }
            }
        }
    }
    private fun forecastMapper(data: WeatherInfo):  WeatherInfo {
       val forecastData  = data.weatherDataPerDay.filter {
            it.key in weekData
        }
        return WeatherInfo(forecastData , data.currentWeatherData)
    }
}