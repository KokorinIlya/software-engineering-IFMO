package com.github.kokorin.fitness.stats.query

import com.github.kokorin.fitness.common.processor.Processor

class QueryProcessor(private val stats: QueryableStats) : Processor<Query> {
    override suspend fun doProcess(t: Query): String {
        return when (t) {
            is GetUserStatsQuery -> {
                stats.getUserStats(t.uid)?.let {
                    val normalizedPeriod = it.totalTime.normalizedStandard()
                    "${normalizedPeriod.years} years, " +
                            "${normalizedPeriod.months} months, " +
                            "${normalizedPeriod.weeks} weeks, " +
                            "${normalizedPeriod.days} days, " +
                            "${normalizedPeriod.hours} hours, " +
                            "${normalizedPeriod.minutes} minutes, " +
                            "${normalizedPeriod.seconds} seconds, " +
                            "${normalizedPeriod.millis} milliseconds spent; " +
                            "${it.visitsCount} total visits"
                } ?: "No such user"
            }
        }
    }

}
