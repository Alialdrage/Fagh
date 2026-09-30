package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.model.UserLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationHelper(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineGranted || coarseGranted
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): UserLocation? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission()) return@withContext null

        try {
            // First check fresh current location with cancellation token
            val cancellationTokenSource = CancellationTokenSource()
            val locationTask = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            )

            val location = com.google.android.gms.tasks.Tasks.await(locationTask)
                ?: run {
                    val lastTask = fusedLocationClient.lastLocation
                    com.google.android.gms.tasks.Tasks.await(lastTask)
                }

            if (location != null) {
                val cityInfo = reverseGeocode(location.latitude, location.longitude)
                UserLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    cityName = cityInfo.first,
                    countryName = cityInfo.second,
                    isGps = true
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun reverseGeocode(lat: Double, lng: Double): Pair<String, String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "موقعي الحالي"
                val country = addr.countryName ?: ""
                city to country
            } else {
                String.format(Locale.getDefault(), "%.4f, %.4f", lat, lng) to ""
            }
        } catch (e: Exception) {
            String.format(Locale.getDefault(), "%.4f, %.4f", lat, lng) to ""
        }
    }
}
