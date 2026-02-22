package com.example.parser

import com.example.domain.Measurement
import com.example.domain.ParseResult
import com.example.domain.SensorType
import com.example.humiditySensor
import com.example.logging.LoggerService
import com.example.temperatureSensor
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SensorMessageParserTest {
    private lateinit var logger: LoggerService
    private lateinit var parser: SensorMessageParser

    @BeforeEach
    fun setup() {
        logger = mockk(relaxed = true)
        parser = SensorMessageParser()
    }

    @Test
    fun `should parse valid temperature message`() =
        runTest {
            val message = "sensor_id=t1; value=36.5"
            val result = parser.parse(message, temperatureSensor)

            assertEquals(ParseResult.Success(Measurement("t1", 36.5, SensorType.Temperature)), result)
        }

    @Test
    fun `should parse valid message with multiple delimiters`() =
        runTest {
            val message = ";;;sensor_id=t1;;;value=36.5;;;"
            val result = parser.parse(message, temperatureSensor)

            assertEquals(ParseResult.Success(Measurement("t1", 36.5, SensorType.Temperature)), result)
        }

    @Test
    fun `should parse valid message with extra spaces and case variation`() =
        runTest {
            val message = " VALUE = 36.5 ; SENSOR_ID = t1 "
            val result = parser.parse(message, temperatureSensor)

            assertEquals(ParseResult.Success(Measurement("t1", 36.5, SensorType.Temperature)), result)
        }

    @Test
    fun `should parse valid humidity message`() =
        runTest {
            val message = "sensor_id=h1; value=48.0"
            val result = parser.parse(message, humiditySensor)

            assertEquals(ParseResult.Success(Measurement("h1", 48.0, SensorType.Humidity)), result)
        }

    @Test
    fun `should return failure when sensor_id missing`() =
        runTest {
            val message = "value=36.5"
            val result = parser.parse(message, temperatureSensor)

            assertTrue(result is ParseResult.Failure)
            assertEquals("Message does not contain sensor_id", (result as ParseResult.Failure).reason)
        }

    @Test
    fun `should return failure when value missing`() =
        runTest {
            val message = "sensor_id=t1"
            val result = parser.parse(message, temperatureSensor)

            assertTrue(result is ParseResult.Failure)
            assertEquals("Message does not contain sensor's value", (result as ParseResult.Failure).reason)
        }

    @Test
    fun `should return failure on malformed message`() =
        runTest {
            val message = "completely invalid message"
            val result = parser.parse(message, temperatureSensor)

            assertTrue(result is ParseResult.Failure)
            assertEquals("Invalid format: completely invalid message", (result as ParseResult.Failure).reason)
        }

    @Test
    fun `should return failure when value is not a number`() =
        runTest {
            val message = "sensor_id=t1; value=not-a-number"
            val result = parser.parse(message, temperatureSensor)

            assertTrue(result is ParseResult.Failure)
            assertTrue((result as ParseResult.Failure).reason.contains("For input string"))
        }
}
