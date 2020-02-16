package com.github.kokorin.searcher.actors

import java.util.concurrent.Executors

import akka.actor.{Actor, Props}
import com.github.kokorin.searcher.config.SearchEngineConfig
import com.github.kokorin.searcher.model.{
  AggregatedSearchResponse,
  SearchEngineResponse
}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Promise}
import AggregatorActor.ThreadPool

sealed trait AggregatorActorMessage

case class SearchQueryMessage(query: String, engines: Seq[SearchEngineConfig])
    extends AggregatorActorMessage

case class SearcherResponseMessage(engineName: String,
                                   response: SearchEngineResponse)
    extends AggregatorActorMessage

case object StopActorMessage

class AggregatorActor(promise: Promise[AggregatedSearchResponse])
    extends Actor
    with StrictLogging {
  var response: scala.collection.mutable.Map[String, SearchEngineResponse] =
    scala.collection.mutable.Map.empty

  var sizeToWait: Int = -1

  override def receive: Receive = {
    case SearchQueryMessage(query, engines) =>
      for { curEngine <- engines } {
        context.actorOf(Props[SearcherActor]) ! SearchRequestMessage(
          query,
          curEngine
        )
      }
      sizeToWait = engines.size
      context.system.scheduler.scheduleOnce(5.seconds) {
        self ! StopActorMessage
      }
      context.become(awaitingResponses)
  }

  private def stopActor(): Unit = {
    promise.success(AggregatedSearchResponse(response.toMap))
    context.stop(self)
  }

  private def awaitingResponses: Receive = {
    case StopActorMessage =>
      stopActor()
    case SearcherResponseMessage(engineName, engineResponse) =>
      response(engineName) = engineResponse
      if (response.size == sizeToWait) {
        stopActor()
      }
  }
}

object AggregatorActor {
  implicit val ThreadPool: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
}
