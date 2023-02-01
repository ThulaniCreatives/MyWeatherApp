package thulanicreatives.com.myweatherapp.weather.ui

import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherData
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Constants {
    val weekData = listOf(12,36,60,84,108,132,156) //  this list select weekdays data, forecasted midDay
    val listofWeatherInfo =
        WeatherData(LocalDateTime.now(), 23.0, 34.0, 4.0, 80.0, WeatherType.fromWMO(1))

    val listOfWeatherInfo = listOf(listofWeatherInfo)
    val sampleData = mapOf(
        Pair(0, listOfWeatherInfo)
    )
}