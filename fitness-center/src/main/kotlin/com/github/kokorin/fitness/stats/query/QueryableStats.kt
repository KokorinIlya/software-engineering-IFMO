package com.github.kokorin.fitness.stats.query

import com.github.kokorin.fitness.stats.model.UserStats

interface QueryableStats {
    fun getUserStats(uid: Int): UserStats?
}
