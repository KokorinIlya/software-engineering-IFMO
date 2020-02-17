package com.github.kokorin.searcher.engines

import java.util.regex.Pattern

import akka.http.scaladsl.server.Route
import com.github.kokorin.searcher.web.Handler
import akka.http.scaladsl.server.Directives._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import EngineHandler.{Formats, WhitespacePattern}
import com.github.kokorin.searcher.model.SearchEngineResponse

class EngineHandler(engineName: String) extends Handler {
  override def route: Route = (get & path("search") & parameter('query)) {
    query =>
      val prettifiedQuery = WhitespacePattern.matcher(query).replaceAll("_")
      if (prettifiedQuery == s"${engineName}_is_bad") {
        reject()
      } else {
        if (prettifiedQuery == s"${engineName}_is_slow") {
          Thread.sleep(5000)
        }
        val urls = (1 to 5).map { number =>
          s"host_number_$number.ru/did_you_know_about_$prettifiedQuery/from_$engineName"
        }
        val answer = SearchEngineResponse(status = 200, urls = urls)
        complete(write(answer))
      }
  }
}

object EngineHandler {
  val WhitespacePattern: Pattern = Pattern.compile("\\s+")
  implicit val Formats: Formats = Serialization.formats(NoTypeHints)
}
