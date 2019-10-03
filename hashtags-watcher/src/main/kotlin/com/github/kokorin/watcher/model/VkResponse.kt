package com.github.kokorin.watcher.model

import com.beust.klaxon.Json

data class VkResponse(val response: Response)

data class Response (
    @Json(name = "total_count")
    val totalCount: Int
)

data class VkTimedResponse(val hour: Int, val count: Int)
