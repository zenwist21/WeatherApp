package com.test.weatherapp.core.data.source.remote

import android.util.Log
import com.test.weatherapp.core.data.source.remote.network.ApiService
import com.test.weatherapp.core.data.source.remote.response.WeatherBaseResponse
import com.test.weatherapp.core.domain.source.RemoteDataSource
import com.test.weatherapp.core.util.NO_INTERNET
import com.test.weatherapp.core.util.NetworkConnectivity
import com.test.weatherapp.core.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService,
    private val networkConnectivity: NetworkConnectivity,
) : RemoteDataSource {

    override suspend fun getWeather(param: HashMap<String, Any>): Flow<Resource<WeatherBaseResponse>> {
        return flow {
            try {
                val responseCall = apiService::getWeather
                val response = responseCall.invoke(param)
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()))
                } else {
                    emit(Resource.DataError(response.message()))
                }
            } catch (e: IOException) {
                emit(Resource.DataError(errorMessage = e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }


    override suspend fun getWeatherList(param: HashMap<String, Any>): Flow<Resource<List<WeatherBaseResponse>>> {
        return flow {
            try {
                if (!networkConnectivity.isConnected()) {
                    emit(Resource.DataError(errorMessage = NO_INTERNET))
                    return@flow
                }
                val responseCall = apiService::getMultipleWeather
                val response = responseCall.invoke(param)
                if (response.isSuccessful) {
                    emit(Resource.Success(data = response.body()?.list ?: emptyList()))
                } else {
                    emit(Resource.DataError(response.message()))
                }
            } catch (e: IOException) {
                emit(Resource.DataError(errorMessage = e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }
}