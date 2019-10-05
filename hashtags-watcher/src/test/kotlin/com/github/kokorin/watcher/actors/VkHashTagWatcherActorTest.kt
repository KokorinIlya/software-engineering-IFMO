package com.github.kokorin.watcher.actors

import com.github.kokorin.watcher.clients.vk.AsyncVkClient
import com.github.kokorin.watcher.config.ActorConfig
import com.github.kokorin.watcher.model.*
import com.github.kokorin.watcher.time.TimeConverter
import com.github.kokorin.watcher.utils.toSeconds
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.util.*
import org.junit.Assert.*
import org.junit.Test

class VkHashTagWatcherActorTest {
    @Test
    fun testReceive() = runBlocking {
        val client = mockk<AsyncVkClient>()
        val curDate = Date()
        val timeConverter = TimeConverter(curDate)
        val hashtag = "вконтакте"

        val actorConfig = object : ActorConfig {
            override val timeout: Duration
                get() = Duration.ofMinutes(2)
        }

        val childrenActorConfig = object : ActorConfig {
            override val timeout: Duration
                get() = Duration.ofMinutes(1)
        }
        val curUnixTime = Duration.ofMillis(curDate.time).toSeconds()

        val watcherActor = VkHashTagWatcherActor(client, timeConverter, hashtag, actorConfig, childrenActorConfig)
        val maxHours = 4
        val hours = (1..maxHours)
        hours.map { timeConverter.getStartAndEndTime(it) }.forEach { (startTime, endTime) ->
            coEvery { client.searchHashTag(hashtag, startTime, endTime) } returns VkResponse(
                Response(
                    Duration.ofSeconds(curUnixTime - startTime).toHours().toInt()
                )
            )
        }
        val response = watcherActor.doRequest(maxHours, GlobalScope)
        assertTrue(
            response.toList() == hours.toList().map { HashTagCount(it) }
        )
    }

    @Test
    fun testSearchTimeout() = runBlocking {
        val client = mockk<AsyncVkClient>()
        val curDate = Date()
        val timeConverter = TimeConverter(curDate)
        val hashtag = "вконтакте"

        val actorConfig = object : ActorConfig {
            override val timeout: Duration
                get() = Duration.ofMinutes(2)
        }

        val childrenActorConfig = object : ActorConfig {
            override val timeout: Duration
                get() = Duration.ofSeconds(1)
        }

        val watcherActor = VkHashTagWatcherActor(client, timeConverter, hashtag, actorConfig, childrenActorConfig)
        val maxHours = 4
        val hours = (1..maxHours)
        hours.map { timeConverter.getStartAndEndTime(it) }.forEach { (startTime, endTime) ->
            coEvery { client.searchHashTag(hashtag, startTime, endTime) } coAnswers {
                delay(Duration.ofSeconds(2).toMillis())
                VkResponse(Response(1))
            }
        }
        val response = watcherActor.doRequest(maxHours, GlobalScope)
        assertTrue(response.toList() == hours.toList().map { NoResponse })
    }

    @Test
    fun testPartialSearchTimeout() = runBlocking {
        val client = mockk<AsyncVkClient>()
        val curDate = Date()
        val timeConverter = TimeConverter(curDate)
        val hashtag = "вконтакте"

        val actorConfig = object : ActorConfig {
            override val timeout: Duration
                get() = Duration.ofMinutes(2)
        }

        val childrenActorConfig = object : ActorConfig {
            override val timeout: Duration
                get() = Duration.ofSeconds(1)
        }
        val curUnixTime = Duration.ofMillis(curDate.time).toSeconds()

        val watcherActor = VkHashTagWatcherActor(client, timeConverter, hashtag, actorConfig, childrenActorConfig)
        val maxHours = 4
        val hours = (1..maxHours)
        hours.map { timeConverter.getStartAndEndTime(it) }.forEach { (startTime, endTime) ->
            coEvery { client.searchHashTag(hashtag, startTime, endTime) } coAnswers {
                val curHours = Duration.ofSeconds(curUnixTime - startTime).toHours().toInt()
                if (curHours > 2) {
                    delay(Duration.ofSeconds(2).toMillis())
                }
                VkResponse(Response(curHours))
            }
        }
        val response = watcherActor.doRequest(maxHours, GlobalScope)
        assertTrue(
            response.toList() == listOf(HashTagCount(1), HashTagCount(2), NoResponse, NoResponse)
        )
    }

    @Test
    fun testTotalTimeout() = runBlocking {
        val client = mockk<AsyncVkClient>()
        val curDate = Date()
        val timeConverter = TimeConverter(curDate)
        val hashtag = "вконтакте"

        val actorConfig = object : ActorConfig {
            override val timeout: Duration
                get() = Duration.ofSeconds(2)
        }

        val childrenActorConfig = object : ActorConfig {
            override val timeout: Duration
                get() = Duration.ofMinutes(10)
        }

        val watcherActor = VkHashTagWatcherActor(client, timeConverter, hashtag, actorConfig, childrenActorConfig)
        val maxHours = 4
        val hours = (1..maxHours)
        hours.map { timeConverter.getStartAndEndTime(it) }.forEach { (startTime, endTime) ->
            coEvery { client.searchHashTag(hashtag, startTime, endTime) } coAnswers {
                delay(Duration.ofSeconds(3).toMillis())
                VkResponse(Response(1))
            }
        }
        val response = watcherActor.doRequest(maxHours, GlobalScope)
        assertTrue(response.toList() == hours.toList().map { NoResponse })
    }

    @Test
    fun testPartialTotalTimeout() = runBlocking {
        val client = mockk<AsyncVkClient>()
        val curDate = Date()
        val timeConverter = TimeConverter(curDate)
        val hashtag = "вконтакте"

        val actorConfig = object : ActorConfig {
            override val timeout: Duration
                get() = Duration.ofSeconds(2)
        }

        val childrenActorConfig = object : ActorConfig {
            override val timeout: Duration
                get() = Duration.ofMinutes(10)
        }
        val curUnixTime = Duration.ofMillis(curDate.time).toSeconds()

        val watcherActor = VkHashTagWatcherActor(client, timeConverter, hashtag, actorConfig, childrenActorConfig)
        val maxHours = 4
        val hours = (1..maxHours)
        hours.map { timeConverter.getStartAndEndTime(it) }.forEach { (startTime, endTime) ->
            coEvery { client.searchHashTag(hashtag, startTime, endTime) } coAnswers {
                val curHours = Duration.ofSeconds(curUnixTime - startTime).toHours().toInt()
                if (curHours > 2) {
                    delay(Duration.ofSeconds(2).toMillis())
                }
                VkResponse(Response(curHours))
            }
        }
        val response = watcherActor.doRequest(maxHours, GlobalScope)
        assertTrue(
            response.toList() == listOf(HashTagCount(1), HashTagCount(2), NoResponse, NoResponse)
        )
    }
}
