package com.github.kokorin.watcher.clients.vk

import com.github.kokorin.watcher.clients.http.AsyncHttpClient
import com.github.kokorin.watcher.config.VkConfigImpl
import com.github.kokorin.watcher.model.Response
import com.github.kokorin.watcher.model.VkResponse
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import java.io.File
import io.mockk.*

class AsyncVkClientImplTest {
    @Test
    fun testRequest() = runBlocking {
        val httpClient = mockk<AsyncHttpClient>()
        val vkConfig = VkConfigImpl(
            ConfigFactory.parseFile(File("src/test/resources/application.testing.conf")).getConfig("vk")
        )
        val vkClient = AsyncVkClientImpl(httpClient, vkConfig)
        val hashtag = "вконтакте"
        val startTime = 12345L
        val endTime = 12355L
        val expectedString = "${vkConfig.schema}://${vkConfig.host}:${vkConfig.port}/method/newsfeed.search?" +
                "q=%23$hashtag&" +
                "v=${vkConfig.version.major}.${vkConfig.version.minor}&" +
                "access_token=${vkConfig.accessToken}&" +
                "count=0&" +
                "start_time=$startTime&" +
                "end_time=$endTime"
        val response = "{\"response\":{\"items\":[],\"count\":2,\"total_count\":2}}"
        coEvery { httpClient.get(expectedString) } returns response
        assertEquals(vkClient.searchHashTag(hashtag, startTime, endTime), VkResponse(Response(2)))
    }
}
