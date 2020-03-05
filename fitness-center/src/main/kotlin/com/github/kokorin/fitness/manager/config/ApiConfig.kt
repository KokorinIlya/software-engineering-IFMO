package com.github.kokorin.fitness.manager.config

import com.typesafe.config.Config

interface ApiConfig {
    val port: Int
}

class ApiConfigImpl(conf: Config) : ApiConfig {
    override val port: Int = conf.getInt("port")

}
