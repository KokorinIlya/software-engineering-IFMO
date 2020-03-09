package com.github.kokorin.fitness.common.clock

import org.joda.time.LocalDateTime

object RealTimeClock : Clock {
    override fun now(): LocalDateTime = LocalDateTime.now()
}
