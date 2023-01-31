package thulanicreatives.com.myweatherapp.weather.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import thulanicreatives.com.myweatherapp.weather.data.location.DefaultLocationTracker
import thulanicreatives.com.myweatherapp.weather.domain.repository.WeatherRepository
import thulanicreatives.com.myweatherapp.weather.domain.utils.Resource
import thulanicreatives.com.myweatherapp.weather.domain.utils.WeatherStateFlow
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherData
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherInfo
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherType
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: DefaultLocationTracker
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<WeatherStateFlow>(WeatherStateFlow.Empty)
    val stateFlow: StateFlow<WeatherStateFlow> = _stateFlow


    @RequiresApi(Build.VERSION_CODES.O)
    val listofWeatherInfo =
        WeatherData(LocalDateTime.now(), 23.0, 34.0, 4.0, 80.0, WeatherType.fromWMO(1))
    @RequiresApi(Build.VERSION_CODES.O)
    val listOfWeatherInfo = listOf(listofWeatherInfo)


    @RequiresApi(Build.VERSION_CODES.O)
    val sampleData = mapOf(
        Pair(0, listOfWeatherInfo)
    )
    // private val _accountData = MutableStateFlow(TransactionHistoryConstants.sampleAccounts)
    //val accountData = _accountData.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private val _weatherInfo = MutableStateFlow(WeatherInfo(sampleData ,listofWeatherInfo))
    @RequiresApi(Build.VERSION_CODES.O)
    val weatherDate= _weatherInfo.asStateFlow()




    //    fun getWeatherInfo() {
//        viewModelScope.launch {
//            locationTracker.getCurrentLocation()?.let { location ->
//
//                when (val result =
//                    repository.getWeatherData(location.latitude, location.longitude)) {
//                    is Resource.Success -> {
//                        Timber.i("ViewModel", "${result.data}")
//                    }
//                    is Resource.Error -> {
//
//                    }
//                }
//
//            }
//        }
//    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeatherInfo(latitude: Double = 0.0, longitude: Double = 0.0) {

        viewModelScope.launch {
            _stateFlow.value = WeatherStateFlow.Loading
            when (val result =
                repository.getWeatherData(latitude, longitude)) {
                is Resource.Success -> {
                    _stateFlow.value = WeatherStateFlow.Success
                    _weatherInfo.value = result.data!!
                }
                is Resource.Error -> {
                    _stateFlow.value = WeatherStateFlow.Error("Error")
                }
            }


        }
    }
}