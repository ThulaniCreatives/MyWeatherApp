package thulanicreatives.com.myweatherapp.weather.ui

import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherInfo

data class WeatherState(
    val WeatherInfo: WeatherInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null

)
