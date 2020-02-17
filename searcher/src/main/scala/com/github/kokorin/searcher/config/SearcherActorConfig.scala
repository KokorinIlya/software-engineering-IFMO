package com.github.kokorin.searcher.config

import com.typesafe.config.Config

import scala.concurrent.duration.FiniteDuration
import com.github.kokorin.searcher.utils.DurationUtils.RichJavaDuration

trait SearcherActorConfig {
  val timeout: FiniteDuration
}

class SearcherActorConfigImpl(conf: Config) extends SearcherActorConfig {
  override val timeout: FiniteDuration = conf.getDuration("timeout").asScala
}
