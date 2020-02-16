package com.github.kokorin.searcher.config

import com.github.kokorin.searcher.engines.EnginesListProvider
import com.typesafe.config.Config

trait ApplicationConfig {
  def apiConfig: ApiConfig
  def searchEngines: Seq[SearchEngineConfig]
}

class ApplicationConfigImpl(conf: Config) extends ApplicationConfig {
  override def apiConfig: ApiConfig = new ApiConfigImpl(conf.getConfig("api"))

  override def searchEngines: Seq[SearchEngineConfig] = {
    val enginesConfig = conf.getConfig("searchEngines")
    EnginesListProvider.getEngineNames.map { name =>
      new SearchEngineConfigImpl(enginesConfig.getConfig(name))
    }
  }
}
