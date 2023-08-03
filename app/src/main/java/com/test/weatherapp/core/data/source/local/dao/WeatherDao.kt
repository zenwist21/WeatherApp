package com.test.weatherapp.core.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.test.weatherapp.core.data.source.local.entity.WeatherBaseData
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {
    @Query("SELECT * FROM weatherData")
    fun getWeatherList(): Flow<List<WeatherBaseData>>

    @Query("SELECT * FROM weatherData WHERE isMain = :isMain")
    fun getWeather(isMain: Boolean = true): Flow<WeatherBaseData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWeatherData(data: WeatherBaseData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addListWeatherData(data: List<WeatherBaseData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWeatherListData(data: List<WeatherBaseData>)

    @Query("DELETE FROM weatherData")
    fun deleteList()

    @Query("DELETE FROM weatherData WHERE isMain = :isMain ")
    fun deleteExceptTop(isMain: Boolean = false)
}