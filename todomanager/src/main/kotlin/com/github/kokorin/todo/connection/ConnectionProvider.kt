package com.github.kokorin.todo.connection

import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.DriverManager

interface ConnectionProvider {
    fun getConnection(): Connection
}

@Component
class ConnectionProviderImpl(private val databaseConfigProvider: DatabaseConfigProvider) : ConnectionProvider {
    override fun getConnection(): Connection {
        return DriverManager.getConnection(databaseConfigProvider.connectionString)
    }
}
