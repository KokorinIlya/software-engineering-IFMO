package com.github.kokorin.fitness.stats.command

import com.github.kokorin.fitness.common.processor.Processor

class CommandProcessor(private val stats: UpdatableStats) : Processor<Command> {
    override suspend fun doProcess(t: Command): String {
        return when (t) {
            is RegisterExitCommand -> {
                stats.updateState(t.uid, t.enterTimestamp, t.exitTimestamp)
                "OK"
            }
        }
    }
}
