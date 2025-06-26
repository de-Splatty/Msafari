package com.adkins.msafari.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.adkins.msafari.firestore.Driver
import com.adkins.msafari.models.BookingData
import com.adkins.msafari.models.Traveler

class BookingViewModel : ViewModel() {

    // ✅ Booking Info
    var bookingData = mutableStateOf<BookingData?>(null)
        private set

    var travelerList = mutableStateOf<List<Traveler>>(emptyList())
        private set

    var selectedDrivers = mutableStateListOf<Driver>()
        private set

    // ✅ NEW: Active Trip Flag
    var activeTrip = mutableStateOf(false)
        private set

    // ✅ Set trip state
    fun setActiveTrip(state: Boolean) {
        activeTrip.value = state
    }

    fun isTripActive(): Boolean {
        return activeTrip.value
    }

    fun setBooking(data: BookingData) {
        bookingData.value = data
    }

    fun setTravelers(travelers: List<Traveler>) {
        travelerList.value = travelers
    }

    fun setSelectedDrivers(drivers: List<Driver>) {
        selectedDrivers.clear()
        selectedDrivers.addAll(drivers)
    }

    fun reset() {
        bookingData.value = null
        travelerList.value = emptyList()
        selectedDrivers.clear()
        activeTrip.value = false // Reset trip status
    }
}