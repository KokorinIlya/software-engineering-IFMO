package com.github.kokorin.watcher.actors

import com.github.kokorin.watcher.clients.vk.AsyncVkClient
import com.github.kokorin.watcher.model.VkTimedResponse
import com.github.kokorin.watcher.time.TimeConverter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.concurrent.TimeUnit

class VkHashTagWatcherActor(
    private val client: AsyncVkClient,
    private val timeConverter: TimeConverter,
    private val hashTag: String
) : Closeable {

    override fun close() {
        client.close()
    }

    private val channel = Channel<VkTimedResponse>()
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        private val totalTimeoutSeconds = TimeUnit.MINUTES.toSeconds(2L)
    }

    suspend fun doRequest(maxHours: Int, coroutineScope: CoroutineScope): IntArray {
        var responsesReceived = 0
        val answerArray = IntArray(maxHours) { -2 }
        val children = (1..maxHours).map {
            coroutineScope.launch {
                VkSearchActor(channel, client, hashTag, timeConverter).makeSingleRequest(it)
            }
        }
        try {
            withTimeout(TimeUnit.SECONDS.toMillis(totalTimeoutSeconds)) {
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
