package com.github.kokorin.fitness.common.postgresql

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.SuspendingConnection
import com.github.jasync.sql.db.asSuspending
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder
import com.github.kokorin.fitness.common.config.DatabaseConfig

interface ConnectionProvider {
    fun getConnection(conf: DatabaseConfig): SuspendingConnection
}

object ConnectionPoolProvider : ConnectionProvider {
    override fun getConnection(conf: DatabaseConfig): SuspendingConnection {
        return PostgreSQLConnectionBuilder.createConnectionPool {
            host = conf.host
            port = conf.port
            database = conf.database
            username = conf.username
            password = conf.password
            maxActiveConnections = conf.maxActiveConnections
        }.asSuspending
    }

}
