package com.github.kokorin.watcher.clients

import com.beust.klaxon.Klaxon
import com.github.kokorin.watcher.config.VkConfig
import com.github.kokorin.watcher.model.VkResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.slf4j.LoggerFactory

class AsyncVkClientImpl(private val httpClient: HttpClient, private val vkConfig: VkConfig) : AsyncVkClient {
    override suspend fun searchHashTag(hashTag: String, startTime: Long, endTime: Long): VkResponse? {
        val query = "https://api.vk.com/method/newsfeed.search?" +
                "q=%23$hashTag&" +
                "v=${vkConfig.version.version}&" +
                "access_token=${vkConfig.accessToken.accessToken}&" +
                "count=0&" +
                "start_time=$startTime&" +
                "end_time=$endTime"
        log.info("VK << $query")
        val vkStringResponse = httpClient.get<String>(query)
        log.info("VK >> $vkStringResponse")
        val vkResponse = Klaxon().parse<VkResponse>(vkStringResponse)
        if (vkResponse == null) {
            log.error("Couldn't get total posts count from VK API response string: $vkStringResponse")
        }
        return vkResponse
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun close() {
        try {
            httpClient.close()
            log.info("Http client closed, closing rate limiter")
        } catch (e: Throwable) {
            log.error("Error while closing Http client", e)
            throw e
        }
    }
}
