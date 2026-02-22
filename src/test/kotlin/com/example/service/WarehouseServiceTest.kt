package com.example.service

import com.example.humidityMeasurement
import com.example.humiditySensor
import com.example.networking.MeasurementListener
import com.example.temperatureMeasurement
import com.example.temperatureSensor
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class WarehouseServiceTest {
    private val centralMonitoringService = mockk<CentralMonitoringService>(relaxed = true)
    private val sensorDataListener = mockk<MeasurementListener>()

    @Test
    fun `should start servers for all sensors and process measurements`() =
        runTest {
            every { sensorDataListener.streamMeasurements() } returns
                flowOf(
                    temperatureSensor to temperatureMeasurement,
                    humiditySensor to humidityMeasurement,
                )

            val warehouseService =
                WarehouseService(
                    centralMonitoringService,
                    sensorDataListener,
                )

            warehouseService.start()

            coVerify(exactly = 1) {
                centralMonitoringService.processMeasurement(temperatureMeasurement, temperatureSensor)
            }
            coVerify(exactly = 1) {
                centralMonitoringService.processMeasurement(humidityMeasurement, humiditySensor)
            }

            confirmVerified(centralMonitoringService)
        }
}
