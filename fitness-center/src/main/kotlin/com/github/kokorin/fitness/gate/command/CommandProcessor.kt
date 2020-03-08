package com.github.kokorin.fitness.gate.command

import com.github.kokorin.fitness.common.processor.Processor
import com.github.kokorin.fitness.gate.http.StatsHttpClientsProvider
import com.github.kokorin.fitness.gate.utils.serialize
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CommandProcessor(
    private val commandDao: CommandDao,
    private val statsHttpClientsProvider: StatsHttpClientsProvider
) :
    Processor<Command> {
    override suspend fun doProcess(t: Command): String {
        return when (t) {
            is EnterCommand -> {
                commandDao.processEnter(t.uid, t.enterTimestamp)
                "Entering..."
            }
            is ExitCommand -> {
                val enterTimestamp = commandDao.processExit(t.uid, t.exitTimestamp)
                val client = statsHttpClientsProvider.getClient()

                GlobalScope.launch {
                    val response = client.exitCommand(t.uid, enterTimestamp, t.exitTimestamp)
                    // TODO: log response
                }
                "Exiting..."
            }
        }
    }
}
