package thulanicreatives.com.myweatherapp.weather.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import thulanicreatives.com.myweatherapp.weather.data.location.DefaultLocationTracker
import thulanicreatives.com.myweatherapp.weather.domain.location.LocationTracker
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationTracker (defaultLocationTracker: DefaultLocationTracker) : LocationTracker
}