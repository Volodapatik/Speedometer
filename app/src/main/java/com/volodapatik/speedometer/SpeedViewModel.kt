package com.volodapatik.speedometer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SpeedState(
    val speedKmh: Int? = null, // null = not yet acquired (show "—")
    val isAcquired: Boolean = false
)

class SpeedViewModel : ViewModel() {

    private val _speedState = MutableStateFlow(SpeedState())
    val speedState: StateFlow<SpeedState> = _speedState.asStateFlow()

    fun updateSpeed(speedKmh: Int?) {
        _speedState.value = SpeedState(
            speedKmh = speedKmh,
            isAcquired = speedKmh != null
        )
    }
}
