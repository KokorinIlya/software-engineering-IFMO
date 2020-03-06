package com.github.kokorin.fitness.stats

import com.github.kokorin.fitness.common.postgresql.ConnectionPoolProvider
import com.github.kokorin.fitness.stats.config.ApplicationConfigImpl
import com.github.kokorin.fitness.stats.start.StatsInitializer
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
    val initializer = StatsInitializer(connection)

    val statsMap = initializer.init()

    val server = embeddedServer(Netty, port = applicationConfig.apiConfig.port) {
        routing {
            get("/query/get_stats") {
                val uid = call.request.queryParameters["uid"]?.toInt() ?: -1
                val response = statsMap[uid]?.let {
                    "${it.totalTime.days} days, " +
                            "${it.totalTime.hours} hours, " +
                            "${it.totalTime.minutes} minutes, " +
                            "${it.totalTime.seconds} seconds spent; ${it.visitsCount} total visits"
                } ?: "No such user"
                call.respondText(response)
            }
        }
    }.start(wait = true)
}
