package com.github.kokorin.fitness.gate.config

import com.github.kokorin.fitness.common.config.ApiConfig
import com.github.kokorin.fitness.common.config.ApiConfigImpl
import com.github.kokorin.fitness.common.config.DatabaseConfig
import com.github.kokorin.fitness.common.config.DatabaseConfigImpl
import com.typesafe.config.Config

interface ApplicationConfig {
    val apiConfig: ApiConfig
    val databaseConfig: DatabaseConfig
    // TODO: stats config
}

class ApplicationConfigImpl(conf: Config) : ApplicationConfig {
    override val apiConfig: ApiConfig = ApiConfigImpl(conf.getConfig("api"))
    override val databaseConfig: DatabaseConfig = DatabaseConfigImpl(conf.getConfig("database"))
}
