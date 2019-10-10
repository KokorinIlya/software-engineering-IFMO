package com.github.kokorin.products.connection

import java.sql.Connection
import java.sql.DriverManager

class ConnectionProviderImpl(private val connectionName: String) : ConnectionProvider {
    override fun getConnection(): Connection {
        return DriverManager.getConnection(connectionName)
    }
}
