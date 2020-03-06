package com.github.kokorin.fitness.manager.query

import com.github.kokorin.fitness.manager.model.User

interface QueryDao {
    suspend fun getUser(uid: Int): User?
}
