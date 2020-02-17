package com.github.kokorin.searcher.config

import com.typesafe.config.Config

trait SearchEngineConfig {
  val host: String
  val port: Int
  val name: String
}

class SearchEngineConfigImpl(conf: Config, override val name: String)
    extends SearchEngineConfig {
  override val host: String = conf.getString("host")

  override val port: Int = conf.getInt("port")
}
