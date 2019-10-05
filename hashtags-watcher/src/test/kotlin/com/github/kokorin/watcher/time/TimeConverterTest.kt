package com.github.kokorin.watcher.time

import org.junit.Test
import java.util.*
import org.junit.Assert.*
import java.util.concurrent.TimeUnit

class TimeConverterTest {
    @Test
    fun timeConversionTest() {
        val baseDate = Date()
        val timeConverter = TimeConverter(baseDate)
        val baseTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(baseDate.time)
        for (curHour in 1..10000) {
            val (startTime, endTime) = timeConverter.getStartAndEndTime(curHour)
            assertEquals(TimeUnit.SECONDS.toHours(endTime - startTime), 1L)
            assertEquals(TimeUnit.SECONDS.toHours(baseTimeSeconds - startTime), curHour.toLong())
            assertEquals(TimeUnit.SECONDS.toHours(baseTimeSeconds - endTime), (curHour - 1).toLong())
        }
    }
}
