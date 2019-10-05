package com.github.kokorin.watcher.actors

import com.github.kokorin.watcher.clients.vk.AsyncVkClient
import com.github.kokorin.watcher.config.ActorConfig
import com.github.kokorin.watcher.model.HashTagCount
import com.github.kokorin.watcher.model.IncorrectVkAnswer
import com.github.kokorin.watcher.model.NoResponse
import com.github.kokorin.watcher.model.VkTimedResponse
import com.github.kokorin.watcher.time.TimeConverter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class VkSearchActor(
    private val parentMailbox: Channel<VkTimedResponse>,
    private val client: AsyncVkClient,
    private val hashTag: String,
    private val timeConverter: TimeConverter,
    private val actorConfig: ActorConfig
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    suspend fun makeSingleRequest(hours: Int) {
        val totalCount = try {
            val requestStartTime = System.currentTimeMillis()
            withTimeout(actorConfig.timeout.toMillis()) {
                val (startTime, endTime) = timeConverter.getStartAndEndTime(hours)
                val vkResponse = client.searchHashTag(hashTag, startTime, endTime)
                val requestEndTime = System.currentTimeMillis()
                log.info(
                    "Response from VK received," +
                            " ${TimeUnit.MILLISECONDS.toSeconds(requestEndTime - requestStartTime)}" +
                            " seconds passed"
                )
                vkResponse?.response?.totalCount?.let { HashTagCount(it) } ?: IncorrectVkAnswer
            }
        } catch (e: TimeoutCancellationException) {
            log.error("VK API didnt't produce answer in ${actorConfig.timeout.toMillis()} seconds")
            NoResponse
        } catch (e: CancellationException) {
            log.error("VK API hash't produced answer, job was cancelled")
            NoResponse
        } catch (e: Throwable) {
            log.error("Some error occurred while executing request to VK API", e)
            IncorrectVkAnswer
        }
        parentMailbox.send(VkTimedResponse(hours, totalCount))
        return
    }
}
