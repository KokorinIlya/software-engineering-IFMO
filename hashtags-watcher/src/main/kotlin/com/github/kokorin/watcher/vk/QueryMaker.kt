package com.github.kokorin.watcher.vk

import com.github.kokorin.watcher.config.VkAccessToken
import com.github.kokorin.watcher.config.VkVersion
import java.util.*
import java.util.concurrent.TimeUnit

class QueryMaker(
    private val version: VkVersion,
    private val token: VkAccessToken,
    private val hashTag: String
) {
    private val baseDate = Date()

    fun makeQuery(hour: Int): String {
        val startDate = Date(baseDate.time - TimeUnit.HOURS.toMillis(hour.toLong()))
        val endDate = Date(baseDate.time - TimeUnit.HOURS.toMillis((hour - 1).toLong()))
        assert(startDate.time - endDate.time == TimeUnit.HOURS.toMillis(1L))
        val startTime = TimeUnit.MILLISECONDS.toSeconds(startDate.time)
        val endTime = TimeUnit.MILLISECONDS.toSeconds(endDate.time)
        return "https://api.vk.com/method/newsfeed.search?" +
                "q=%23$hashTag&" +
                "v=${version.version}&" +
                "access_token=${token.accessToken}&" +
                "count=0&" +
                "start_time=$startTime&" +
                "end_time=$endTime"

    }
}
