package com.github.kokorin.searcher.config

import com.typesafe.config.Config

import scala.concurrent.duration.Duration
import com.github.kokorin.searcher.utils.DurationUtils.RichJavaDuration

trait ApiConfig {
  val interface: String
  val port: Int
  val unbindTimeout: Duration
}

class ApiConfigImpl(conf: Config) extends ApiConfig {
  override val interface: String = conf.getString("interface")

  override val port: Int = conf.getInt("port")

  override val unbindTimeout: Duration = conf.getDuration("unbindTimeout").asScala
}
