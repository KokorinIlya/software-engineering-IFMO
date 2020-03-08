package com.github.kokorin.fitness.gate.command

import com.github.kokorin.fitness.common.processor.Processor
import com.github.kokorin.fitness.gate.http.StatsHttpClientsProvider
import com.github.kokorin.fitness.gate.utils.serialize
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class CommandProcessor(
    private val commandDao: CommandDao,
    private val statsHttpClientsProvider: StatsHttpClientsProvider
) : Processor<Command> {
    private val log = LoggerFactory.getLogger(this.javaClass)

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
                    val exitRequest =
                        "EXIT{uid = ${t.uid}, enter_timestamp = $enterTimestamp, exit_timestamp = ${t.exitTimestamp}}"
                    log.info(
                        "STATS << $exitRequest"
                    )
                    val response = try {
                        client.exitCommand(t.uid, enterTimestamp, t.exitTimestamp)
                    } catch (e: Exception) {
                        log.error("Error while executing request to stats module", e)
                        "ERROR: ${e.message}"
                    }
                    log.info(
                        "$exitRequest: STATS >> $response"
                    )
                }
                "Exiting..."
            }
        }
    }
}
