package com.github.kokorin.fitness.gate.command

import com.github.kokorin.fitness.common.processor.Processor
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class CommandProcessor(private val commandDao: CommandDao) : Processor<Command> {
    override suspend fun doProcess(t: Command): String {
        return when (t) {
            is EnterCommand -> {
                commandDao.processEnter(t.uid, t.enterTimestamp)
                "Entering..."
            }
            is ExitCommand -> {
                val enterTime = commandDao.processExit(t.uid, t.exitTimestamp)
                val client = HttpClient() // TODO : mock client provider

                GlobalScope.launch {
                    // TODO: use configs
                    // TODO: make correct string in utils
                    val enterTimeString = enterTime.toString("yyyy-MM-dd'T'HH:mm:ss")
                    val exitTimeString = t.exitTimestamp.toString("yyyy-MM-dd'T'HH:mm:ss")
                    println("TIME IS $enterTimeString, $exitTimeString")
                    val url = "http://localhost:8082/command/exit?uid=${t.uid}&enter=$enterTimeString&exit=${exitTimeString}"
                    client.get<String>(url)
                    // TODO: log response
                }
                "Exiting..."
            }
        }
    }
}
