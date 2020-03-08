package com.github.kokorin.fitness.stats.query

sealed class Query

data class GetUserStatsQuery(val uid: Int) : Query()
