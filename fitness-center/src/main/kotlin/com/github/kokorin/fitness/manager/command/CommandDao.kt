package com.github.kokorin.fitness.manager.command

import org.joda.time.LocalDateTime

interface CommandDao {
    suspend fun registerNewUser(): Int

    suspend fun subscriptionRenewal(uid: Int, until: LocalDateTime)
}
