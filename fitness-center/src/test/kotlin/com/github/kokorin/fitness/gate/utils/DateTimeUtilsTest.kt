package com.github.kokorin.fitness.gate.utils

import org.joda.time.LocalDateTime
import org.junit.Test
import org.junit.Assert.*

class DateTimeUtilsTest {
    @Test
    fun testSerialization() {
        val date = LocalDateTime.parse("1862-04-15T20:00:00.000")
        val dateStr = date.serialize()
        assertEquals(dateStr, "1862-04-15T20:00:00")
    }
}
