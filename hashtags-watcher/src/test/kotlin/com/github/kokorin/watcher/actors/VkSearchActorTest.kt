package com.github.kokorin.watcher.actors

import com.github.kokorin.watcher.clients.vk.AsyncVkClient
import com.github.kokorin.watcher.config.ActorConfig
import com.github.kokorin.watcher.model.*
import com.github.kokorin.watcher.time.TimeConverter
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import java.time.Duration
import java.util.*

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
        coEvery { parentMailbox.send(VkTimedResponse(5, HashTagCount(25))) } answers {
            sended += 1
        }

        val actorConfig = object : ActorConfig {
            override val timeout: Duration
                get() = Duration.ofMinutes(1L)
        }

        val searchActor = VkSearchActor(parentMailbox, vkClient, hashTag, timeConverter, actorConfig)
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
            delay(Duration.ofSeconds(2).toMillis())
            VkResponse(Response(25))
        }
        coEvery { parentMailbox.send(VkTimedResponse(5, NoResponse)) } answers {
            sended += 1
        }

        val actorConfig = object : ActorConfig {
            override val timeout: Duration
                get() = Duration.ofSeconds(1L)
        }

        val searchActor = VkSearchActor(parentMailbox, vkClient, hashTag, timeConverter, actorConfig)
        searchActor.makeSingleRequest(hour)
        assertEquals(sended, 1)
    }
}
