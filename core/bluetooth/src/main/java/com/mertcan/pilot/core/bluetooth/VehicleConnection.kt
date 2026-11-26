package com.mertcan.pilot.core.bluetooth

sealed interface VehicleConnection {
    data object Disconnected : VehicleConnection
    data class Connected(val deviceName: String?) : VehicleConnection
}
