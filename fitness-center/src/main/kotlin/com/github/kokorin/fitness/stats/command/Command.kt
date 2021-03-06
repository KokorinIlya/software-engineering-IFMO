package com.github.kokorin.fitness.stats.command

import org.joda.time.LocalDateTime

sealed class Command

data class RegisterExitCommand(
    val uid: Int,
    val enterTimestamp: LocalDateTime, val exitTimestamp: LocalDateTime
) : Command()
