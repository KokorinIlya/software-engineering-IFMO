package com.github.kokorin.events.stats

import com.github.kokorin.events.clock.TimeTravelClock
import org.junit.Test
import org.junit.Assert.*
import java.time.Duration
import java.time.Instant
import kotlin.math.abs

class RPMEventStatisticsTest {
    private val epsilon = 1e-5

    private fun statsEqual(first: Map<String, Double>, second: Map<String, Double>): Boolean {
        if (first.keys != second.keys) {
            return false
        }
        for ((key, firstValue) in first) {
            val secondValue = second[key]!!
            if (abs(firstValue - secondValue) > epsilon) {
                return false
            }
        }
        return true
    }

    @Test
    fun simpleTest() {
        val startTime = Instant.now()
        val clock = TimeTravelClock(startTime)
        val stats = RPMEventStatistics(clock)

        stats.incEvent("a")
        stats.incEvent("b")
        stats.incEvent("a")

        assertTrue(
            statsEqual(
                stats.getAllEventStatistic(), mapOf(
                    Pair("a", 2.0 / 60.0),
                    Pair("b", 1.0 / 60.0)
                )
            )
        )
    }

    @Test
    fun forgetSingleTest() {
        val startTime = Instant.now()
        val clock = TimeTravelClock(startTime)
        val stats = RPMEventStatistics(clock)

        stats.incEvent("a")

        clock.plus(Duration.ofHours(1))

        stats.incEvent("b")
        stats.incEvent("a")

        assertTrue(
            statsEqual(
                stats.getAllEventStatistic(), mapOf(
                    Pair("a", 1.0 / 60.0),
                    Pair("b", 1.0 / 60.0)
                )
            )
        )
    }

    @Test
    fun forgetEventTest() {
        val startTime = Instant.now()
        val clock = TimeTravelClock(startTime)
        val stats = RPMEventStatistics(clock)

        stats.incEvent("b")
        stats.incEvent("b")

        clock.plus(Duration.ofHours(1))

        stats.incEvent("a")

        assertTrue(
            statsEqual(
                stats.getAllEventStatistic(), mapOf(
                    Pair("a", 1.0 / 60.0)
                )
            )
        )
    }

    @Test
    fun partialForgetTest() {
        val startTime = Instant.now()
        val clock = TimeTravelClock(startTime)
        val stats = RPMEventStatistics(clock)

        stats.incEvent("b")
        stats.incEvent("b")

        clock.plus(Duration.ofMinutes(10))

        assertTrue(
            statsEqual(
                stats.getAllEventStatistic(), mapOf(
                    Pair("b", 2.0 / 60.0)
                )
            )
        )

        stats.incEvent("b")

        assertTrue(
            statsEqual(
                stats.getAllEventStatistic(), mapOf(
                    Pair("b", 3.0 / 60.0)
                )
            )
        )

        clock.plus(Duration.ofMinutes(51))

        assertTrue(
            statsEqual(
                stats.getAllEventStatistic(), mapOf(
                    Pair("b", 1.0 / 60.0)
                )
            )
        )
    }
}


