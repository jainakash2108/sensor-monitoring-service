package com.example.domain

data class Sensor(
    val name: String,
    val port: Int,
    val threshold: Double,
    val type: SensorType,
) {
    fun createMeasurement(
        sensorId: String,
        value: Double,
    ): Measurement = Measurement(sensorId = sensorId, value = value, type = type)
}

enum class SensorType(
    val unit: String,
) {
    Temperature(unit = "°C"),
    Humidity(unit = "%"),
}
