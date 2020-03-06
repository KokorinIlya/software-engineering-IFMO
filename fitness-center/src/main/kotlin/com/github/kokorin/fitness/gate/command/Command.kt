package com.github.kokorin.fitness.gate.command

import org.joda.time.LocalDateTime

sealed class Command

data class EnterCommand(val uid: Int, val enterTimestamp: LocalDateTime) : Command()

data class ExitCommand(val uid: Int, val exitTimestamp: LocalDateTime) : Command()

