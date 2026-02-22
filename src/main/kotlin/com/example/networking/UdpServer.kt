package com.example.networking

import com.example.domain.Sensor
import com.example.logging.LoggerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException

class UdpServer(
    private val sensor: Sensor,
    private val logger: LoggerService,
) {
    fun listen(): Flow<String> =
        callbackFlow {
            val socket = createDatagramSocket() ?: return@callbackFlow

            val job =
                launch(Dispatchers.IO) {
                    socket.use {
                        receivePackets(it)
                    }
                }
            awaitClose {
                socket.close()
                job.cancel()
            }
        }.flowOn(Dispatchers.IO)

    private fun ProducerScope<String>.createDatagramSocket(): DatagramSocket? =
        try {
            DatagramSocket(sensor.port).also {
                logger.logInfo(sensor)
            }
        } catch (e: SocketException) {
            logger.logError("Failed to open socket on port ${sensor.port}: ${e.message}")
            close(e)
            null
        }

    private fun ProducerScope<String>.receivePackets(socket: DatagramSocket) {
        val buffer = ByteArray(1024)
        while (isActive) {
            val packet = DatagramPacket(buffer, buffer.size)
            try {
                socket.receive(packet)
                val message = String(packet.data, 0, packet.length)
                trySend(message)
            } catch (e: SocketException) {
                if (isActive) {
                    logger.logError("Socket error on port ${sensor.port}: ${e.message}")
                }
                break
            }
        }
    }
}
