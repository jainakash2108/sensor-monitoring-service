package com.example

import com.example.logging.LoggerService
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ApplicationIntegrationTest {
    @Test
    fun `should process sequence of network events and shutdown cleanly`() =
        runBlocking {
            val loggerSpy = spyk(LoggerService())

            val appJob =
                launch(Dispatchers.Default) {
                    Application(loggerSpy).run()
                }

            delay(1000)

            val udpPackets =
                listOf(
                    3344 to "sensor_id=t1; value=38",
                    3344 to "sensor_id=t1; value=28",
                    3355 to "sensor_id=h1; value=48",
                    3355 to "sensor_id=h1; value=58",
                )

            withContext(Dispatchers.IO) {
                DatagramSocket().use { socket ->
                    val address = InetAddress.getByName("127.0.0.1")
                    udpPackets.forEach { (port, msg) ->
                        val bytes = msg.toByteArray()
                        socket.send(DatagramPacket(bytes, bytes.size, address, port))
                        delay(50)
                    }
                }
            }

            delay(500)

            coVerify { loggerSpy.logMeasurement(match { it.sensor.port == 3344 && it.measurement.value == 28.0 }) }
            coVerify { loggerSpy.logAlarm(match { it.sensor.port == 3344 && it.measurement.value == 38.0 }) }

            coVerify { loggerSpy.logMeasurement(match { it.sensor.port == 3355 && it.measurement.value == 48.0 }) }
            coVerify { loggerSpy.logAlarm(match { it.sensor.port == 3355 && it.measurement.value == 58.0 }) }

            withContext(Dispatchers.IO) {
                DatagramSocket().use { socket ->
                    val address = InetAddress.getByName("127.0.0.1")
                    val msg = "invalid_message"
                    val bytes = msg.toByteArray()
                    socket.send(DatagramPacket(bytes, bytes.size, address, 3344))
                }
            }
            delay(200)

            appJob.cancelAndJoin()
        }
}
