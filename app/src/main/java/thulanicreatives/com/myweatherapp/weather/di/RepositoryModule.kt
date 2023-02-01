package thulanicreatives.com.myweatherapp.weather.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import thulanicreatives.com.myweatherapp.weather.data.location.DefaultLocationTracker
import thulanicreatives.com.myweatherapp.weather.data.repository.WeatherRepositoryImp
import thulanicreatives.com.myweatherapp.weather.domain.location.LocationTracker
import thulanicreatives.com.myweatherapp.weather.domain.repository.WeatherRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository (weatherRepositoryImp: WeatherRepositoryImp) : WeatherRepository
}