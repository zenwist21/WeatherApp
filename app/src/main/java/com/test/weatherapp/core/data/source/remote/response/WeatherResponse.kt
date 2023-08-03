package com.test.weatherapp.core.data.source.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.test.weatherapp.core.data.source.local.entity.WeatherData
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherListResponse(
    val cnt: Int? = 0,
    val list: List<WeatherBaseResponse> = emptyList(),
):Parcelable



@Parcelize
data class WeatherBaseResponse(
    val id: Int? = 0,
    val name: String? = null,
    val cod: String? = null,
    val timezone: String? = null,
    @SerializedName("main")
    val mainData: WeatherMainDataResponse? = null,
    @SerializedName("wind")
    val wind: WindDataResponse? = null,
    @SerializedName("coord")
    val coordination: CoordinationDataResponse? = null,
    @SerializedName("sys")
    val sys: SysDataResponse? = null,
    val weather: List<WeatherDataResponse> = emptyList(),
    val isMain:Boolean = false
):Parcelable



@Parcelize
data class WeatherMainDataResponse(
    val temp: Double? = null,
    val feels_like: Double? = null,
    val temp_min: Double? = null,
    val temp_max: Double? = null,
    val pressure: Double? = null,
    val humidity: Double? = null,
):Parcelable

@Parcelize
data class CoordinationDataResponse(
    val lon: String? = null,
    val lat: String? = null
) : Parcelable

@Parcelize
data class SysDataResponse(
    val country: String? = null,
    val timezone: Long = 0,
    val sunrise: Long = 0,
    val sunset: Long = 0,
) : Parcelable


@Parcelize
data class WindDataResponse(
    val speed: String? = null,
    val deg: String? = null
) : Parcelable

@Parcelize
data class WeatherDataResponse(
    val id: String? = null,
    val main: String? = null,
    val description: String? = null,
) : Parcelable