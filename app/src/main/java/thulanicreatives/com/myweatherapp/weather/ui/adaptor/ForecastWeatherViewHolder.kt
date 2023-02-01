package thulanicreatives.com.myweatherapp.weather.ui.adaptor

import androidx.recyclerview.widget.RecyclerView
import thulanicreatives.com.myweatherapp.databinding.ItemForecastBinding
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ForecastWeatherViewHolder(val binding: ItemForecastBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        transactionHistory: WeatherData,
        lastItem: Boolean,
        showLoadingWhileStale: Boolean
    ) {
        with(binding) {
            txtTemp.text = transactionHistory.temperatureCelsius.toString()
            txtTempDesc.text = transactionHistory.weatherType.weatherDesc
            txtWeekDay.text = convertDate(transactionHistory.time)
            imvTransactionIcon.setImageDrawable(binding.root.context.getDrawable(transactionHistory.weatherType.iconRes))
        }
    }

    private fun convertDate(date: LocalDateTime): String {
        val localDateTime = LocalDateTime.parse(date.toString())
        val formatter = DateTimeFormatter.ofPattern("E")
        return formatter.format(localDateTime)
    }
}
