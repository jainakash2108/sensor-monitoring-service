package com.example.networking

import com.example.domain.ParseResult
import com.example.humidityMeasurement1
import com.example.humidityMeasurement2
import com.example.humidityMessage1
import com.example.humidityMessage2
import com.example.humiditySensor
import com.example.logging.LoggerService
import com.example.parser.SensorMessageParser
import com.example.repository.SensorRepository
import com.example.temperatureMeasurement1
import com.example.temperatureMeasurement2
import com.example.temperatureMessage1
import com.example.temperatureMessage2
import com.example.temperatureSensor
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MeasurementListenerTest {
    @Test
    fun `streamMeasurements should combine multiple sensor flows and parse messages`() =
        runTest {
            val sensorRepository = mockk<SensorRepository>()
            val parser = mockk<SensorMessageParser>()
            val logger = mockk<LoggerService>(relaxed = true)
            val serverProvider = mockk<SensorServerProvider>()

            every { sensorRepository.fetchAll() } returns listOf(temperatureSensor, humiditySensor)

            val udpServer1 = mockk<UdpServer>()
            val udpServer2 = mockk<UdpServer>()
            every { serverProvider.createServer(temperatureSensor, logger) } returns udpServer1
            every { serverProvider.createServer(humiditySensor, logger) } returns udpServer2

            every { udpServer1.listen() } returns flowOf(temperatureMessage1, temperatureMessage2)
            every { udpServer2.listen() } returns flowOf(humidityMessage1, humidityMessage2)

            every { parser.parse(temperatureMessage1, temperatureSensor) } returns
                ParseResult.Success(
                    temperatureMeasurement1,
                )
            every { parser.parse(temperatureMessage2, temperatureSensor) } returns
                ParseResult.Success(
                    temperatureMeasurement2,
                )
            every { parser.parse(humidityMessage1, humiditySensor) } returns ParseResult.Success(humidityMeasurement1)
            every { parser.parse(humidityMessage2, humiditySensor) } returns ParseResult.Success(humidityMeasurement2)

            val listener = MeasurementListener(sensorRepository, parser, logger, serverProvider)
            val results = listener.streamMeasurements().toList()

            assertEquals(4, results.size)
            val expected =
                listOf(
                    temperatureSensor to temperatureMeasurement1,
                    temperatureSensor to temperatureMeasurement2,
                    humiditySensor to humidityMeasurement1,
                    humiditySensor to humidityMeasurement2,
                )
            assertEquals(expected.size, results.size)
            assert(results.containsAll(expected))
        }
}
