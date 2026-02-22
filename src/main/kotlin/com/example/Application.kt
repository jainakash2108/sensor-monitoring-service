package com.example

import com.example.logging.LoggerService
import com.example.networking.MeasurementListener
import com.example.parser.SensorMessageParser
import com.example.repository.SensorRepository
import com.example.service.CentralMonitoringService
import com.example.service.WarehouseService
import kotlinx.coroutines.runBlocking

class Application(
    private val logger: LoggerService = LoggerService(),
    private val sensorRepository: SensorRepository = SensorRepository(),
    private val monitoringService: CentralMonitoringService = CentralMonitoringService(logger),
    private val parser: SensorMessageParser = SensorMessageParser(),
    private val sensorDataListener: MeasurementListener =
        MeasurementListener(
            sensorRepository = sensorRepository,
            parser = parser,
            logger = logger,
        ),
    private val warehouseService: WarehouseService =
        WarehouseService(
            monitoringService = monitoringService,
            sensorDataListener = sensorDataListener,
        ),
) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) =
            runBlocking {
                Application().run()
            }
    }

    suspend fun run() {
        logger.logInfo("Sensor Monitoring Service Started")
        warehouseService.start()
    }
}
