package com.weather.app.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class Coordinates(val latitude: Double, val longitude: Double)

class LocationProvider(context: Context) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context.applicationContext)

    /**
     * Caller is responsible for holding ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION before calling.
     *
     * Strategy: ask for a fresh fix first, fall back to the last known fix if the system
     * can't acquire one in time (common on emulators and indoors).
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Coordinates? {
        val fresh = awaitCurrentLocation()
        if (fresh != null) return fresh.toCoordinates()
        return awaitLastLocation()?.toCoordinates()
    }

    @SuppressLint("MissingPermission")
    private suspend fun awaitCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener { cont.resume(it) }
            .addOnFailureListener { e -> cont.resumeWithException(e) }
    }

    @SuppressLint("MissingPermission")
    private suspend fun awaitLastLocation(): Location? = suspendCancellableCoroutine { cont ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { cont.resume(it) }
            .addOnFailureListener { e -> cont.resumeWithException(e) }
    }

    private fun Location.toCoordinates() = Coordinates(latitude, longitude)
}
