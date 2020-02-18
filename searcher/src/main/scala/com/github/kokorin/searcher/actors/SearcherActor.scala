package com.github.kokorin.searcher.actors

import java.util.concurrent.{Executors, Future}

import akka.actor.Actor
import com.github.kokorin.searcher.config.SearchEngineConfig
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
import SearcherActor.{Formats, RequestToSearchEngineMessage}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

class SearcherActor extends Actor with StrictLogging {
  var httpClient: Option[CloseableHttpAsyncClient] = None
  var responseFuture: Option[Future[HttpResponse]] = None
  var curSearchEngineName: String = "not_initialized_search_engine"

  override def receive: Receive = {
    case RequestToSearchEngineMessage(request, searchEngine) =>
      curSearchEngineName = searchEngine.name
      val asyncHttpClient = HttpAsyncClients.createDefault() // TODO: use abstraction (and mock it for tests)
      httpClient = Some(asyncHttpClient)
      Try {
        asyncHttpClient.start()
        asyncHttpClient.execute(
          new HttpGet(
            s"http://${searchEngine.host}:${searchEngine.port}/search?query=$request"
          ),
          getCallback(searchEngine.name)
        )
      } match {
        case Failure(exception) =>
          logger.error(
            s"Error while requesting ${searchEngine.name}",
            exception
          )
          sendEmptyResponse(searchEngine.name)
          context.stop(self)
        case Success(value) =>
          responseFuture = Some(value)
      }
  }

  private def sendEmptyResponse(searchEngineName: String): Unit = {
    context.parent ! AggregatorActor.SearcherResponseMessage(
      searchEngineName,
      SearchEngineResponse(SearchEngineResponse.ERR, Seq())
    )
  }

  private def onResponseReceiving(): Unit = {
    responseFuture = None
    context.stop(self)
  }

  private def getCallback(searchEngineName: String) = {
    new FutureCallback[HttpResponse] {
      override def completed(t: HttpResponse): Unit = {
        logger.info(s"Received http response from $searchEngineName")
        try {
          val stringResponse = EntityUtils.toString(t.getEntity)
          val response = read[SearchEngineResponse](stringResponse)
          context.parent ! AggregatorActor.SearcherResponseMessage(
            searchEngineName,
            response
          )
        } catch {
          case NonFatal(ex) =>
            logger.error(
              s"Error while parsing http response from $searchEngineName",
              ex
            )
            sendEmptyResponse(searchEngineName)
        } finally {
          onResponseReceiving()
        }
      }

      override def failed(e: Exception): Unit = {
        logger.error(
          s"Error while executing http request to $searchEngineName",
          e
        )
        sendEmptyResponse(searchEngineName)
        onResponseReceiving()
      }

      override def cancelled(): Unit = {
        logger.info(s"Http request to $searchEngineName was cancelled")
        onResponseReceiving()
      }
    }
  }

  override def postStop(): Unit = {
    logger.info(
      s"Search actor for $curSearchEngineName is stopping, resources are being released..."
    )
    responseFuture.foreach(_.cancel(true))
    try {
      httpClient.foreach(_.close())
    } catch {
      case NonFatal(ex) =>
        logger.error(
          s"Error while closing http client in search actor for $curSearchEngineName",
          ex
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
