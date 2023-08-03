package com.test.weatherapp.core.data.source.repository

import com.test.weatherapp.core.data.source.local.WeatherDataSource
import com.test.weatherapp.core.data.source.local.entity.WeatherBaseData
import com.test.weatherapp.core.data.source.remote.NetworkBoundResource
import com.test.weatherapp.core.data.source.remote.RemoteDataSourceImpl
import com.test.weatherapp.core.data.source.remote.response.WeatherBaseResponse
import com.test.weatherapp.core.domain.repository.WeatherRepository
import com.test.weatherapp.core.util.Resource
import com.test.weatherapp.core.util.mapListWeatherResponseToEntities
import com.test.weatherapp.core.util.mapWeatherResponseToEntities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val localData: WeatherDataSource,
    private val remoteData: RemoteDataSourceImpl,
) : WeatherRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    override suspend fun getWeather(param: HashMap<String, Any>): Flow<Resource<WeatherBaseData>> =
        object : NetworkBoundResource<WeatherBaseData, WeatherBaseResponse>() {
            override fun loadFromDB(): Flow<WeatherBaseData> {
                return localData.getWeather()
            }

            override fun shouldFetch(data: WeatherBaseData?): Boolean = true

            override suspend fun createCall(): Flow<Resource<WeatherBaseResponse>> {
                return remoteData.getWeather(param)
            }

            override suspend fun saveCallResult(data: WeatherBaseResponse) {
                localData.clearAll()
                localData.addWeather(data = mapWeatherResponseToEntities(data).copy(isMain = true))
            }
        }.asFlow()

    override suspend fun getWeatherList(
        param: HashMap<String, Any>
    ): Flow<Resource<List<WeatherBaseData>>> =
        object : NetworkBoundResource<List<WeatherBaseData>, List<WeatherBaseResponse>>() {
            override fun loadFromDB(): Flow<List<WeatherBaseData>> {
                return localData.getListWeather().map { it.filter {ft -> !ft.isMain } }
            }

            override fun shouldFetch(data: List<WeatherBaseData>?): Boolean =
                true

            override suspend fun createCall(): Flow<Resource<List<WeatherBaseResponse>>> {
               return remoteData.getWeatherList(param)
            }

            override suspend fun saveCallResult(data: List<WeatherBaseResponse>) {
                localData.clearExceptTop()
                val list = mapListWeatherResponseToEntities(data)
                localData.addListWeathers(list)
            }
        }.asFlow()

}