package com.github.kokorin.searcher

import com.typesafe.config.ConfigFactory
import java.nio.file.Paths

import com.github.kokorin.searcher.config.ApplicationConfigImpl
import com.github.kokorin.searcher.web.api.{ApiHandler, WebApiStarter}
import org.apache.log4j.PropertyConfigurator

object Main {
  def main(args: Array[String]): Unit = {
    PropertyConfigurator.configure("src/main/resources/log4j.properties")
    val mainConfig =
      ConfigFactory.parseFile(
        Paths.get("src/main/resources/application.conf").toFile
      )
    val appConfig = new ApplicationConfigImpl(mainConfig)
    val apiHandler = new ApiHandler(appConfig)
    new WebApiStarter("api-actorsystem", apiHandler, appConfig.apiConfig)
      .start()
  }
}
