package com.github.kokorin.searcher.config

import com.typesafe.config.Config

import scala.concurrent.duration.Duration
import com.github.kokorin.searcher.utils.DurationUtils.RichJavaDuration

trait ApiConfig {
  def interface: String
  def port: Int
  def unbindTimeout: Duration
}

class ApiConfigImpl(conf: Config) extends ApiConfig {
  override def interface: String = conf.getString("interface")

  override def port: Int = conf.getInt("port")

  override def unbindTimeout: Duration = conf.getDuration("unbindTimeout").asScala
}
