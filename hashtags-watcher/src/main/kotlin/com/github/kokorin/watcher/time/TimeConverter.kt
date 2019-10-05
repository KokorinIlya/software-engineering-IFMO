package com.github.kokorin.watcher.time

import java.util.*
import java.util.concurrent.TimeUnit

class TimeConverter(private val baseTime: Date) {
    /**
     * Returns time in UNIX format (in seconds) for two moments:
     * for moment @code{hour} hours before @code{baseTime}, and @code{hour - 1} hours before.
     */
    fun getStartAndEndTime(hour: Int): Pair<Long, Long> {
        require(hour >= 1) { throw IllegalArgumentException("Hour must be positive") }
        val startDate = Date(baseTime.time - TimeUnit.HOURS.toMillis(hour.toLong()))
        val endDate = Date(baseTime.time - TimeUnit.HOURS.toMillis((hour - 1).toLong()))
        assert(endDate.time - startDate.time == TimeUnit.HOURS.toMillis(1L))
        val startTime = TimeUnit.MILLISECONDS.toSeconds(startDate.time)
        val endTime = TimeUnit.MILLISECONDS.toSeconds(endDate.time)
        return Pair(startTime, endTime)
    }
}
