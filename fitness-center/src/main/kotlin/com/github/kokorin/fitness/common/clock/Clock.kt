package com.github.kokorin.fitness.common.clock

import org.joda.time.LocalDateTime

interface Clock {
    fun now(): LocalDateTime
}
