package com.github.kokorin.aop.clock

import java.time.Instant

class RealTimeClock : Clock {
    override fun now(): Instant = Instant.now()
}
