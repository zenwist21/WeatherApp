package com.test.weatherapp.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.test.weatherapp.core.data.source.local.dao.WeatherDao
import com.test.weatherapp.core.data.source.local.entity.WeatherBaseData
import com.test.weatherapp.core.util.DataConverter

@Database(
    entities = [WeatherBaseData::class], version = 1, exportSchema = true
)

@TypeConverters(DataConverter::class)
abstract class WeatherDb : RoomDatabase() {
    abstract fun weatherDao() : WeatherDao
}