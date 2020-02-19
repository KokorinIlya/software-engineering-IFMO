package com.github.kokorin.rx

import com.github.kokorin.rx.command.Command
import com.github.kokorin.rx.config.ApplicationConfigImpl
import com.github.kokorin.rx.dao.ReactiveDaoImpl
import com.github.kokorin.rx.providers.StaticCurrencyConverterProvider
import com.mongodb.rx.client.MongoClients
import com.typesafe.config.ConfigFactory
import io.reactivex.netty.protocol.http.server.HttpServer
import java.nio.file.Paths

fun main() {
    val config = ConfigFactory.parseFile(Paths.get("src/main/resources/application.conf").toFile())
    val applicationConfig = ApplicationConfigImpl(config)
    val databaseConfig = applicationConfig.databaseConfig
    val dao = ReactiveDaoImpl(
        MongoClients.create("${databaseConfig.schema}://${databaseConfig.host}:${databaseConfig.port}")
            .getDatabase(databaseConfig.databaseName),
        StaticCurrencyConverterProvider
    )
    HttpServer
        .newServer(applicationConfig.apiConfig.port)
        .start { request, response ->
            val command = Command.makeCommand(request)
            response.writeString(command.process(dao).map { "$it\n" })
        }
        .awaitShutdown()
}
