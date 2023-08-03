package com.test.weatherapp.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.weatherapp.core.data.source.local.entity.WeatherBaseData
import com.test.weatherapp.core.domain.repository.WeatherRepository
import com.test.weatherapp.core.util.Resource
import com.test.weatherapp.presentation.utils.mapState
import com.test.weatherapp.presentation.utils.weatherParam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: WeatherRepository
) : ViewModel() {
    private var _state = MutableStateFlow(HomeState())

    val loading: StateFlow<Boolean> =
        _state.mapState(viewModelScope, initialValue = false) { it.isLoading }
    val loadingList: StateFlow<Boolean> =
        _state.mapState(viewModelScope, initialValue = false) { it.isLoadingList }
    val response: StateFlow<WeatherBaseData?> =
        _state.mapState(viewModelScope, initialValue = null) { it.response }
    val responseList: StateFlow<List<WeatherBaseData>> =
        _state.mapState(viewModelScope, initialValue = emptyList()) { it.responseList }
    fun getWeather(lat: String, long: String) {
        viewModelScope.launch {
            repo.getWeather(weatherParam(lat = lat, long = long)).onEach {
                when (it) {
                    is Resource.DataError -> {
                        _state.update { state -> state.copy(isLoading = false) }
                    }

                    is Resource.Loading -> {
                        _state.update { state -> state.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _state.update { state ->
                            state.copy(
                                isLoading = false,
                                response = it.data
                            )
                        }
                    }
                }
            }.launchIn(this)
        }
    }
    fun getListWeather() {
        viewModelScope.launch {
            repo.getWeatherList(param = weatherParam(getMultiple = true)).onEach {
                when (it) {
                    is Resource.DataError -> {
                        _state.update { state -> state.copy(isLoadingList = false) }
                    }

                    is Resource.Loading -> {
                        _state.update { state -> state.copy(isLoadingList = true) }
                    }

                    is Resource.Success -> {
                        _state.update { state ->
                            state.copy(
                                isLoadingList = false,
                                responseList =  it.data ?: emptyList()
                            )
                        }
                    }
                }
            }.launchIn(this)
        }
    }
}

data class HomeState(
    val isLoading: Boolean = false,
    val isLoadingList: Boolean = false,
    val response: WeatherBaseData? = null,
    val responseList: List<WeatherBaseData> = emptyList(),
)