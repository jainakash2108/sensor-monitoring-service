package com.example.repository

import com.example.domain.Sensor
import com.example.domain.SensorType

class SensorRepository(
    private val sensors: List<Sensor> = defaultSensors(),
) {
    fun fetchAll(): List<Sensor> = sensors

    companion object {
        fun defaultSensors() =
            listOf(
                Sensor(
                    name = "Temperature",
                    port = 3344,
                    threshold = 35.0,
                    type = SensorType.Temperature,
                ),
                Sensor(
                    name = "Humidity",
                    port = 3355,
                    threshold = 50.0,
                    type = SensorType.Humidity,
                ),
            )
    }
}
