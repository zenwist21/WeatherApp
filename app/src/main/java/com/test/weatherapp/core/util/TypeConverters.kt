package com.test.weatherapp.core.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.test.weatherapp.core.data.source.local.entity.CoordinationData
import com.test.weatherapp.core.data.source.local.entity.SysData
import com.test.weatherapp.core.data.source.local.entity.WeatherData
import com.test.weatherapp.core.data.source.local.entity.WeatherMainData
import com.test.weatherapp.core.data.source.local.entity.WindData

class DataConverter {

    @TypeConverter
    fun String.toWeatherObject(): List<WeatherData> = Gson().fromJson(this, object : TypeToken<List<WeatherData>>() {}.type)

    @TypeConverter
    fun String.toWindObject(): WindData = Gson().fromJson(this, object : TypeToken<WindData>() {}.type)

    @TypeConverter
    fun String.toMainObject(): WeatherMainData = Gson().fromJson(this, object : TypeToken<WeatherMainData>() {}.type)

    @TypeConverter
    fun String.toCoordObject(): CoordinationData = Gson().fromJson(this, object : TypeToken<CoordinationData>() {}.type)

    @TypeConverter
    fun String.toSysObject(): SysData = Gson().fromJson(this, object : TypeToken<SysData>() {}.type)

    @TypeConverter
    fun List<WeatherData>.toJson():String = Gson().toJson(this)

    @TypeConverter
    fun WindData.toJson():String = Gson().toJson(this)

    @TypeConverter
    fun WeatherMainData.toJson():String = Gson().toJson(this)

    @TypeConverter
    fun CoordinationData.toJson():String = Gson().toJson(this)

    @TypeConverter
    fun SysData.toJson():String = Gson().toJson(this)
}