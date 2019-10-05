package com.github.kokorin.watcher.actors

import com.github.kokorin.watcher.clients.vk.AsyncVkClient
import com.github.kokorin.watcher.model.Response
import com.github.kokorin.watcher.model.VkResponse
import com.github.kokorin.watcher.model.VkTimedResponse
import com.github.kokorin.watcher.time.TimeConverter
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import java.util.*
import java.util.concurrent.TimeUnit

class VkSearchActorTest {
    @Test
    fun testRequest() = runBlocking {
        val timeConverter = TimeConverter(Date())
        val hour = 5
        val (startTime, endTime) = timeConverter.getStartAndEndTime(5)
        val vkClient = mockk<AsyncVkClient>()
        val hashTag = "вконтакте"
        val parentMailbox = mockk<Channel<VkTimedResponse>>()

        var sended = 0
        coEvery { vkClient.searchHashTag(hashTag, startTime, endTime) } returns VkResponse(Response(25))
        coEvery { parentMailbox.send(VkTimedResponse(5, 25)) } answers {
            sended += 1
        }

        val searchActor = VkSearchActor(parentMailbox, vkClient, hashTag, timeConverter)
        searchActor.makeSingleRequest(hour)
        assertEquals(sended, 1)
    }

    @Test
    fun testTimeout() = runBlocking {
        val timeConverter = TimeConverter(Date())
        val hour = 5
        val (startTime, endTime) = timeConverter.getStartAndEndTime(5)
        val vkClient = mockk<AsyncVkClient>()
        val hashTag = "вконтакте"
        val parentMailbox = mockk<Channel<VkTimedResponse>>()

        var sended = 0
        coEvery { vkClient.searchHashTag(hashTag, startTime, endTime) } coAnswers  {
            delay(TimeUnit.MINUTES.toMillis(2))
            VkResponse(Response(25))
        }
        coEvery { parentMailbox.send(VkTimedResponse(5, -1)) } answers {
            sended += 1
        }

        val searchActor = VkSearchActor(parentMailbox, vkClient, hashTag, timeConverter)
        searchActor.makeSingleRequest(hour)
        assertEquals(sended, 1)
    }
}
