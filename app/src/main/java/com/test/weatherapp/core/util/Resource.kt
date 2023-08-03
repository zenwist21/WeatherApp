package com.test.weatherapp.core.util

sealed class Resource<T>(
    val data: T? = null,
    val errorMessage: Any? = null,
) {
    class Success<T>(data: T? = null) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class DataError<T>(errorMessage: Any) :
        Resource<T>(null, errorMessage = errorMessage)

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is DataError -> "Error[exception=$errorMessage"
            is Loading<T> -> "Loading"
        }
    }
}