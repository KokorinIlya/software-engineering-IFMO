package com.github.kokorin.fitness.stats.config

import com.github.kokorin.fitness.common.config.ApiConfig
import com.github.kokorin.fitness.common.config.ApiConfigImpl
import com.github.kokorin.fitness.common.config.DatabaseConfig
import com.github.kokorin.fitness.common.config.DatabaseConfigImpl
import com.typesafe.config.Config

interface ApplicationConfig {
    val apiConfig: ApiConfig
    val databaseConfig: DatabaseConfig
}

class ApplicationConfigImpl(conf: Config) : ApplicationConfig {
    override val apiConfig: ApiConfig = ApiConfigImpl(conf.getConfig("api"))
    override val databaseConfig: DatabaseConfig = DatabaseConfigImpl(conf.getConfig("database"))
}
