package com.github.kokorin.watcher.clients.vk

import com.beust.klaxon.Klaxon
import com.github.kokorin.watcher.clients.http.AsyncHttpClient
import com.github.kokorin.watcher.config.VkConfig
import com.github.kokorin.watcher.model.VkResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.slf4j.LoggerFactory

class AsyncVkClientImpl(private val httpClient: AsyncHttpClient, private val vkConfig: VkConfig) : AsyncVkClient {
    override suspend fun searchHashTag(hashTag: String, startTime: Long, endTime: Long): VkResponse? {
        val query = "${vkConfig.schema}://${vkConfig.host}:${vkConfig.port}/method/newsfeed.search?" +
                "q=%23$hashTag&" +
                "v=${vkConfig.version.major}.${vkConfig.version.minor}&" +
                "access_token=${vkConfig.accessToken}&" +
                "count=0&" +
                "start_time=$startTime&" +
                "end_time=$endTime"
        log.info("VK << $query")
        val vkStringResponse = httpClient.get(query)
        log.info("VK >> $vkStringResponse")
        val vkResponse = Klaxon().parse<VkResponse>(vkStringResponse)
        if (vkResponse == null) {
            log.error("Couldn't get total posts count from VK API response string: $vkStringResponse")
        }
        return vkResponse
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun close() {
        httpClient.close()
    }
}
