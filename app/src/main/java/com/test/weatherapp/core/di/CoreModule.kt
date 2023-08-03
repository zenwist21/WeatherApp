package com.test.weatherapp.core.di

import android.content.Context
import androidx.room.Room
import com.test.weatherapp.BuildConfig
import com.test.weatherapp.core.data.source.local.room.WeatherDb
import com.test.weatherapp.core.data.source.remote.network.ApiService
import com.test.weatherapp.core.util.AdapterFactory
import com.test.weatherapp.core.util.Network
import com.test.weatherapp.core.util.NetworkConnectivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): WeatherDb =
        Room.databaseBuilder(appContext, WeatherDb::class.java, "myAccount.db").build()

    @Provides
    fun provideDao(db: WeatherDb) = db.weatherDao()

    @Provides
    @Singleton
    fun provideCoroutineContext(): CoroutineContext {
        return Dispatchers.IO
    }


    @Provides
    @Singleton
    fun provideNetworkConnectivity(@ApplicationContext context: Context): NetworkConnectivity {
        return Network(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor) = OkHttpClient.Builder()
        .pingInterval(1, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val method = original.method
            val body = original.body
            val request = original.newBuilder()
                .header("Accept", "application/json")
                .method(method, body)
                .build()
            return@addInterceptor chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            loggingInterceptor.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return loggingInterceptor
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideOkHttp(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(AdapterFactory())
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .build()
}