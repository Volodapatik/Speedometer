package com.volodapatik.speedometer

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.math.roundToInt

class LocationHelper(context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null
    private var lastValidSpeed: Float? = null

    // More responsive settings for car use
    private val maxAccuracyMeters = 40f
    // Very light smoothing — almost instant reaction, still kills strong jitter
    private val speedSmoothingFactor = 0.55f

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(onSpeedUpdate: (Int?) -> Unit) {
        stopLocationUpdates()

        // Fast updates: ~3–5 times per second when possible
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            300L // preferred interval 300 ms
        ).apply {
            setMinUpdateIntervalMillis(150L)   // allow as fast as 150 ms
            setMaxUpdateDelayMillis(500L)      // don't batch too much
            setMinUpdateDistanceMeters(0f)
            setWaitForAccurateLocation(false)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                processLocation(location, onSpeedUpdate)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    private fun processLocation(location: Location, onSpeedUpdate: (Int?) -> Unit) {
        // Ignore very bad accuracy
        if (location.hasAccuracy() && location.accuracy > maxAccuracyMeters) {
            return
        }

        val speedMps = if (location.hasSpeed() && location.speed >= 0f) {
            location.speed
        } else {
            0f
        }

        // m/s → km/h
        var speedKmh = speedMps * 3.6f

        // Kill GPS noise at near-zero speed
        if (speedKmh < 1.0f) {
            speedKmh = 0f
        }

        // Very light exponential smoothing (keeps reaction fast)
        val smoothed = if (lastValidSpeed != null) {
            lastValidSpeed!! * (1f - speedSmoothingFactor) + speedKmh * speedSmoothingFactor
        } else {
            speedKmh
        }

        lastValidSpeed = smoothed

        // Round to nearest integer
        val displaySpeed = smoothed.roundToInt()

        onSpeedUpdate(displaySpeed)
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        locationCallback = null
    }
}
