package com.github.kokorin.fitness.gate

import com.github.kokorin.fitness.common.postgresql.ConnectionPoolProvider
import com.github.kokorin.fitness.gate.command.CommandDaoImpl
import com.github.kokorin.fitness.gate.command.CommandProcessor
import com.github.kokorin.fitness.gate.command.EnterCommand
import com.github.kokorin.fitness.gate.command.ExitCommand
import com.github.kokorin.fitness.gate.config.ApplicationConfigImpl
import com.github.kokorin.fitness.gate.http.StatsHttpClientsProviderImpl
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.joda.time.LocalDateTime

fun main(): Unit = runBlocking {
    val pathToConfig = Paths.get("src/main/resources/gate/application.conf")
    val config = ConfigFactory.parseFile(pathToConfig.toFile())
    val applicationConfig = ApplicationConfigImpl(config)
    val connection = ConnectionPoolProvider.getConnection(applicationConfig.databaseConfig)
    val dao = CommandDaoImpl(connection)
    val clientsProvider = StatsHttpClientsProviderImpl(applicationConfig.statsConfig)
    val commandProcessor = CommandProcessor(dao, clientsProvider)

    val server = embeddedServer(Netty, port = applicationConfig.apiConfig.port) {
        routing {
            get("/command/enter") {
                val uid = call.request.queryParameters["uid"]?.toInt() ?: -1
                val enterTime = LocalDateTime.now()
                val command = EnterCommand(uid, enterTime)
                call.respondText(commandProcessor.process(command))
            }
            get("/command/exit") {
                val uid = call.request.queryParameters["uid"]?.toInt() ?: -1
                val exitTime = LocalDateTime.now()
                val command = ExitCommand(uid, exitTime)
                call.respondText(commandProcessor.process(command))
            }
        }
    }.start(wait = true)
}
