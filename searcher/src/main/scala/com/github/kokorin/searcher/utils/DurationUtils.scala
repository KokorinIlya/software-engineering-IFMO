package com.github.kokorin.searcher.utils

import java.time.{Duration => JavaDuration}
import scala.concurrent.duration.{Duration => ScalaDuration}

object DurationUtils {
  implicit class RichJavaDuration(duration: JavaDuration) {
    def asScala: ScalaDuration = ScalaDuration.fromNanos(duration.getNano)
  }
}
