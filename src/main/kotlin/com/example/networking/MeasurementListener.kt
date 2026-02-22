package com.example.networking

import com.example.domain.Measurement
import com.example.domain.ParseResult
import com.example.domain.Sensor
import com.example.logging.LoggerService
import com.example.parser.SensorMessageParser
import com.example.repository.SensorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

open class SensorServerProvider {
    open fun createServer(
        sensor: Sensor,
        logger: LoggerService,
    ): UdpServer = UdpServer(sensor, logger)
}

class MeasurementListener(
    private val sensorRepository: SensorRepository,
    private val parser: SensorMessageParser,
    private val logger: LoggerService,
    private val serverProvider: SensorServerProvider = SensorServerProvider(),
) {
    fun streamMeasurements(): Flow<Pair<Sensor, Measurement>> =
        sensorRepository
            .fetchAll()
            .map { sensor ->
                serverProvider
                    .createServer(sensor, logger)
                    .listen()
                    .mapNotNull { message ->
                        when (val result = parser.parse(message, sensor)) {
                            is ParseResult.Success -> {
                                sensor to result.measurement
                            }

                            is ParseResult.Failure -> {
                                logger.logError("Parsing error: $message and reason: ${result.reason}")
                                null
                            }
                        }
                    }
            }.merge()
}
