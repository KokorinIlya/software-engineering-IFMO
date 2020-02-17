package com.github.kokorin.searcher.config

import com.typesafe.config.Config

import scala.concurrent.duration.Duration
import com.github.kokorin.searcher.utils.DurationUtils.RichJavaDuration

trait SearcherActorConfig {
  val timeout: Duration
}

class SearcherActorConfigImpl(conf: Config) extends SearcherActorConfig {
  override val timeout: Duration = conf.getDuration("timeout").asScala
}
