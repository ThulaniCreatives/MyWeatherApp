package com.sdb.retail.mobile.transaction.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import thulanicreatives.com.myweatherapp.databinding.ItemForecastBinding
import thulanicreatives.com.myweatherapp.weather.domain.weather.WeatherData
import thulanicreatives.com.myweatherapp.weather.ui.adaptor.ForecastWeatherViewHolder

class ForecastWeatherAdapter(private var transactionHistory: List<WeatherData>) :
    RecyclerView.Adapter<ForecastWeatherViewHolder>() {
    private var showLoadingWhileStale = true

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForecastWeatherViewHolder {
        val binding = ItemForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ForecastWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastWeatherViewHolder, position: Int) {
        with(transactionHistory[position]) {
            holder.bind(this, isLastItem(itemCount, position),showLoadingWhileStale)
        }
    }

    override fun getItemCount(): Int {
        return transactionHistory.size
    }

    fun onDataUpdate(transactionHistory: List<WeatherData>) {
        showLoadingWhileStale = true
        this.transactionHistory = transactionHistory
        notifyDataSetChanged()
    }

    private fun isLastItem(size: Int, position: Int) = size - 1 - position == 0

}