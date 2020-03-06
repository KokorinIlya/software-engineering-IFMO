package com.github.kokorin.fitness.manager.command

import com.github.kokorin.fitness.common.processor.Processor

class CommandProcessor(private val commandDao: CommandDao) :
    Processor<Command> {
    override suspend fun doProcess(t: Command): String {
        return when (t) {
            NewUserCommand -> {
                val newUid = commandDao.registerNewUser()
                "New UID = $newUid"
            }
            is SubscriptionRenewalCommand -> {
                commandDao.subscriptionRenewal(t.uid, t.until)
                "Successful renewal"
            }
        }
    }
}
