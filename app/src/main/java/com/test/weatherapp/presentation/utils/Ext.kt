package com.test.weatherapp.presentation.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.BlurMaskFilter
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.test.weatherapp.BuildConfig
import com.test.weatherapp.R
import com.test.weatherapp.presentation.theme.Grey200
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import okhttp3.internal.UTC
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

fun Modifier.customShadow(
    color: Color = Color.Black,
    borderRadius: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0f.dp,
    modifier: Modifier = Modifier
) = this.then(
    modifier.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            val spreadPixel = spread.toPx()
            val leftPixel = (0f - spreadPixel) + offsetX.toPx()
            val topPixel = (0f - spreadPixel) + offsetY.toPx()
            val rightPixel = (this.size.width + spreadPixel)
            val bottomPixel = (this.size.height + spreadPixel)

            if (blurRadius != 0.dp) {
                frameworkPaint.maskFilter =
                    (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }

            frameworkPaint.color = color.toArgb()
            it.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx(),
                paint
            )
        }
    }
)

@Composable
fun Context.LocationPermissionLauncher(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        when {
            ContextCompat.checkSelfPermission(
                this@LocationPermissionLauncher,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                onPermissionGranted()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this@LocationPermissionLauncher.findActivity() as ComponentActivity,
                permission
            ) -> {
                // You can show a rationale here if needed and request permission again
                onPermissionDenied()
            }

            else -> {
                // Request the permission
                locationPermissionLauncher.launch(permission)
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun convertLongToTimeStringUsingDateTimeFormatter(timeInMillis: Long): String {
    val date = Date(timeInMillis)
    val format = SimpleDateFormat("HH")
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(date)
}

fun isDayTime(timeZone: Long? = null): Boolean {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    timeZone?.let {
       calendar.timeInMillis = calendar.timeInMillis +(it * 1000)
    }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val sunriseHour = 6 // Set your local sunrise hour
    val sunsetHour = 18 // Set your local sunset hour
    Log.e("TAG", "isDayTime: $hour", )
    return hour in (sunriseHour + 1) until sunsetHour
}


fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun weatherParam(
    getMultiple: Boolean = false,
    lat: String? = null,
    long: String? = null
): HashMap<String, Any> {
    val params: HashMap<String, Any> = HashMap()
    if (getMultiple)
        params["id"] = "5128638,1880252,1275339,1273294,2147714,2158177"
    else {
        params["lat"] = lat ?: ""
        params["lon"] = long ?: ""
    }
    params["appid"] = BuildConfig.ACCESS_TOKEN
    params["units"] = "metric"
    return params
}

fun Modifier.shimmerBackground(shape: Shape = RectangleShape): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
            RepeatMode.Restart
        ),
    )
    val shimmerColors = listOf(
        Grey200,
        Grey200.copy(alpha = 0.8f),
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation, translateAnimation),
        end = Offset(translateAnimation + 100f, translateAnimation + 100f),
        tileMode = TileMode.Mirror,
    )
    return@composed this.then(background(brush, shape))
}

fun getWeatherIcon(param: String, timeZone: Long? = null): Int {
    val currentDay = isDayTime(timeZone)
    return when (param) {
        "Thunderstorm" -> {
            R.raw.ic_thunder_storm
        }
        "Clear" -> {
            if (currentDay) R.raw.ic_clear_day else R.raw.ic_clear_night
        }

        "Clouds" -> {
            if (currentDay) R.raw.ic_cloud_day else R.raw.ic_cloud_night
        }

        "Snow" -> {
            R.raw.ic_snow
        }

        "Rain" -> {
            if (currentDay) R.raw.ic_day_rain else R.raw.ic_night_rain
        }

        "Drizzle" -> {
            if (currentDay) R.raw.ic_day_rain else R.raw.ic_night_rain
        }

        else -> {
            R.raw.ic_mist
        }
    }

}

@SuppressLint("SimpleDateFormat")
fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("dd MMMM yyyy")
    return dateFormat.format(Calendar.getInstance().time)
}

fun String.toCapitalized() = replaceFirstChar { it.uppercase() }


@ExperimentalCoroutinesApi
fun <T, K> StateFlow<T>.mapState(
    scope: CoroutineScope,
    initialValue: K,
    transform: suspend (data: T) -> K
): StateFlow<K> {
    return mapLatest {
        transform(it)
    }
        .stateIn(scope, SharingStarted.Eagerly, initialValue)
}
