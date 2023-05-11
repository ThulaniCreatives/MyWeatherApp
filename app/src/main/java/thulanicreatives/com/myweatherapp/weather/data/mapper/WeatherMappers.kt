package thulanicreatives.com.myweatherapp.weather.data.mapper


import android.os.Build
import androidx.annotation.RequiresApi
import thulanicreatives.com.myweatherapp.weather.data.remote.WeatherDataDto
import thulanicreatives.com.myweatherapp.weather.data.remote.WeatherDto
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherData
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherInfo
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private data class IndexedWeatherData(
    val index: Int,
    val data: WeatherData
)

fun WeatherDataDto.toWeatherDataMap(): Map<Int, List<WeatherData>> {
    return time.mapIndexed { index, time ->
        val temperature = temperatures[index]
        val weatherCode = weatherCodes[index]
        val windSpeed = windSpeeds[index]
        val pressure = pressures[index]
        val humidity = humidities[index]
            IndexedWeatherData(
                index = index,
                data = WeatherData(
                    time = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME),
                    temperatureCelsius = temperature,
                    pressure = pressure,
                    windSpeed = windSpeed,
                    humidity = humidity,
                    weatherType = WeatherType.fromWMO(weatherCode)
                )
            )

    }.groupBy {
        it.index
    }.mapValues {
        it.value.map { it.data }
    }
}
fun WeatherDto.toWeatherInfo(): WeatherInfo {
    val weatherDataMap = weatherData.toWeatherDataMap()
    val now = LocalDateTime.now()
    val currentWeatherData = weatherDataMap[0]?.find {
        val hour = if(now.minute < 30) now.hour else now.hour + 1
        it.time.hour == hour
    }
    return WeatherInfo(
        weatherDataPerDay = weatherDataMap,
        currentWeatherData = currentWeatherData
    )
}