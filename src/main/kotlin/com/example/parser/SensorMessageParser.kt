package com.example.parser

import com.example.domain.ParseResult
import com.example.domain.Sensor

class SensorMessageParser {
    fun parse(
        message: String,
        sensor: Sensor,
    ): ParseResult =
        runCatching {
            val parts =
                message
                    .split(";")
                    .filter { it.isNotBlank() }
                    .associate {
                        val split = it.split("=")
                        if (split.size < 2) throw IllegalArgumentException("Invalid format: $it")
                        val key = split[0].trim().lowercase()
                        val value = split[1].trim()
                        key to value
                    }

            val sensorId: String = parts["sensor_id"] ?: throw IllegalArgumentException("Message does not contain sensor_id")
            val value: Double = parts["value"]?.toDouble() ?: throw IllegalArgumentException("Message does not contain sensor's value")

            ParseResult.Success(sensor.createMeasurement(sensorId, value))
        }.getOrElse { e ->
            ParseResult.Failure(message, e.message ?: "Unknown error")
        }
}
