package com.github.kokorin.searcher.actors

import java.util.concurrent.{Executors, Future}

import akka.actor.{Actor, ActorRef, Cancellable}
import com.github.kokorin.searcher.config.{
  SearchEngineConfig,
  SearcherActorConfig
}
import com.github.kokorin.searcher.model.SearchEngineResponse
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.{
  CloseableHttpAsyncClient,
  HttpAsyncClients
}
import org.apache.http.util.EntityUtils
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.read
import SearcherActor.{
  Formats,
  RequestToSearchEngineMessage,
  ResponseFromSearchEngine,
  ThreadPool,
  TimeoutMessage
}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

class SearcherActor(searcherActorConfig: SearcherActorConfig)
    extends Actor
    with StrictLogging {
  // TODO: functional style
  var answerMailbox: ActorRef = _
  var httpClient: CloseableHttpAsyncClient = _
  var responseFuture: Future[HttpResponse] = _
  var timeoutMessageSender: Cancellable = _
  var searchEngineName: String = _

  override def receive: Receive = {
    case RequestToSearchEngineMessage(request, searchEngine) =>
      searchEngineName = searchEngine.name
      answerMailbox = context.sender()
      httpClient = HttpAsyncClients.createDefault()
      Try {
        httpClient.start()
        val getRequest = new HttpGet(
          s"http://${searchEngine.host}:${searchEngine.port}/search?query=$request"
        )
        httpClient.execute( // TODO: use abstraction (so it can be mocked)
          getRequest,
          new FutureCallback[HttpResponse] {
            override def completed(t: HttpResponse): Unit = {
              logger.info(s"Received http response from $searchEngineName")
              try {
                val stringResponse = EntityUtils.toString(t.getEntity)
                val response = read[SearchEngineResponse](stringResponse)
                self ! ResponseFromSearchEngine(response, searchEngine.name)
              } catch {
                case NonFatal(ex) =>
                  logger.error(
                    s"Error while parsing http response from $searchEngineName",
                    ex
                  )
                  self ! ResponseFromSearchEngine(
                    SearchEngineResponse(status = 500, urls = Seq()),
                    searchEngine.name
                  )
              }

            }

            override def failed(e: Exception): Unit = {
              logger.error(
                s"Error while executing http request to $searchEngineName",
                e
              )
              self ! ResponseFromSearchEngine(
                SearchEngineResponse(status = 500, urls = Seq()),
                searchEngine.name
              )
            }

            override def cancelled(): Unit = {
              logger.info(s"Http request to $searchEngineName was cancelled")
            }
          }
        )
      } match {
        case Failure(exception) =>
          logger.error(s"Error while requesting $searchEngineName", exception)
        case Success(value) =>
          responseFuture = value
          timeoutMessageSender =
            context.system.scheduler.scheduleOnce(searcherActorConfig.timeout) {
              self ! TimeoutMessage
            }
          context.become(awaitingResponse)
      }
  }

  private def stopActor(): Unit = {
    try {
      httpClient.close()
    } catch {
      case NonFatal(ex) => logger.error("Error while closing http client", ex)
    }
    context.stop(self)
  }

  private def awaitingResponse: Receive = {
    case ResponseFromSearchEngine(response, engineName) =>
      answerMailbox ! AggregatorActor.SearcherResponseMessage(
        engineName,
        response
      )
      timeoutMessageSender.cancel()
      stopActor()
    case TimeoutMessage =>
      logger.info(s"Timeout while waining response from $searchEngineName")
      responseFuture.cancel(true)
      stopActor()
  }
}

object SearcherActor {
  implicit val Formats: Formats = Serialization.formats(NoTypeHints)

  implicit val ThreadPool: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  sealed trait SearcherActorMessage

  case class RequestToSearchEngineMessage(request: String,
                                          searchEngine: SearchEngineConfig)
      extends SearcherActorMessage

  case class ResponseFromSearchEngine(response: SearchEngineResponse,
                                      searchEngineName: String)
      extends SearcherActorMessage

  case object TimeoutMessage extends SearcherActorMessage
}
