package com.github.kokorin.watcher.actors

import com.github.kokorin.watcher.clients.vk.AsyncVkClient
import com.github.kokorin.watcher.config.ActorConfig
import com.github.kokorin.watcher.model.HashTagResponse
import com.github.kokorin.watcher.model.NoResponse
import com.github.kokorin.watcher.model.VkTimedResponse
import com.github.kokorin.watcher.time.TimeConverter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import java.io.Closeable

class VkHashTagWatcherActor(
    private val client: AsyncVkClient,
    private val timeConverter: TimeConverter,
    private val hashTag: String,
    private val actorConfig: ActorConfig,
    private val childrenConfig: ActorConfig
) : Closeable {

    override fun close() {
        client.close()
    }

    private val channel = Channel<VkTimedResponse>()
    private val log = LoggerFactory.getLogger(this.javaClass)

    suspend fun doRequest(maxHours: Int, coroutineScope: CoroutineScope): Array<HashTagResponse> {
        var responsesReceived = 0
        val answerArray = Array<HashTagResponse>(maxHours) { NoResponse }
        val children = (1..maxHours).map {
            coroutineScope.launch {
                VkSearchActor(channel, client, hashTag, timeConverter, childrenConfig).makeSingleRequest(it)
            }
        }
        try {
            withTimeout(actorConfig.timeout.toMillis()) {
                for (curTimedResponse in channel) {
                    responsesReceived += 1
                    answerArray[curTimedResponse.hour - 1] = curTimedResponse.count
                    if (responsesReceived == maxHours) {
                        channel.close()
                        break
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            log.error("Timeout, only $responsesReceived responses from $maxHours were received")
        } finally {
            children.forEach { it.cancel() }
            children.forEach { it.join() }
        }
        return answerArray
    }
}
