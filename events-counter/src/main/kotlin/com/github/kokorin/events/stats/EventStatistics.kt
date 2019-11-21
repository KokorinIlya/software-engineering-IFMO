package com.github.kokorin.events.stats

interface EventStatistics {
    fun incEvent(name: String)

    fun getEventStatisticByName(name: String): Double

    fun getAllEventStatistic(): Map<String, Double>

    fun printStatistic()
}
