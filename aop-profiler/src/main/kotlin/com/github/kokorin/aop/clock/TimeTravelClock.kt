package com.github.kokorin.aop.clock

import java.time.Instant
import java.time.temporal.TemporalAmount
import java.util.concurrent.atomic.AtomicReference

class TimeTravelClock(startTime: Instant) : Clock {
    private var currentInstant: AtomicReference<Instant> = AtomicReference(startTime)

    override fun now(): Instant = currentInstant.get()

    fun setNow(newInstant: Instant) {
        currentInstant.set(newInstant)
    }

    fun plus(interval: TemporalAmount) {
        currentInstant.updateAndGet { it.plus(interval) }
    }

    fun minus(interval: TemporalAmount) {
        currentInstant.updateAndGet { it.minus(interval) }
    }
}
