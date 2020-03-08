package com.github.kokorin.fitness.gate.command

import org.joda.time.LocalDateTime

interface CommandDao {
    suspend fun processEnter(uid: Int, enterTime: LocalDateTime)

    suspend fun processExit(uid: Int, exitTime: LocalDateTime): LocalDateTime
}
