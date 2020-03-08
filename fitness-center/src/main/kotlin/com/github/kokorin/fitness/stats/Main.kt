package com.github.kokorin.fitness.stats

import com.github.kokorin.fitness.common.postgresql.ConnectionPoolProvider
import com.github.kokorin.fitness.common.utils.getTimestamp
import com.github.kokorin.fitness.common.utils.getUid
import com.github.kokorin.fitness.stats.command.CommandProcessor
import com.github.kokorin.fitness.stats.command.RegisterExitCommand
import com.github.kokorin.fitness.stats.config.ApplicationConfigImpl
import com.github.kokorin.fitness.stats.query.GetUserStatsQuery
import com.github.kokorin.fitness.stats.query.QueryProcessor
import com.github.kokorin.fitness.stats.state.StatsState
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.nio.file.Paths

fun main(): Unit = runBlocking {
    val pathToConfig = Paths.get("src/main/resources/stats/application.conf")
    val config = ConfigFactory.parseFile(pathToConfig.toFile())
    val applicationConfig = ApplicationConfigImpl(config)
    val connection = ConnectionPoolProvider.getConnection(applicationConfig.databaseConfig)
    val stateHolder = StatsState()
    val queryProcessor = QueryProcessor(stateHolder)
    val commandProcessor = CommandProcessor(stateHolder)

    stateHolder.init(connection)
    val server = embeddedServer(Netty, port = applicationConfig.apiConfig.port) {
        routing {
            get("/query/get_stats") {
                val uid = call.request.queryParameters.getUid()
                val query = GetUserStatsQuery(uid)
                call.respondText(queryProcessor.process(query))
            }
            get("command/exit") {
                val uid = call.request.queryParameters.getUid()
                val enterTime = call.request.queryParameters.getTimestamp("enter")
                val exitTime = call.request.queryParameters.getTimestamp("exit")
                val command = RegisterExitCommand(uid, enterTime, exitTime)
                call.respondText(commandProcessor.process(command))
            }
        }
    }.start(wait = true)
}
