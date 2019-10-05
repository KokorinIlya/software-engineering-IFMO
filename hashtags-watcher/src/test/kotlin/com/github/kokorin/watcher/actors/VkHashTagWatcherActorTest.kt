package com.github.kokorin.watcher.actors

import com.github.kokorin.watcher.clients.vk.AsyncVkClient
import com.github.kokorin.watcher.config.ActorConfig
import com.github.kokorin.watcher.model.HashTagCount
import com.github.kokorin.watcher.model.HashTagResponse
import com.github.kokorin.watcher.model.Response
import com.github.kokorin.watcher.model.VkResponse
import com.github.kokorin.watcher.time.TimeConverter
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.util.*
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit

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
        val curUnixTime = TimeUnit.MILLISECONDS.toSeconds(curDate.time)

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
}
