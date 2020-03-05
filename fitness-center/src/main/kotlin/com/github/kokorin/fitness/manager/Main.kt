package com.github.kokorin.fitness.manager

import com.github.jasync.sql.db.asSuspending
import com.github.kokorin.fitness.common.postgresql.ConnectionPoolProvider
import com.github.kokorin.fitness.manager.config.ApplicationConfigImpl
import com.github.kokorin.fitness.manager.dao.ManagerDao
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.nio.file.Paths

fun main(): Unit = runBlocking {
    val pathToConfig = Paths.get("src/main/resources/manager/application.conf")
    val config = ConfigFactory.parseFile(pathToConfig.toFile())
    val applicationConfig = ApplicationConfigImpl(config)
    val connection = ConnectionPoolProvider.getConnection(applicationConfig.databaseConfig)
    val dao = ManagerDao(connection)

    val server = embeddedServer(Netty, port = applicationConfig.apiConfig.port) {
        routing {
            get("/command/new_uid") {
                try {
                    val newUid = dao.registerNewUser()
                    call.respondText("New UID = $newUid")
                } catch (e: Exception) {
                    call.respondText("Error registering new user: ${e.message}")
                }
            }
            get("/command/exit") {
                val uid = call.parameters["uid"]
                call.respondText("EXIT, uid = $uid")
            }
        }
    }.start(wait = true)
}
