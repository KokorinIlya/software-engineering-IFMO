package com.github.kokorin.fitness.stats.command

import org.joda.time.LocalDateTime

interface UpdatableStats {
    fun updateState(uid: Int, enterTime: LocalDateTime, exitTime: LocalDateTime)
}
