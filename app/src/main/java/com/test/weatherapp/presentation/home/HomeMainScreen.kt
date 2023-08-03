package com.test.weatherapp.presentation.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.test.weatherapp.R
import com.test.weatherapp.core.data.source.local.entity.WeatherBaseData
import com.test.weatherapp.presentation.theme.NunitoFont
import com.test.weatherapp.presentation.theme.Typography
import com.test.weatherapp.presentation.theme.Yellow700
import com.test.weatherapp.presentation.utils.LocationPermissionLauncher
import com.test.weatherapp.presentation.utils.customShadow
import com.test.weatherapp.presentation.utils.getCurrentDate
import com.test.weatherapp.presentation.utils.getWeatherIcon
import com.test.weatherapp.presentation.utils.isDayTime
import com.test.weatherapp.presentation.utils.shimmerBackground
import com.test.weatherapp.presentation.utils.toCapitalized
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
    val loading: Boolean by viewModel.loading.collectAsState()
    val loadingList: Boolean by viewModel.loadingList.collectAsState()
    val data by viewModel.response.collectAsState()
    val listData by viewModel.responseList.collectAsState()
    var location by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier,
        backgroundColor = Color.Black,
    ) {
        context.LocationPermissionLauncher(
            onPermissionGranted = {
                coroutineScope.launch {
                    var mFusedLocationClient: FusedLocationProviderClient?
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    mFusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        object : CancellationToken() {
                            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken =
                                CancellationTokenSource().token

                            override fun isCancellationRequested(): Boolean = false
                        }).addOnSuccessListener { loc ->
                        if (loc == null) {
                            locationError = "Cannot Find Location"
                            location = false
                        } else {
                            location = true
                            viewModel.getWeather(
                                lat = loc.latitude.toString(),
                                long = loc.longitude.toString()
                            )
                            mFusedLocationClient = null
                        }
                    }
                }
            },
            onPermissionDenied = {
                locationError = context.getString(R.string.cannot_get_location)
            })

        if (!location) {
            LoadingScreen(text = stringResource(id = R.string.app_name))
            if (locationError.isNotEmpty()) Toast.makeText(
                context,
                locationError,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Column(Modifier.padding(it)) {
                if (loading) {
                    LoadingScreen()
                }
                else {
                    data?.let { data ->
                        //Calling api
                        viewModel.getListWeather()

                        MainCard(data)
                        Text(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            text = stringResource(id = R.string.other_countries_weather),
                            style = Typography.titleLarge.copy(
                                color = Color.White,
                                fontFamily = NunitoFont,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp,
                            )
                        )

                        LazyRow(modifier = Modifier.padding(top = 20.dp)) {
                            if (loadingList) {
                                items(10) {
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 20.dp)
                                            .size(200.dp)
                                            .shimmerBackground(RoundedCornerShape(20.dp))
                                    )
                                }
                            } else {
                                items(listData) { data ->
                                    ItemWeatherAdditional(data = data)
                                }
                            }

                        }
                    }
                }
            }
        }

    }
}

