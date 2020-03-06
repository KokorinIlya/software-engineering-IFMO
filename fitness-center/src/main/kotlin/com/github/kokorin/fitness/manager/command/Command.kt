package com.github.kokorin.fitness.manager.command

import org.joda.time.LocalDateTime

sealed class Command

object NewUserCommand : Command()

data class SubscriptionRenewalCommand(val uid: Int, val until: LocalDateTime) : Command()
