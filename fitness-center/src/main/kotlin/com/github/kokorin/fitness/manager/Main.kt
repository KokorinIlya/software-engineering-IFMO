package com.github.kokorin.fitness.manager

import com.github.kokorin.fitness.common.postgresql.ConnectionPoolProvider
import com.github.kokorin.fitness.common.utils.getTimestamp
import com.github.kokorin.fitness.common.utils.getUid
import com.github.kokorin.fitness.manager.config.ApplicationConfigImpl
import com.github.kokorin.fitness.manager.command.CommandDaoImpl
import com.github.kokorin.fitness.manager.command.CommandProcessor
import com.github.kokorin.fitness.manager.command.NewUserCommand
import com.github.kokorin.fitness.manager.command.SubscriptionRenewalCommand
import com.github.kokorin.fitness.manager.query.GetUserQuery
import com.github.kokorin.fitness.manager.query.QueryDaoImpl
import com.github.kokorin.fitness.manager.query.QueryProcessor
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

fun main(): Unit = runBlocking {
    val pathToConfig = Paths.get("src/main/resources/manager/application.conf")
    val config = ConfigFactory.parseFile(pathToConfig.toFile())
    val applicationConfig = ApplicationConfigImpl(config)
    val connection = ConnectionPoolProvider.getConnection(applicationConfig.databaseConfig)
    val commandDao = CommandDaoImpl(connection)
    val queryDao = QueryDaoImpl(connection)
    val commandProcessor = CommandProcessor(commandDao)
    val queryProcessor = QueryProcessor(queryDao)

    embeddedServer(Netty, port = applicationConfig.apiConfig.port) {
        routing {
            get("/command/new_uid") {
                call.respondText(commandProcessor.process(NewUserCommand))
            }
            get("/command/renewal") {
                val uid = call.request.queryParameters.getUid()
                val until = call.request.queryParameters.getTimestamp("until")
                val command = SubscriptionRenewalCommand(uid, until)
                call.respondText(commandProcessor.process(command))
            }
            get("query/get_user") {
                val uid = call.request.queryParameters.getUid()
                val query = GetUserQuery(uid)
                call.respondText(queryProcessor.process(query))
            }
        }
    }.start(wait = true)
    Unit
}
