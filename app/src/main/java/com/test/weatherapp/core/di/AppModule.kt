package com.test.weatherapp.core.di

import com.test.weatherapp.core.data.source.repository.WeatherRepositoryImpl
import com.test.weatherapp.core.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun provideWeatherRepo(repository: WeatherRepositoryImpl) : WeatherRepository


}