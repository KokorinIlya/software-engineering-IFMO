package com.github.kokorin.watcher.clients.http

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class RPSLimitHttpClient(
    private val client: AsyncHttpClient,
    private val rps: Int,
    private val timeUnit: TimeUnit = TimeUnit.SECONDS
) : AsyncHttpClient {
    override suspend fun get(query: String): String {
        while (true) {
            val executingTasks = curExecutingTasks.get()
            assert(executingTasks <= rps)
            if (executingTasks == rps) {
                delay(500)
                continue
            } else {
                if (curExecutingTasks.compareAndSet(executingTasks, executingTasks + 1)) {
                    val answer = client.get(query)
                    scope.launch {
                        delay(timeUnit.toMillis(1))
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

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun close() {
        client.close()
        scope.coroutineContext.cancelChildren()
    }

    private val curExecutingTasks = AtomicInteger()
}
