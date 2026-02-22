package com.example.logging

import com.example.domain.MeasurementReport
import com.example.domain.Sensor
import java.time.Instant

class LoggerService {
    private fun timestamp() = Instant.now()

    fun logInfo(message: String) {
        println("[${timestamp()}] [INFO] [WAREHOUSE] $message")
    }

    fun logInfo(sensor: Sensor) {
        println(
            "[${timestamp()}] [INFO] [UDP] " +
                "sensor=${sensor.name} " +
                "port=${sensor.port} " +
                "threshold=${sensor.threshold}${sensor.type.unit} " +
                "status=LISTENING",
        )
    }

    fun logMeasurement(report: MeasurementReport) {
        println(
            "[${timestamp()}] [INFO] [MONITORING] " +
                "sensor=${report.sensor.name} " +
                "id=${report.measurement.sensorId} " +
                "port=${report.sensor.port} " +
                "value=${report.measurement.value}${report.sensor.type.unit} " +
                "threshold=${report.sensor.threshold}${report.sensor.type.unit} " +
                "status=OK",
        )
    }

    fun logAlarm(report: MeasurementReport) {
        System.err.println(
            "[${timestamp()}] [ALARM] [MONITORING] " +
                "sensor=${report.sensor.name} " +
                "id=${report.measurement.sensorId} " +
                "port=${report.sensor.port} " +
                "value=${report.measurement.value}${report.sensor.type.unit} " +
                "threshold=${report.sensor.threshold}${report.sensor.type.unit} " +
                "status=THRESHOLD_EXCEEDED",
        )
    }

    fun logError(message: String) {
        System.err.println("[${timestamp()}] [ERROR] $message")
    }
}
