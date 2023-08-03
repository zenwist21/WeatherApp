package com.test.weatherapp.core.data.source.local

import com.test.weatherapp.core.data.source.local.dao.WeatherDao
import com.test.weatherapp.core.data.source.local.entity.WeatherBaseData
import com.test.weatherapp.core.data.source.local.entity.WeatherMainData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherDataSource @Inject constructor(private val dao: WeatherDao) {
    fun getListWeather() : Flow<List<WeatherBaseData>> = dao.getWeatherList()
    fun getWeather() : Flow<WeatherBaseData> = dao.getWeather()
    fun clearAll() = dao.deleteList()
    fun clearExceptTop() = dao.deleteExceptTop()
    fun addListWeathers(data: List<WeatherBaseData>) = dao.addWeatherListData(data)
    fun addWeather(data:WeatherBaseData) = dao.addWeatherData(data)
}