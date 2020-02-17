package com.github.kokorin.searcher.web.api

import java.util.concurrent.Executors

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.kokorin.searcher.web.Handler

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Promise}
import ApiHandler.{Formats, SearchAggregatorActorSystem, ThreadPool}
import com.github.kokorin.searcher.actors.AggregatorActor
import com.github.kokorin.searcher.config.{
  ApplicationConfig,
  SearchEngineConfig
}
import com.github.kokorin.searcher.model.AggregatedSearchResponse
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write

class ApiHandler(applicationConfig: ApplicationConfig) extends Handler {
  private def pingRoute: Route = (path("search") & get & parameter("query")) {
    query =>
      val promise: Promise[AggregatedSearchResponse] = Promise()
      val actor = SearchAggregatorActorSystem.actorOf(
        Props(
          classOf[AggregatorActor],
          promise,
          applicationConfig.aggregatorActorConfig,
          applicationConfig.searcherActorConfig
        )
      )
      actor ! AggregatorActor
        .SearchQueryMessage(query, applicationConfig.searchEngines)
      complete(promise.future.map(write[AggregatedSearchResponse]))
  }

  override def route: Route = pingRoute
}

object ApiHandler {
  val SearchAggregatorActorSystem = ActorSystem("search-aggregator")
  implicit val Formats: Formats = Serialization.formats(NoTypeHints)
  implicit val ThreadPool: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(4))
}
