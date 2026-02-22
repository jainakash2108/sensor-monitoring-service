package com.example.service

import com.example.networking.MeasurementListener

class WarehouseService(
    private val monitoringService: CentralMonitoringService,
    private val sensorDataListener: MeasurementListener,
) {
    suspend fun start() {
        sensorDataListener
            .streamMeasurements()
            .collect { (sensor, measurement) ->
                monitoringService.processMeasurement(measurement, sensor)
            }
    }
}
