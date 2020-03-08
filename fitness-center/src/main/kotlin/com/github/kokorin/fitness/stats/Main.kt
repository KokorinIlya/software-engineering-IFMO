package com.github.kokorin.fitness.stats

import com.github.kokorin.fitness.common.postgresql.ConnectionPoolProvider
import com.github.kokorin.fitness.stats.config.ApplicationConfigImpl
import com.github.kokorin.fitness.stats.model.UserStats
import com.github.kokorin.fitness.stats.start.StatsInitializer
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.joda.time.LocalDateTime
import org.joda.time.Period
import java.nio.file.Paths

fun main(): Unit = runBlocking {
    val pathToConfig = Paths.get("src/main/resources/stats/application.conf")
    val config = ConfigFactory.parseFile(pathToConfig.toFile())
    val applicationConfig = ApplicationConfigImpl(config)
    val connection = ConnectionPoolProvider.getConnection(applicationConfig.databaseConfig)
    val initializer = StatsInitializer(connection)

    // TODO: state to container
    val statsMap = initializer.init()

    val server = embeddedServer(Netty, port = applicationConfig.apiConfig.port) {
        routing {
            // TODO: command & query processors
            get("/query/get_stats") {
                val uid = call.request.queryParameters["uid"]?.toInt() ?: -1
                val response = statsMap[uid]?.let {
                    val normalizedPeriod = it.totalTime.normalizedStandard()
                    // TODO: months & years
                    "${normalizedPeriod.days} days, " +
                            "${normalizedPeriod.hours} hours, " +
                            "${normalizedPeriod.minutes} minutes, " +
                            "${normalizedPeriod.seconds} seconds, " +
                            "${normalizedPeriod.millis} milliseconds spent; ${it.visitsCount} total visits"
                } ?: "No such user"
                call.respondText(response)
            }
            get("command/exit") {
                val uid = call.request.queryParameters["uid"]?.toInt() ?: -1
                val enterTime = LocalDateTime.parse(call.request.queryParameters["enter"])
                val exitTime = LocalDateTime.parse(call.request.queryParameters["exit"])
                val period = Period.fieldDifference(enterTime, exitTime)
                statsMap.compute(uid) { _, curStats ->
                    if (curStats == null) {
                        UserStats(period, 1)
                    } else {
                        UserStats(curStats.totalTime.plus(period), curStats.visitsCount + 1)
                    }
                }
                call.respondText("OK")
            }
        }
    }.start(wait = true)
}
