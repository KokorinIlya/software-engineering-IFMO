package com.github.kokorin.fitness.gate.command

import com.github.kokorin.fitness.common.processor.Processor

class CommandProcessor(private val commandDao: CommandDao) : Processor<Command> {
    override suspend fun doProcess(t: Command): String {
        return when (t) {
            is EnterCommand -> {
                commandDao.processEnter(t.uid, t.enterTimestamp)
                "Entering..."
            }
            is ExitCommand -> {
                commandDao.processExit(t.uid, t.exitTimestamp)
                "Exiting..."
            }
        }
    }
}
