package com.test.weatherapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.location.FusedLocationProviderClient
import com.test.weatherapp.presentation.home.HomeScreen
import com.test.weatherapp.presentation.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme(darkTheme = false) {
                HomeScreen()
            }
        }
    }

//    private fun activateLocationService() {
//        if (!checkPermissionLocation(this)) {
//            this.getLocationServices()
//        } else if (!isLocationEnabled(this)) {
//            this.getLocationServices()
//        } else {
//            getLocation()
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun getLocation(){
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
//        mFusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,
//            object : CancellationToken() {
//                override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken =
//                    CancellationTokenSource().token
//
//                override fun isCancellationRequested(): Boolean = false
//            }).addOnSuccessListener { location ->
//            if (location == null) Log.e("TAG", "getLocation: Cannot Find Location")
//            else {
//
//            }
//        }
//    }
}

