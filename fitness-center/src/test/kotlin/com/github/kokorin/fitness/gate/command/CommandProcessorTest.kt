package com.github.kokorin.fitness.gate.command

import com.github.kokorin.fitness.gate.http.StatsHttpClient
import com.github.kokorin.fitness.gate.http.StatsHttpClientsProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime
import org.junit.Test
import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.Exception

class CommandProcessorTest {
    @Test
    fun correctExitTest() = runBlocking {
        val exitTime = LocalDateTime.parse("1862-04-14T20:00:00")
        val enterTime = LocalDateTime.parse("1862-04-13T20:00:00")
        val commandDao = mockk<CommandDao>()
        coEvery {
            commandDao.processExit(1, exitTime)
        }.returns(enterTime)

        val countDownLatch = CountDownLatch(1)
        val statsAccessCounter = AtomicInteger(0)
        val clientsProvider = mockk<StatsHttpClientsProvider>()
        every {
            clientsProvider.getClient()
        }.answers {
            val client = mockk<StatsHttpClient>()
            coEvery {
                client.exitCommand(1, enterTime, exitTime)
            }.answers {
                statsAccessCounter.incrementAndGet()
                countDownLatch.countDown()
                "OK"
            }
            client
        }
        val commandProcessor = CommandProcessor(commandDao, clientsProvider)
        commandProcessor.process(ExitCommand(1, exitTime))
        countDownLatch.await(100, TimeUnit.MILLISECONDS)
        assertEquals(statsAccessCounter.get(), 1)
    }

    @Test
    fun statsUnavailableTest() = runBlocking {
        val exitTime = LocalDateTime.parse("1862-04-14T20:00:00")
        val enterTime = LocalDateTime.parse("1862-04-13T20:00:00")
        val commandDao = mockk<CommandDao>()
        coEvery {
            commandDao.processExit(1, exitTime)
        }.returns(enterTime)

        val countDownLatch = CountDownLatch(1)
        val statsAccessCounter = AtomicInteger(0)
        val clientsProvider = mockk<StatsHttpClientsProvider>()
        every {
            clientsProvider.getClient()
        }.answers {
            val client = mockk<StatsHttpClient>()
            coEvery {
                client.exitCommand(1, enterTime, exitTime)
            }.answers {
                countDownLatch.countDown()
                throw Exception("Connection refused")
            }
            client
        }
        val commandProcessor = CommandProcessor(commandDao, clientsProvider)
        commandProcessor.process(ExitCommand(1, exitTime))
        countDownLatch.await(100, TimeUnit.MILLISECONDS)
        assertEquals(statsAccessCounter.get(), 0)
    }

    @Test
    fun incorrectExit() = runBlocking {
        val exitTime = LocalDateTime.parse("1862-04-14T20:00:00")
        val commandDao = mockk<CommandDao>()
        coEvery {
            commandDao.processExit(1, exitTime)
        }.throws(Exception("Error exiting"))
        val clientsProvider = mockk<StatsHttpClientsProvider>()

        val commandProcessor = CommandProcessor(commandDao, clientsProvider)
        val res = commandProcessor.process(ExitCommand(1, exitTime))
        val expectedRes = "Error executing ExitCommand(uid=1, exitTimestamp=1862-04-14T20:00:00.000): Error exiting"
        assertEquals(res, expectedRes)
    }
}
