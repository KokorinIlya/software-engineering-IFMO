package com.github.kokorin.searcher.actors

import akka.actor.{Actor, ActorRef}
import com.github.kokorin.searcher.config.{
  SearchEngineConfig,
  SearcherActorConfig
}
import com.github.kokorin.searcher.model.SearchEngineResponse
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.util.EntityUtils
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.read
import SearcherActor.{Formats, SearchRequestMessage, SearchResponseMessage}

class SearcherActor(searcherActorConfig: SearcherActorConfig) extends Actor {
  var answerMailbox: ActorRef = _
  override def receive: Receive = {
    case SearchRequestMessage(request, searchEngine) =>
      answerMailbox = context.sender()
      val httpClient = HttpAsyncClients.createDefault()
      httpClient.start()
      val fut = httpClient.execute(
        new HttpGet(
          s"http://${searchEngine.host}:${searchEngine.port}/search?query=$request"
        ),
        new FutureCallback[HttpResponse] {
          override def completed(t: HttpResponse): Unit = {
            val stringResponse = EntityUtils.toString(t.getEntity)
            val response = read[SearchEngineResponse](stringResponse)
            self ! SearchResponseMessage(response, searchEngine)
          }

          override def failed(e: Exception): Unit = {
            self ! SearchResponseMessage(
              SearchEngineResponse(status = 404, urls = Seq()),
              searchEngine
            )
          }

          override def cancelled(): Unit = {
            self ! SearchResponseMessage(
              SearchEngineResponse(status = 404, urls = Seq()),
              searchEngine
            )
          }
        }
      )
      context.become(awaitingResponse)
  }

  private def awaitingResponse: Receive = {
    case SearchResponseMessage(response, engine) =>
      answerMailbox ! AggregatorActor.SearcherResponseMessage(
        engine.name,
        response
      )
  }
}

object SearcherActor {
  implicit val Formats: Formats = Serialization.formats(NoTypeHints)

  sealed trait SearcherActorMessage

  case class SearchRequestMessage(request: String,
                                  searchEngine: SearchEngineConfig)

  case class SearchResponseMessage(response: SearchEngineResponse,
                                   searchEngine: SearchEngineConfig)

}
