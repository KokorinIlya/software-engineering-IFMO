package com.github.kokorin.searcher.config

import com.typesafe.config.Config

trait SearchEngineConfig {
  def host: String
  def port: Int
  def name: String
}

class SearchEngineConfigImpl(conf: Config) extends SearchEngineConfig {
  override def host: String = conf.getString("host")

  override def port: Int = conf.getInt("port")

  override def name: String = conf.getString("name")
}

