package com.example.service

import com.example.domain.Measurement
import com.example.domain.SensorType
import com.example.humiditySensor
import com.example.logging.LoggerService
import com.example.temperatureSensor
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CentralMonitoringServiceTest {
    private lateinit var logger: LoggerService
    private lateinit var service: CentralMonitoringService

    @BeforeEach
    fun setup() {
        logger = mockk(relaxed = true)
        service = CentralMonitoringService(logger)
    }

    @Test
    fun `should call logAlarm when temperature exceeds threshold`() =
        runTest {
            val measurement = Measurement("t1", 40.0, SensorType.Temperature)

            service.processMeasurement(measurement, temperatureSensor)

            coVerify {
                logger.logAlarm(
                    match {
                        it.sensor.name == "Temperature" &&
                            it.measurement.sensorId == "t1" &&
                            it.measurement.value == 40.0
                    },
                )
            }
        }

    @Test
    fun `should call logMeasurement when temperature is below threshold`() =
        runTest {
            val measurement = Measurement("t2", 30.0, SensorType.Temperature)

            service.processMeasurement(measurement, temperatureSensor)

            coVerify {
                logger.logMeasurement(
                    match {
                        it.sensor.name == "Temperature" &&
                            it.measurement.sensorId == "t2" &&
                            it.measurement.value == 30.0
                    },
                )
            }
        }

    @Test
    fun `should call logAlarm when humidity exceeds threshold`() =
        runTest {
            val measurement = Measurement("h1", 60.0, SensorType.Humidity)
            service.processMeasurement(measurement, humiditySensor)

            coVerify {
                logger.logAlarm(
                    match {
                        it.sensor.name == "Humidity" &&
                            it.measurement.sensorId == "h1" &&
                            it.measurement.value == 60.0
                    },
                )
            }
        }

    @Test
    fun `should call logMeasurement when humidity is below threshold`() =
        runTest {
            val measurement = Measurement("h2", 45.0, SensorType.Humidity)

            service.processMeasurement(measurement, humiditySensor)

            coVerify {
                logger.logMeasurement(
                    match {
                        it.sensor.name == "Humidity" &&
                            it.measurement.sensorId == "h2" &&
                            it.measurement.value == 45.0
                    },
                )
            }
        }
}
