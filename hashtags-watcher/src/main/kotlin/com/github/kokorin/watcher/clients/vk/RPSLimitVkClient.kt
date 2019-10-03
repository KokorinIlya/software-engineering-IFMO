package com.github.kokorin.watcher.clients.vk

import com.github.kokorin.watcher.model.VkResponse
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class RPSLimitVkClient(private val client: AsyncVkClient, private val rps: Int) :
    AsyncVkClient {
    private val scope = CoroutineScope(Dispatchers.Default)

    override suspend fun searchHashTag(
        hashTag: String,
        startTime: Long,
        endTime: Long
    ): VkResponse? {
        while (true) {
            val executingTasks = curExecutingTasks.get()
            assert(executingTasks <= rps)
            if (executingTasks == rps) {
                delay(500)
                continue
            } else {
                if (curExecutingTasks.compareAndSet(executingTasks, executingTasks + 1)) {
                    val answer = client.searchHashTag(hashTag, startTime, endTime)
                    scope.launch {
                        delay(TimeUnit.SECONDS.toMillis(1))
                        curExecutingTasks.getAndDecrement()
                    }
                    return answer
                } else {
                    yield()
                    continue
                }
            }
        }
    }

    override fun close() {
        client.close()
        scope.coroutineContext.cancelChildren()
    }

    private val curExecutingTasks = AtomicInteger()
}
