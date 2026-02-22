package com.example.domain

import com.example.temperatureSensor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SensorTest {
    @Test
    fun `createMeasurement should create measurement with correct values and type`() {
        val measurement = temperatureSensor.createMeasurement("t1", 25.5)

        assertEquals("t1", measurement.sensorId)
        assertEquals(25.5, measurement.value)
        assertEquals(temperatureSensor.type, measurement.type)
    }
}
