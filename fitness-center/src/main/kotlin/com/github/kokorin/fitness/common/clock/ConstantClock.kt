package com.github.kokorin.fitness.common.clock

import org.joda.time.LocalDateTime

class ConstantClock(private val startTime: LocalDateTime) : Clock {
    override fun now(): LocalDateTime = startTime
}
