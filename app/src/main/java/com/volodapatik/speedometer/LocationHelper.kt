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
    private var lastUpdateTime: Long = 0L

    // Thresholds for filtering
    private val maxAccuracyMeters = 50f
    private val minUpdateIntervalMs = 500L
    private val speedSmoothingFactor = 0.35f // light smoothing

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(onSpeedUpdate: (Int?) -> Unit) {
        stopLocationUpdates()

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L // 1 second
        ).apply {
            setMinUpdateIntervalMillis(500L)
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
        val now = System.currentTimeMillis()

        // Ignore low-quality locations
        if (location.hasAccuracy() && location.accuracy > maxAccuracyMeters) {
            return
        }

        // Rate limit visual updates slightly
        if (now - lastUpdateTime < minUpdateIntervalMs && lastValidSpeed != null) {
            return
        }

        val speedMps = if (location.hasSpeed()) {
            location.speed
        } else {
            0f
        }

        // Convert m/s → km/h
        var speedKmh = speedMps * 3.6f

        // Treat very low speeds as stationary (GPS noise)
        if (speedKmh < 1.2f) {
            speedKmh = 0f
        }

        // Light exponential smoothing to reduce jitter without lagging too much
        val smoothed = if (lastValidSpeed != null) {
            lastValidSpeed!! * (1f - speedSmoothingFactor) + speedKmh * speedSmoothingFactor
        } else {
            speedKmh
        }

        lastValidSpeed = smoothed
        lastUpdateTime = now

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
