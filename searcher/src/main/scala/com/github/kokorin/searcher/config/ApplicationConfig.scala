package com.github.kokorin.searcher.config

import com.typesafe.config.Config
import scala.collection.JavaConverters._

trait ApplicationConfig {
  val apiConfig: ApiConfig
  val searchEngines: Seq[SearchEngineConfig]
  val aggregatorActorConfig: AggregatorActorConfig
  val searcherActorConfig: SearcherActorConfig
}

class ApplicationConfigImpl(conf: Config) extends ApplicationConfig {
  override val apiConfig: ApiConfig = new ApiConfigImpl(conf.getConfig("api"))

  override val searchEngines: Seq[SearchEngineConfig] = {
    val enginesConfig = conf.getConfig("searchEngines")
    enginesConfig.getStringList("enginesList").asScala.map { name =>
      new SearchEngineConfigImpl(enginesConfig.getConfig(name), name)
    }
  }

  private val actorsConfig = conf.getConfig("actors")

  override val aggregatorActorConfig: AggregatorActorConfig =
    new AggregatorActorConfigImpl(actorsConfig.getConfig("aggregator"))

  override val searcherActorConfig: SearcherActorConfig =
    new SearcherActorConfigImpl(actorsConfig.getConfig("searcher"))
}
