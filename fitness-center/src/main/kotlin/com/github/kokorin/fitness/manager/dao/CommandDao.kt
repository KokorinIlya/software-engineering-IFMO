package com.github.kokorin.fitness.manager.dao

import java.util.*

interface CommandDao {
    suspend fun registerNewUser(): Int

    suspend fun subscriptionRenewal(uid: Int, until: Date)
}
