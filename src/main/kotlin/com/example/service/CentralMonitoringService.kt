package com.example.service

import com.example.domain.Measurement
import com.example.domain.MeasurementReport
import com.example.domain.Sensor
import com.example.logging.LoggerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CentralMonitoringService(
    private val logger: LoggerService,
) {
    suspend fun processMeasurement(
        measurement: Measurement,
        sensor: Sensor,
    ) = withContext(Dispatchers.IO) {
        val report =
            MeasurementReport(
                measurement = measurement,
                sensor = sensor,
            )

        if (measurement.value > sensor.threshold) {
            logger.logAlarm(report)
        } else {
            logger.logMeasurement(report)
        }
    }
}
