package com.github.kokorin.fitness.gate.utils

import org.joda.time.LocalDateTime

fun LocalDateTime.serialize(): String = this.toString("yyyy-MM-dd'T'HH:mm:ss")
