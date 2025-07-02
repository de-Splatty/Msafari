package com.adkins.msafari.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adkins.msafari.firestore.DriverManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DriverInfoState(
    val fullName: String = "",
    val phoneNumber: String = "",
    val idNumber: String = "",
    val plateNumber: String = "",
    val vehicleType: String = "",
    val seater: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCompleted: Boolean = false
)

class DriverViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val driverId = auth.currentUser?.uid.orEmpty()

    private val _state = MutableStateFlow(DriverInfoState())
    val state: StateFlow<DriverInfoState> = _state

    fun updateField(
        fullName: String? = null,
        phoneNumber: String? = null,
        idNumber: String? = null,
        plateNumber: String? = null,
        vehicleType: String? = null,
        seater: Int? = null
    ) {
        _state.value = _state.value.copy(
            fullName = fullName ?: _state.value.fullName,
            phoneNumber = phoneNumber ?: _state.value.phoneNumber,
            idNumber = idNumber ?: _state.value.idNumber,
            plateNumber = plateNumber ?: _state.value.plateNumber,
            vehicleType = vehicleType ?: _state.value.vehicleType,
            seater = seater ?: _state.value.seater
        )
    }

    fun submitDriverInfo(onSuccess: () -> Unit) {
        val state = _state.value

        if (
            state.fullName.isBlank() || state.phoneNumber.isBlank() ||
            state.idNumber.isBlank() || state.plateNumber.isBlank() ||
            state.vehicleType.isBlank() || state.seater <= 0
        ) {
            _state.value = state.copy(errorMessage = "Please fill all fields correctly.")
            return
        }

        _state.value = state.copy(isLoading = true, errorMessage = null)

        DriverManager.saveDriverProfile(
            name = state.fullName.trim(),
            plateNumber = state.plateNumber.trim(),
            vehicleType = state.vehicleType,
            seater = state.seater,
            phoneNumber = state.phoneNumber.trim(),
            nationalId = state.idNumber.trim(),
            onSuccess = {
                _state.value = _state.value.copy(isLoading = false, isCompleted = true)
                onSuccess()
            },
            onFailure = { error ->
                _state.value = _state.value.copy(isLoading = false, errorMessage = error)
            }
        )
    }

    fun resetError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun loadDriverIfExists(onExists: () -> Unit) {
        viewModelScope.launch {
            DriverManager.getDriverProfile(driverId) { driver ->
                if (driver != null &&
                    driver.name.isNotBlank() &&
                    driver.vehicleType.isNotBlank() &&
                    driver.seater > 0 &&
                    driver.plateNumber.isNotBlank() &&
                    driver.nationalId.isNotBlank()
                ) {
                    onExists()
                }
            }
        }
    }
}