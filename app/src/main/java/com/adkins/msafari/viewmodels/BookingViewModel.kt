package com.adkins.msafari.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.adkins.msafari.firestore.Driver
import com.adkins.msafari.models.BookingData
import com.adkins.msafari.models.Traveler

class BookingViewModel : ViewModel() {
    var bookingData = mutableStateOf<BookingData?>(null)
        private set

    var travelerList = mutableStateOf<List<Traveler>>(emptyList())
        private set

    fun setBooking(data: BookingData) {
        bookingData.value = data
    }

    fun setTravelers(travelers: List<Traveler>) {
        travelerList.value = travelers
    }

    fun reset() {
        bookingData.value = null
        travelerList.value = emptyList()
    }

    var selectedDrivers = mutableStateListOf<Driver>()
        private set

    fun setSelectedDrivers(drivers: List<Driver>) {
        selectedDrivers.clear()
        selectedDrivers.addAll(drivers)
    }

}
