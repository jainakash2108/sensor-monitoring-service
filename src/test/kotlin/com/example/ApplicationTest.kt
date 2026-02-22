package com.example

import com.example.logging.LoggerService
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class ApplicationTest {
    @Test
    fun `run should log startup message`() =
        runTest {
            val logger = mockk<LoggerService>(relaxed = true)
            val app = Application(logger = logger)

            val job =
                launch(Dispatchers.Default) {
                    app.run()
                }

            delay(200)

            verify { logger.logInfo(match<String> { it.contains("Sensor Monitoring Service Started") }) }

            job.cancelAndJoin()
        }
}
