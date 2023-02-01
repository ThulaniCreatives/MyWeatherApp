package thulanicreatives.com.myweatherapp.weather.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import thulanicreatives.com.myweatherapp.weather.data.mapper.toWeatherInfo
import thulanicreatives.com.myweatherapp.weather.data.remote.WeatherAPI
import thulanicreatives.com.myweatherapp.weather.domain.repository.WeatherRepository
import thulanicreatives.com.myweatherapp.weather.domain.utils.Resource
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherInfo
import javax.inject.Inject

class WeatherRepositoryImp @Inject constructor(private val api: WeatherAPI) : WeatherRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Resource<WeatherInfo> {
        return try {
            Resource.Success(
                data = api.getWeatherData(
                    lat = latitude,
                    long = longitude
                ).toWeatherInfo()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unkown error occured")
        }
    }
}