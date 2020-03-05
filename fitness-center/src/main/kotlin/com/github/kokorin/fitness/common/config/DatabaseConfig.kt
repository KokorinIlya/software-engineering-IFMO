package com.github.kokorin.fitness.common.config

import com.typesafe.config.Config

interface DatabaseConfig {
    val host: String
    val port: Int
    val database: String
    val username: String
    val password: String
    val maxActiveConnections: Int
}

class DatabaseConfigImpl(conf: Config) : DatabaseConfig {
    override val host: String = conf.getString("host")
    override val port: Int = conf.getInt("port")
    override val database: String = conf.getString("database")
    override val username: String = conf.getString("username")
    override val password: String = conf.getString("password")
    override val maxActiveConnections: Int = conf.getInt("maxActiveConnections")
}
