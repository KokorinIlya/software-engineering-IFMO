package com.github.kokorin.fitness.manager.dao

import com.github.kokorin.fitness.manager.model.User

interface QueryDao {
    suspend fun getUser(uid: Int): User?
}
