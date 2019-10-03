package com.github.kokorin.watcher.actors

import com.github.kokorin.watcher.clients.AsyncVkClient
import com.github.kokorin.watcher.model.VkTimedResponse
import com.github.kokorin.watcher.time.TimeConverter
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class VkSearchActor(
    private val parentMailbox: Channel<VkTimedResponse>,
    private val client: AsyncVkClient,
    private val hashTag: String,
    private val timeConverter: TimeConverter
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        private val vkTimeoutSeconds = TimeUnit.MINUTES.toSeconds(1L)
    }

    suspend fun makeSingleRequest(hours: Int) {
        val totalCount = try {
            val requestStartTime = System.currentTimeMillis()
            withTimeout(TimeUnit.SECONDS.toMillis(vkTimeoutSeconds)) {
                val (startTime, endTime) = timeConverter.getStartAndEndTime(hours)
                val vkResponse = client.searchHashTag(hashTag, startTime, endTime)
                val requestEndTime = System.currentTimeMillis()
                log.info(
                    "Response from VK received," +
                            " ${TimeUnit.MILLISECONDS.toSeconds(requestEndTime - requestStartTime)}" +
                            " seconds passed"
                )
                vkResponse?.response?.totalCount
            }
        } catch (e: TimeoutCancellationException) {
            log.error("VK API didnt't produce answer in $vkTimeoutSeconds seconds")
            null
        } catch (e: Throwable) {
            log.error("Some error occurred while executing request to VK API", e)
            null
        }
        if (totalCount != null) {
            parentMailbox.send(VkTimedResponse(hours, totalCount))
        } else {
            parentMailbox.send(VkTimedResponse(hours, -1))
        }
        return
    }
}
