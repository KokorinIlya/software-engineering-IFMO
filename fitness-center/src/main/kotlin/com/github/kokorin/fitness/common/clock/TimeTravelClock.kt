package com.github.kokorin.fitness.common.clock

import org.joda.time.LocalDateTime
import org.joda.time.Period

class TimeTravelClock(startTime: LocalDateTime) : Clock {
    private var curTime = startTime

    override fun now(): LocalDateTime = curTime

    fun setTime(newTime: LocalDateTime) {
        curTime = newTime
    }

    fun plus(period: Period) {
        curTime.plus(period)
    }

    fun minus(period: Period) {
        curTime.minus(period)
    }
}
