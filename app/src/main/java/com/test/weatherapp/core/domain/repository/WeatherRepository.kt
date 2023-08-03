package com.test.weatherapp.core.domain.repository

import com.test.weatherapp.core.data.source.local.entity.WeatherBaseData
import com.test.weatherapp.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeather(param: HashMap<String, Any>): Flow<Resource<WeatherBaseData>>
    suspend fun getWeatherList(
        param: HashMap<String, Any>
    ): Flow<Resource<List<WeatherBaseData>>>
}