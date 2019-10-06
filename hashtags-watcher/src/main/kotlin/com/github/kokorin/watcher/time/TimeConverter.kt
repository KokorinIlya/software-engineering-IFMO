package com.github.kokorin.watcher.time

import com.github.kokorin.watcher.utils.toSeconds
import java.time.Duration
import java.util.*

class TimeConverter(private val baseTime: Date) {
    /**
     * Returns time in UNIX format (in seconds) for two moments:
     * for moment @code{hour} hours before @code{baseTime}, and @code{hour - 1} hours before.
     */
    fun getStartAndEndTime(hour: Int): Pair<Long, Long> {
        require(hour >= 1) { throw IllegalArgumentException("Hour must be positive") }
        val startDate = Date(baseTime.time - Duration.ofHours(hour.toLong()).toMillis())
        val endDate = Date(baseTime.time - Duration.ofHours(hour.toLong() - 1L).toMillis())
        assert(endDate.time - startDate.time == Duration.ofHours(1L).toMillis())
        val startTime = Duration.ofMillis(startDate.time).toSeconds()
        val endTime = Duration.ofMillis(endDate.time).toSeconds()
        assert(Duration.ofSeconds(endTime - startTime).toHours() == 1L)
        return Pair(startTime, endTime)
    }
}
