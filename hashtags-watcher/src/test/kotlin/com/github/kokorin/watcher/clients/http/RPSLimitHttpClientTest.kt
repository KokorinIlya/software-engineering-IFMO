package com.github.kokorin.watcher.clients.http

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.TimeUnit


class RPSLimitHttpClientTest {
    @Test
    fun rpsLimitTest() = runBlocking {
        val iterationsCount = 100
        val coroutinesCount = 10
        val mockedClient = object : AsyncHttpClient {
            val seconds = ConcurrentLinkedDeque<Long>()
            var closeCounts: Int = 0

            override suspend fun get(query: String): String {
                val currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
                delay(100)
                seconds.addFirst(currentTime)
                return "Response"
            }

            override fun close() {
                closeCounts += 1
            }
        }

        val client = RPSLimitHttpClient(mockedClient, 5)
        val jobs = (1..coroutinesCount).map {
            GlobalScope.launch {
                for (j in 1..iterationsCount) {
                    client.get("Request")
                }
            }
        }
        jobs.forEach { it.join() }
        client.close()
        val sortedTimes = mockedClient.seconds.toList().sorted()
        for (i in 0 until sortedTimes.size - 5) {
            assertTrue(sortedTimes[i] < sortedTimes[i + 5])
        }
        assertEquals(mockedClient.closeCounts, 1)
    }
}
