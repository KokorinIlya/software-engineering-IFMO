package com.github.kokorin.fitness.gate.model

import org.joda.time.LocalDateTime

data class Event(val eventType: GateEventType, val eventTimestamp: LocalDateTime)
