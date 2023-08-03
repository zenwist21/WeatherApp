package com.test.weatherapp.core.data.source.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Entity(tableName = "weatherData")
data class WeatherBaseData(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = 0,
    @ColumnInfo("name")
    val name: String? = null,
    @ColumnInfo("cod")
    val cod: String? = null,
    @ColumnInfo("timezone")
    val timezone: String? = null,
    @SerializedName("wind")
    val wind: WindData? = null,
    @ColumnInfo("mainData")
    val mainData: WeatherMainData? = WeatherMainData(),
    @ColumnInfo("coordination")
    val coordination: CoordinationData? = CoordinationData(),
    @ColumnInfo("weather")
    val weather: List<WeatherData> = emptyList(),
    @ColumnInfo("sys")
    val sys: SysData? = SysData(),
    val isMain: Boolean = false
)

data class WeatherMainData(
    val temp: Double? = null,
    val feels_like: Double? = null,
    val temp_min: Double? = null,
    val temp_max: Double? = null,
    val pressure: Double? = null,
    val humidity: Double? = null,
)

@Parcelize
data class WindData(
    val speed: String? = null,
    val deg: String? = null
) : Parcelable

@Parcelize
data class CoordinationData(
    val lon: String? = null,
    val lat: String? = null
) : Parcelable

@Parcelize
data class WeatherData(
    val id: String? = null,
    val main: String? = null,
    val description: String? = null,
) : Parcelable

@Parcelize
data class SysData(
    val country: String? = null,
    val timezone: Long = 0,
    val sunrise: Long = 0,
    val sunset: Long = 0,
) : Parcelable
