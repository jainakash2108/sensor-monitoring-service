package com.example.logging

import com.example.domain.MeasurementReport
import com.example.humidityMeasurement
import com.example.humiditySensor
import com.example.temperatureMeasurement
import com.example.temperatureSensor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertTrue

class LoggerServiceTest {
    private lateinit var outContent: ByteArrayOutputStream
    private lateinit var errContent: ByteArrayOutputStream
    private lateinit var logger: LoggerService

    @BeforeEach
    fun setUp() {
        outContent = ByteArrayOutputStream()
        errContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))

        logger = LoggerService()
    }

    @AfterEach
    fun tearDown() {
        System.setOut(System.out)
        System.setErr(System.err)
    }

    @Test
    fun `logStartup should print info message to stdout`() {
        val message = "System started"
        logger.logInfo(message)

        val output = outContent.toString()
        assertTrue(output.contains("[INFO] [WAREHOUSE]"))
        assertTrue(output.contains(message))
    }

    @Test
    fun `logMeasurement should print info message to stdout`() {
        val report =
            MeasurementReport(
                measurement = temperatureMeasurement,
                sensor = temperatureSensor,
            )

        logger.logMeasurement(report)

        val output = outContent.toString()
        assertTrue(output.contains("[INFO] [MONITORING]"))
        assertTrue(output.contains("sensor=Temperature"))
        assertTrue(output.contains("id=t1"))
        assertTrue(output.contains("value=36.5${temperatureSensor.type.unit}"))
        assertTrue(output.contains("status=OK"))
    }

    @Test
    fun `logAlarm should print alarm message to stderr`() {
        val report =
            MeasurementReport(
                measurement = humidityMeasurement,
                sensor = humiditySensor,
            )

        logger.logAlarm(report)

        val output = errContent.toString()
        assertTrue(output.contains("[ALARM] [MONITORING]"))
        assertTrue(output.contains("sensor=Humidity"))
        assertTrue(output.contains("id=h1"))
        assertTrue(output.contains("value=55.0${humiditySensor.type.unit}"))
        assertTrue(output.contains("status=THRESHOLD_EXCEEDED"))
    }

    @Test
    fun `logError should print error message to stderr`() {
        val errorMessage = "Something went wrong"
        logger.logError(errorMessage)

        val output = errContent.toString()
        assertTrue(output.contains("[ERROR]"))
        assertTrue(output.contains(errorMessage))
    }
}
