package com.github.kokorin.searcher.engines

import java.nio.file.Paths

import com.github.kokorin.searcher.config.ApiConfigImpl
import com.github.kokorin.searcher.web.api.WebApiStarter
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

object EnginesMain {
  def main(args: Array[String]): Unit = {
    val enginesConfig =
      ConfigFactory
        .parseFile(Paths.get("src/main/resources/search_engines_api.conf").toFile)
        .getConfig("searchEngines")
    for {
      engineName <- enginesConfig.getStringList("enginesList").asScala
      actorSystemName = s"$engineName-api-actorsystem"
      engineHandler = new EngineHandler(engineName)
      engineConf = enginesConfig.getConfig(engineName)
      engineApiConf = new ApiConfigImpl(engineConf)
    } {
      new WebApiStarter(actorSystemName, engineHandler, engineApiConf).start()
    }
  }
}
