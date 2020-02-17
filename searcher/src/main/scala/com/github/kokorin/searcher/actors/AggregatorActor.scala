package com.github.kokorin.searcher.actors

import java.util.concurrent.Executors

import akka.actor.{Actor, Cancellable, Props}
import com.github.kokorin.searcher.config.{
  AggregatorActorConfig,
  SearchEngineConfig,
  SearcherActorConfig
}
import com.github.kokorin.searcher.model.{
  AggregatedSearchResponse,
  SearchEngineResponse
}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Promise}
import AggregatorActor._

class AggregatorActor(promise: Promise[AggregatedSearchResponse],
                      aggregatorActorConfig: AggregatorActorConfig,
                      searcherActorConfig: SearcherActorConfig)
    extends Actor
    with StrictLogging {
  var response: scala.collection.mutable.Map[String, SearchEngineResponse] =
    scala.collection.mutable.Map.empty

  var sizeToWait: Int = -1

  var stopMessageSender: Cancellable = _

  override def receive: Receive = {
    case SearchQueryMessage(query, engines) =>
      for { curEngine <- engines } {
        val curSearchActor =
          context.actorOf(Props(classOf[SearcherActor], searcherActorConfig))
        curSearchActor ! SearcherActor.SearchRequestMessage(query, curEngine)
      }
      sizeToWait = engines.size
      stopMessageSender =
        context.system.scheduler.scheduleOnce(aggregatorActorConfig.timeout) {
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
        stopMessageSender.cancel()
        stopActor()
      }
  }
}

object AggregatorActor {
  implicit val ThreadPool: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  sealed trait AggregatorActorMessage

  case class SearchQueryMessage(query: String, engines: Seq[SearchEngineConfig])
      extends AggregatorActorMessage

  case class SearcherResponseMessage(engineName: String,
                                     response: SearchEngineResponse)
      extends AggregatorActorMessage

  case object StopActorMessage
}
