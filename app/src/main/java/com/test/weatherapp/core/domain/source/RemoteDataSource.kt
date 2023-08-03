package com.test.weatherapp.core.domain.source

import com.test.weatherapp.core.data.source.remote.response.WeatherBaseResponse
import com.test.weatherapp.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun getWeather(param:HashMap<String, Any>): Flow<Resource<WeatherBaseResponse>>
    suspend fun getWeatherList(param: HashMap<String, Any>) : Flow<Resource<List<WeatherBaseResponse>>>
}