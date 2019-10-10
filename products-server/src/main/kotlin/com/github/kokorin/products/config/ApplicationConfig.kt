package com.github.kokorin.products.config

import com.typesafe.config.Config

interface ApplicationConfig {
    val port: Int

    val database: String
}

class ApplicationConfigImpl(conf: Config) : ApplicationConfig {
    override val database: String = conf.getConfig("storage").getString("database")

    override val port: Int = conf.getConfig("server").getInt("port")
}
