package thulanicreatives.com.myweatherapp.weather.domain.utils

import androidx.annotation.StringRes

sealed class WeatherStateFlow { // events that could happen here. only this class in sealed class can implement the sealed class.
    object Success:WeatherStateFlow()
    data class Error(@StringRes val errorMsg: Int) : WeatherStateFlow()
    object Loading: WeatherStateFlow()
}