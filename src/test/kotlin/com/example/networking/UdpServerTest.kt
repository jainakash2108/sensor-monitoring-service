package com.example.networking

import com.example.domain.Sensor
import com.example.domain.SensorType
import com.example.logging.LoggerService
import com.example.temperatureMessage1
import com.example.temperatureMessage2
import com.example.temperatureSensor
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UdpServerTest {
    private val logger = mockk<LoggerService>(relaxed = true)

    @Test
    fun `should emit messages received via UDP and log listening`() =
        runBlocking {
            val udpServer = UdpServer(temperatureSensor, logger)

            val serverJob =
                async(Dispatchers.IO) {
                    udpServer.listen().take(1).toList()
                }

            delay(200)

            withContext(Dispatchers.IO) {
                DatagramSocket().use { socket ->
                    val bytes = temperatureMessage1.toByteArray()
                    val packet =
                        DatagramPacket(
                            bytes,
                            bytes.size,
                            InetAddress.getByName("127.0.0.1"),
                            temperatureSensor.port,
                        )
                    socket.send(packet)
                }
            }

            val results = serverJob.await()

            assertEquals(1, results.size)
            assertEquals(temperatureMessage1, results[0])
            verify { logger.logInfo(temperatureSensor) }
        }

    @Test
    fun `should emit multiple messages`() =
        runBlocking {
            val udpServer = UdpServer(temperatureSensor, logger)
            val messagesToSend =
                listOf(
                    temperatureMessage1,
                    temperatureMessage2,
                )

            val serverJob =
                async(Dispatchers.IO) {
                    udpServer.listen().take(messagesToSend.size).toList()
                }

            delay(200)

            withContext(Dispatchers.IO) {
                DatagramSocket().use { socket ->
                    messagesToSend.forEach { msg ->
                        val bytes = msg.toByteArray()
                        val packet =
                            DatagramPacket(
                                bytes,
                                bytes.size,
                                InetAddress.getByName("127.0.0.1"),
                                temperatureSensor.port,
                            )
                        socket.send(packet)
                        delay(50)
                    }
                }
            }

            val receivedMessages = serverJob.await()
            assertEquals(messagesToSend, receivedMessages)
        }

    @Test
    fun `should log error and close flow when UDP port is already in use`() =
        runBlocking {
            val port = 3388
            val sensor = Sensor("Conflict", port, 50.0, SensorType.Temperature)
            val udpServer = UdpServer(sensor, logger)

            withContext(Dispatchers.IO) {
                DatagramSocket(port).use {
                    assertFailsWith<Exception> {
                        udpServer.listen().toList()
                    }
                }
            }

            verify { logger.logError(match { it.contains("Failed to open socket on port $port") }) }
        }
}
