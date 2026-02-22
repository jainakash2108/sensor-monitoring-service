package com.example.repository

import com.example.humiditySensor
import com.example.temperatureSensor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SensorRepositoryTest {
    @Test
    fun `fetchAll should return all sensors provided in constructor`() {
        val sensors = listOf(temperatureSensor, humiditySensor)
        val repository = SensorRepository(sensors)

        val result = repository.fetchAll()

        assertEquals(2, result.size)
        assertEquals(sensors, result)
    }

    @Test
    fun `fetchAll should return default sensors when no list is provided`() {
        val repository = SensorRepository()
        val result = repository.fetchAll()

        assertEquals(2, result.size)
        assertEquals("Temperature", result[0].name)
    }
}
