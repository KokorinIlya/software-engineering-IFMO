package com.github.kokorin.searcher.web.api

import java.util.concurrent.Executors

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.kokorin.searcher.web.Handler

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Promise}
import ApiHandler.{ThreadPool, formats, system}
import com.github.kokorin.searcher.actors.{AggregatorActor, SearchQueryMessage}
import com.github.kokorin.searcher.config.ApplicationConfig
import com.github.kokorin.searcher.model.AggregatedSearchResponse
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write

class ApiHandler(applicationConfig: ApplicationConfig) extends Handler {
  private def pingRoute: Route = (path("search") & get & parameter("query")) {
    query =>
      val promise: Promise[AggregatedSearchResponse] = Promise()
      val actor = system.actorOf(Props(classOf[AggregatorActor], promise))
      actor ! SearchQueryMessage(query, applicationConfig.searchEngines)
      complete(promise.future.map(write[AggregatedSearchResponse]))
  }

  override def route: Route = pingRoute
}

object ApiHandler {
  val system = ActorSystem("search_aggregator")
  implicit val formats: Formats = Serialization.formats(NoTypeHints)
  implicit val ThreadPool: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(4))
}
