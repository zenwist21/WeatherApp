package com.test.weatherapp.core.data.source.remote.network

import com.test.weatherapp.core.data.source.remote.response.WeatherBaseResponse
import com.test.weatherapp.core.data.source.remote.response.WeatherListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiService {
    @GET("weather")
    suspend fun getWeather(@QueryMap param: HashMap<String, Any>): Response<WeatherBaseResponse>

    @GET("group")
    suspend fun getMultipleWeather(@QueryMap param: HashMap<String, Any>): Response<WeatherListResponse>
}