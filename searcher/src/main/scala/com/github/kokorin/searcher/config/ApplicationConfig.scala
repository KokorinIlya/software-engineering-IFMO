package com.github.kokorin.searcher.config

import com.typesafe.config.Config
import scala.collection.JavaConverters._

trait ApplicationConfig {
  val apiConfig: ApiConfig
  val searchEngines: Seq[SearchEngineConfig]
  val aggregatorActorConfig: AggregatorActorConfig
}

class ApplicationConfigImpl(conf: Config) extends ApplicationConfig {
  override val apiConfig: ApiConfig = new ApiConfigImpl(conf.getConfig("api"))

  override val searchEngines: Seq[SearchEngineConfig] = {
    val enginesConfig = conf.getConfig("searchEngines")
    enginesConfig.getStringList("enginesList").asScala.map { name =>
      new SearchEngineConfigImpl(enginesConfig.getConfig(name), name)
    }
  }

  override val aggregatorActorConfig: AggregatorActorConfig =
    new AggregatorActorConfigImpl(
      conf.getConfig("actors").getConfig("aggregator")
    )
}
