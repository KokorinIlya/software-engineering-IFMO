package com.github.kokorin.todo.connection

import com.github.kokorin.todo.config.ConfigProvider
import org.springframework.stereotype.Component

interface DatabaseConfigProvider {
    val connectionString: String
}

@Component
class DatabaseConfigProviderImpl(private val configProvider: ConfigProvider) : DatabaseConfigProvider {
    private val databaseConfig = configProvider.config.getConfig("database")
    private val host = databaseConfig.getString("host")
    private val port = databaseConfig.getInt("port")
    private val address = databaseConfig.getString("address")
    private val protocol = databaseConfig.getString("protocol")
    override val connectionString: String = "jdbc:$protocol://$host:$port/$address"
}
