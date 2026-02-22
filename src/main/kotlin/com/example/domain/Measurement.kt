package com.example.domain

data class Measurement(
    val sensorId: String,
    val value: Double,
    val type: SensorType,
)
