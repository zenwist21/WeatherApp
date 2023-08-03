package com.test.weatherapp.core.util

import android.util.Log
import com.test.weatherapp.core.data.source.local.entity.CoordinationData
import com.test.weatherapp.core.data.source.local.entity.SysData
import com.test.weatherapp.core.data.source.local.entity.WeatherBaseData
import com.test.weatherapp.core.data.source.local.entity.WeatherData
import com.test.weatherapp.core.data.source.local.entity.WeatherMainData
import com.test.weatherapp.core.data.source.local.entity.WindData
import com.test.weatherapp.core.data.source.remote.response.CoordinationDataResponse
import com.test.weatherapp.core.data.source.remote.response.SysDataResponse
import com.test.weatherapp.core.data.source.remote.response.WeatherBaseResponse
import com.test.weatherapp.core.data.source.remote.response.WeatherDataResponse
import com.test.weatherapp.core.data.source.remote.response.WeatherMainDataResponse
import com.test.weatherapp.core.data.source.remote.response.WindDataResponse

fun mapWeatherResponseToEntities(input: WeatherBaseResponse): WeatherBaseData =
    WeatherBaseData(
        id = input.id,
        name = input.name,
        cod = input.cod,
        timezone = input.timezone,
        weather = input.weather.mapToEntities(),
        mainData = input.mainData?.mapToEntities(),
        coordination = input.coordination?.mapToEntities(),
        wind = input.wind?.mapToEntities(),
        sys = input.sys?.mapToEntities()
    )



fun mapListWeatherResponseToEntities(input: List<WeatherBaseResponse>): List<WeatherBaseData> {
    val mutableList = mutableListOf<WeatherBaseData>()
    input.forEach { temp ->
        mutableList.add(
            WeatherBaseData(
                id = temp.id,
                name = temp.name,
                cod = temp.cod,
                timezone = temp.timezone,
                weather = temp.weather.mapToEntities(),
                mainData = temp.mainData?.mapToEntities(),
                coordination = temp.coordination?.mapToEntities(),
                wind = temp.wind?.mapToEntities(),
                sys = temp.sys?.mapToEntities()
            )
        )
    }
    Log.e("TAG", "mapListWeatherResponseToEntities: $mutableList")
    return mutableList
}


fun List<WeatherDataResponse>.mapToEntities(): List<WeatherData> {
    val mutableList = mutableListOf<WeatherData>()
    this.forEach { mutableList.add(WeatherData(it.id, it.main, it.description)) }
    return mutableList
}

fun WindDataResponse.mapToEntities(): WindData = WindData(speed, deg)
fun SysDataResponse.mapToEntities(): SysData = SysData(country, timezone, sunrise, sunset)
fun WeatherMainDataResponse.mapToEntities(): WeatherMainData =
    WeatherMainData(temp, feels_like, temp_min, temp_max, pressure, humidity)

fun CoordinationDataResponse.mapToEntities(): CoordinationData = CoordinationData(lon, lat)