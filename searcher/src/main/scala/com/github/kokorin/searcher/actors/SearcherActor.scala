package com.github.kokorin.searcher.actors

import java.util.concurrent.{Executors, Future}

import akka.actor.Actor
import com.github.kokorin.searcher.config.SearchEngineConfig
import com.github.kokorin.searcher.model.SearchEngineResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.concurrent.FutureCallback
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.read
import SearcherActor.{Formats, RequestToSearchEngineMessage}
import com.github.kokorin.searcher.web.http.{
  AsyncHTTPClient,
  AsyncHTTPClientsProvider
}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

class SearcherActor(clientProvider: AsyncHTTPClientsProvider)
    extends Actor
    with StrictLogging {
  var httpClient: Option[AsyncHTTPClient] = None
  var responseFuture: Option[Future[_]] = None
  var curSearchEngineName: String = "not_initialized_search_engine"

  override def receive: Receive = {
    case RequestToSearchEngineMessage(request, searchEngine) =>
      curSearchEngineName = searchEngine.name
      val asyncHttpClient = clientProvider.newAsyncClient
      httpClient = Some(asyncHttpClient)
      Try {
        asyncHttpClient.start()
        asyncHttpClient.execute(
          new HttpGet(
            s"http://${searchEngine.host}:${searchEngine.port}/search?query=$request"
          ),
          getCallback
        )
      } match {
        case Failure(exception) =>
          logger.error(
            s"Error while requesting ${searchEngine.name}",
            exception
          )
          sendEmptyResponse()
          context.stop(self)
        case Success(value) =>
          responseFuture = Some(value)
      }
  }

  private def sendEmptyResponse(): Unit = {
    context.parent ! AggregatorActor.SearcherResponseMessage(
      curSearchEngineName,
      SearchEngineResponse(SearchEngineResponse.ERR, Seq())
    )
  }

  private def onResponseReceiving(): Unit = {
    responseFuture = None
    context.stop(self)
  }

  private def getCallback: FutureCallback[String] = {
    new FutureCallback[String] {
      override def completed(stringResponse: String): Unit = {
        logger.info(s"Received HTTP response from $curSearchEngineName")
        try {
          val response = read[SearchEngineResponse](stringResponse)
          context.parent ! AggregatorActor.SearcherResponseMessage(
            curSearchEngineName,
            response
          )
        } catch {
          case NonFatal(ex) =>
            logger.error(
              s"Error while parsing HTTP response from $curSearchEngineName",
              ex
            )
            sendEmptyResponse()
        } finally {
          onResponseReceiving()
        }
      }

      override def failed(e: Exception): Unit = {
        logger.error(
          s"Error while executing HTTP request to $curSearchEngineName",
          e
        )
        sendEmptyResponse()
        onResponseReceiving()
      }

      override def cancelled(): Unit = {
        logger.info(s"HTTP request to $curSearchEngineName was cancelled")
        onResponseReceiving()
      }
    }
  }

  override def postStop(): Unit = {
    logger.info(
      s"$curSearchEngineName search actor is stopping, resources are being released..."
    )
    responseFuture.foreach(_.cancel(true))
    Try {
      httpClient.foreach(_.close())
    } match {
      case Failure(exception) =>
        logger.error(
          s"Error while closing HTTP client in $curSearchEngineName search actor",
          exception
        )
      case Success(_) =>
        logger.info(
          s"HTTP client in $curSearchEngineName search actor has been closed successfully"
        )
    }
  }
}

object SearcherActor {
  implicit val Formats: Formats = Serialization.formats(NoTypeHints)

  implicit val ThreadPool: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  case class RequestToSearchEngineMessage(request: String,
                                          searchEngine: SearchEngineConfig)
}
