package com.github.kokorin.fitness.gate.config

import com.typesafe.config.Config

interface StatsConfig {
    val host: String
    val port: Int
    val schema: String
}

class StatsConfigImpl(conf: Config) : StatsConfig {
    override val host: String = conf.getString("host")
    override val port: Int = conf.getInt("port")
    override val schema: String = conf.getString("schema")
}
