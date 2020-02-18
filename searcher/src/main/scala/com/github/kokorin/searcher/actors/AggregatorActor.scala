package com.github.kokorin.searcher.actors

import java.util.concurrent.Executors

import akka.actor.{Actor, Cancellable, Props}
import com.github.kokorin.searcher.config.{
  AggregatorActorConfig,
  SearchEngineConfig
}
import com.github.kokorin.searcher.model.{
  AggregatedSearchResponse,
  SearchEngineResponse
}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Promise}
import AggregatorActor._
import com.github.kokorin.searcher.web.http.AsyncHTTPClientsProvider

class AggregatorActor(promise: Promise[AggregatedSearchResponse],
                      aggregatorActorConfig: AggregatorActorConfig,
                      clientProvider: AsyncHTTPClientsProvider)
    extends Actor
    with StrictLogging {

  var response: scala.collection.mutable.Map[String, SearchEngineResponse] =
    scala.collection.mutable.Map.empty

  override def receive: Receive = {
    case SearchQueryMessage(query, engines) =>
      for { curEngine <- engines } {
        val curSearchActor =
          context.actorOf(Props(classOf[SearcherActor], clientProvider))
        curSearchActor ! SearcherActor.RequestToSearchEngineMessage(
          query,
          curEngine
        )
      }
      val answersToWait = engines.size
      val stopMessageSender =
        context.system.scheduler.scheduleOnce(aggregatorActorConfig.timeout) {
          self ! TimeoutMessage
        }
      context.become(awaitingResponses(answersToWait, stopMessageSender))
  }

  override def postStop(): Unit = {
    logger.info(s"Aggregator actor is stopping...")
    promise.success(AggregatedSearchResponse(response.toMap))
  }

  private def awaitingResponses(answersToWait: Int,
                                stopMessageSender: Cancellable): Receive = {
    case TimeoutMessage =>
      context.stop(self)
    case SearcherResponseMessage(engineName, engineResponse) =>
      response(engineName) = engineResponse
      if (response.size == answersToWait) {
        stopMessageSender.cancel()
        context.stop(self)
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

  case object TimeoutMessage extends AggregatorActorMessage
}