@Composable
@Preview
fun MainCard(data: WeatherBaseData = WeatherBaseData()) {
    Card(
        Modifier
            .height(400.dp)
            .padding(15.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Yellow700
    ) {
        Column(
            Modifier.padding(vertical = 25.dp, horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = data.name ?: stringResource(id = R.string.location_name),
                    style = Typography.titleLarge.copy(
                        color = Color.Black,
                        fontFamily = NunitoFont,
                        fontSize = 32.sp,
                    )
                )
                Text(
                    text = getCurrentDate(),
                    style = Typography.titleLarge.copy(
                        color = Color.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                    )
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Current weather
                Text(
                    text = data.weather[0].description?.toCapitalized()
                        ?: stringResource(id = R.string.dummy_current_weather),
                    style = Typography.titleLarge.copy(
                        color = Color.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                    )
                )
                Text(
                    text = stringResource(
                        id = R.string.temperature,
                        data.mainData?.temp?.toString()
                            ?: stringResource(id = R.string.dummy_temperature)
                    ),
                    style = Typography.titleLarge.copy(
                        color = Color.Black,
                        fontFamily = NunitoFont,
                        fontSize = 60.sp,
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .size(80.dp)
                        .customShadow(color = Color.White, spread = 2.dp, blurRadius = 15.dp)
                        .background(
                            color = Color.White, shape = RoundedCornerShape(15.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    //Icon
                    val composition by rememberLottieComposition(
                        spec = LottieCompositionSpec.RawRes(
                            getWeatherIcon(data.weather[0].main ?: "Clouds")
                        )
                    )
                    LottieAnimation(
                        modifier = Modifier.size(60.dp),
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                    )
                    //                    Image(
//                        modifier = Modifier.size(60.dp),
//                        painter = painterResource(id = R.drawable.ic_day),
//                        contentDescription = stringResource(id = R.string.icon_point)
//                    )
                }
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp, top = 26.dp, bottom = 26.dp),
                    text = data.weather[0].main + if (isDayTime()) " Day " else " Night ",
                    style = Typography.titleLarge.copy(
                        color = Color.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                    )
                )

                Column(
                    modifier = Modifier
                        .background(
                            color = Color.Black, shape = RoundedCornerShape(15.dp)
                        )
                        .height(80.dp)
                        .width(65.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    //Icon
                    Image(
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .size(15.dp),
                        painter = painterResource(id = R.drawable.ic_wind),
                        contentDescription = stringResource(id = R.string.icon_point)
                    )
                    Text(
                        text = stringResource(id = R.string.wind),
                        style = Typography.titleLarge.copy(
                            color = Color.White,
                            fontFamily = NunitoFont,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.wind_kmh, data.wind?.speed ?: "0"),
                        style = Typography.titleLarge.copy(
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                        )
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .customShadow(color = Color.White, spread = 1.dp, blurRadius = 20.dp)
                        .background(
                            color = Color.White, shape = RoundedCornerShape(15.dp)
                        )
                        .height(80.dp)
                        .width(65.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    //Icon
                    Image(
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .size(15.dp),
                        painter = painterResource(id = R.drawable.ic_water),
                        contentDescription = stringResource(id = R.string.icon_point)
                    )
                    Text(
                        text = stringResource(id = R.string.humidity),
                        style = Typography.titleLarge.copy(
                            color = Color.Black,
                            fontFamily = NunitoFont,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                        )
                    )
                    Text(
                        text = stringResource(
                            id = R.string.humidity_value,
                            data.mainData?.humidity.toString()
                        ),
                        style = Typography.titleLarge.copy(
                            color = Color.Black,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                        )
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ItemWeatherAdditional(data: WeatherBaseData? = WeatherBaseData()) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(getWeatherIcon(data?.weather?.get(0)?.main ?: "Clouds", timeZone = data?.sys?.timezone.toString()))
    )
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .size(200.dp)
            .background(
                color = Color.White, shape = RoundedCornerShape(15.dp)
            )
    ) {
        LottieAnimation(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f),
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //Icon
            Text(
                text = data?.name ?: stringResource(id = R.string.location_name),
                style = Typography.titleLarge.copy(
                    color = Color.Black,
                    fontFamily = NunitoFont,
                    fontSize = 22.sp,
                )
            )
            Text(
                modifier = Modifier.padding(top = 15.dp),
                text = data?.weather?.get(0)?.description
                    ?: stringResource(id = R.string.dummy_current_weather),
                style = Typography.titleLarge.copy(
                    color = Color.Black,
                    fontFamily = NunitoFont,
                    fontSize = 13.sp,
                )
            )
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(id = R.string.temperature, data?.mainData?.temp ?: "0"),
                style = Typography.titleLarge.copy(
                    color = Color.Black,
                    fontFamily = NunitoFont,
                    fontSize = 38.sp,
                )
            )
        }
    }
}

@Composable
fun LoadingScreen(text: String = stringResource(id = R.string.please_wait)) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ic_loading_anim))
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            modifier = Modifier.size(300.dp),
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
        Text(
            modifier = Modifier.padding(top = 15.dp),
            text = text,
            style = Typography.titleLarge.copy(
                color = Color.White,
                fontFamily = NunitoFont,
                fontSize = 18.sp,
            )
        )
    }
}

