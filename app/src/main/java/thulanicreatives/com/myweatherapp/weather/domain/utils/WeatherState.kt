package thulanicreatives.com.myweatherapp.weather.domain.utils

sealed class WeatherStateFlow { // events that could happen here. only this class in sealed class can implement the sealed class.
    object Success:WeatherStateFlow()
    data class Error(val errorMessage:String) : WeatherStateFlow()
    object Loading: WeatherStateFlow()
    object Empty: WeatherStateFlow()
}