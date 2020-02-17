package com.github.kokorin.searcher.utils

import java.time.{Duration => JavaDuration}
import scala.concurrent.duration.{Duration => ScalaDuration, FiniteDuration}

object DurationUtils {
  implicit class RichJavaDuration(val duration: JavaDuration) extends AnyVal {
    def asScala: FiniteDuration = ScalaDuration.fromNanos(duration.toNanos)
  }
}
