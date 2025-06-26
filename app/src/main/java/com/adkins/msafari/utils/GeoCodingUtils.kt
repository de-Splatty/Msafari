package com.adkins.msafari.util

import android.content.Context
import android.location.Geocoder
import android.location.Address
import java.util.*

object GeocodingUtils {

    fun getCoordinatesFromAddress(context: Context, location: String): Pair<Double, Double>? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocationName(location, 1) ?: emptyList()
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                Pair(address.latitude, address.longitude)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}