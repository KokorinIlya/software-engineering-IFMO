package com.github.kokorin.events.clock

import java.time.Instant

class RealTimeClock : Clock {
    override fun now(): Instant = Instant.now()
}
