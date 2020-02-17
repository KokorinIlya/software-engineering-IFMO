package com.github.kokorin.searcher.config

import com.typesafe.config.Config

import scala.concurrent.duration.FiniteDuration
import com.github.kokorin.searcher.utils.DurationUtils.RichJavaDuration

trait AggregatorActorConfig {
  val timeout: FiniteDuration
}

class AggregatorActorConfigImpl(conf: Config) extends AggregatorActorConfig {
  override val timeout: FiniteDuration = conf.getDuration("timeout").asScala
}
