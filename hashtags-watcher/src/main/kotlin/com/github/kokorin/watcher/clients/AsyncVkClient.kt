package com.github.kokorin.watcher.clients

import com.github.kokorin.watcher.model.VkResponse

interface AsyncVkClient : AutoCloseable {
    suspend fun searchHashTag(hashTag: String, startTime: Long, endTime: Long): VkResponse?
}
