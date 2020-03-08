package com.github.kokorin.fitness.common.utils

import io.ktor.http.Parameters
import org.joda.time.LocalDateTime

fun Parameters.getUid(): Int {
    return this["uid"]?.toInt() ?: -1
}

fun Parameters.getTimestamp(paramName: String): LocalDateTime {
    return LocalDateTime.parse(this[paramName] ?: "1862-04-14T00:00:00")
}
