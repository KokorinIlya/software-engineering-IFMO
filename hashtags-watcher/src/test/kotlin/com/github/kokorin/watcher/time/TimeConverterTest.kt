package com.github.kokorin.watcher.time

import com.github.kokorin.watcher.utils.toSeconds
import org.junit.Test
import java.util.*
import org.junit.Assert.*
import java.time.Duration

class TimeConverterTest {
    @Test
    fun timeConversionTest() {
        val baseDate = Date()
        val timeConverter = TimeConverter(baseDate)
        val baseTimeSeconds = Duration.ofMillis(baseDate.time).toSeconds()
        for (curHour in 1..10000) {
            val (startTime, endTime) = timeConverter.getStartAndEndTime(curHour)
            assertEquals(Duration.ofSeconds(endTime - startTime).toHours(), 1L)
            assertEquals(Duration.ofSeconds(baseTimeSeconds - startTime).toHours(), curHour.toLong())
            assertEquals(Duration.ofSeconds(baseTimeSeconds - endTime).toHours(), (curHour - 1).toLong())
        }
    }
}
