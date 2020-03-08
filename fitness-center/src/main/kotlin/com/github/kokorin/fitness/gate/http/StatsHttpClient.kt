package com.github.kokorin.fitness.gate.http

import org.joda.time.LocalDateTime

interface StatsHttpClient {
    suspend fun exitCommand(uid: Int, enterTimestamp: LocalDateTime, exitTimestamp: LocalDateTime): String
}
