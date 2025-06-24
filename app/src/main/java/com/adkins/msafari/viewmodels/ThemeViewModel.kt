package com.adkins.msafari.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(true) // default to dark
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkTheme.value = enabled
    }
}