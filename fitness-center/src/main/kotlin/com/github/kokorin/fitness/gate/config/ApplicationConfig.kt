package com.github.kokorin.fitness.gate.config

import com.github.kokorin.fitness.common.config.ApiConfig
import com.github.kokorin.fitness.common.config.ApiConfigImpl
import com.github.kokorin.fitness.common.config.DatabaseConfig
import com.github.kokorin.fitness.common.config.DatabaseConfigImpl
import com.typesafe.config.Config

interface ApplicationConfig {
    val apiConfig: ApiConfig
    val databaseConfig: DatabaseConfig
    val statsConfig: StatsConfig
}

class ApplicationConfigImpl(conf: Config) : ApplicationConfig {
    override val apiConfig: ApiConfig = ApiConfigImpl(conf.getConfig("api"))
    override val databaseConfig: DatabaseConfig = DatabaseConfigImpl(conf.getConfig("database"))
    override val statsConfig: StatsConfig = StatsConfigImpl(conf.getConfig("stats"))
}
