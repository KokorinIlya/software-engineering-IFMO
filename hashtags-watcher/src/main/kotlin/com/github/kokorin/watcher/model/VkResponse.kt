package com.github.kokorin.watcher.model

import com.beust.klaxon.Json

data class VkResponse(val response: Response)

data class Response (
    @Json(name = "total_count")
    val totalCount: Int
)

sealed class HashTagResponse

data class HashTagCount(val count: Int) : HashTagResponse()

object IncorrectVkAnswer : HashTagResponse()

object NoResponse : HashTagResponse()

data class VkTimedResponse(val hour: Int, val count: HashTagResponse)
