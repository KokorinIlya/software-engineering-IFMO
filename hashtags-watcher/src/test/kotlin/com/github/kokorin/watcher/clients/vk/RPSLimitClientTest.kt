package com.github.kokorin.watcher.clients.vk

import com.github.kokorin.watcher.model.VkResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.TimeUnit

class RPSLimitClientTest {
    private class MockVkClient : AsyncVkClient {
        val seconds = ConcurrentLinkedDeque<Long>()
        override fun close() {

        }

        override suspend fun searchHashTag(hashTag: String, startTime: Long, endTime: Long): VkResponse? {
            val currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
            delay(100)
            seconds.addFirst(currentTime)
            return null
        }

    }

    @Test
    fun rpsLimitTest() = runBlocking {
        val iterationsCount = 100
        val coroutinesCount = 10
        val mockedClient = MockVkClient()
        val client = RPSLimitVkClient(mockedClient, 5)
        val jobs = (1..coroutinesCount).map {
            GlobalScope.launch {
                for (j in 1..iterationsCount) {
                    client.searchHashTag("", 0, 0)
                }
            }
        }
        jobs.forEach { it.join() }
        client.close()
        val sortedTimes = mockedClient.seconds.toList().sorted()
        for (i in 0 until sortedTimes.size - 5) {
            assertTrue(sortedTimes[i] < sortedTimes[i + 5])
        }
    }
}
