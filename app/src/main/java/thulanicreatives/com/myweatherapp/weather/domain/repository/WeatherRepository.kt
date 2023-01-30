package thulanicreatives.com.myweatherapp.weather.domain.repository

import thulanicreatives.com.myweatherapp.weather.domain.utils.Resource
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherInfo

interface WeatherRepository {
    suspend fun getWeatherData(latitude:Double, longitude:Double) : Resource<WeatherInfo>
}